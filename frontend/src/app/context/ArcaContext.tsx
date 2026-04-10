import React, { createContext, useContext, useState, useCallback } from 'react';
import { Credential, SyncLog, Device } from '@/types';
import { apiClient, EncryptedCredential } from '@/services/apiClient';
import { supabase } from '@/lib/supabaseClient';
import { deriveKeys, encryptFieldWithKey, decryptFieldWithKey, CRYPTO_VERSION } from '@/lib/crypto';

type SyncStatus = 'synced' | 'syncing' | 'pending' | 'offline' | 'error';
type ActiveScreen = 'login' | 'unlock' | 'vault' | 'sync-logs';

// ── Local-storage key for email (needed to re-derive vault key after reload) ──
const EMAIL_STORAGE_KEY = 'arca_user_email';

// ── Context type ───────────────────────────────────────────────────────────────

interface ArcaContextType {
  // Auth
  isAuthenticated: boolean;
  isVaultUnlocked: boolean;
  userEmail: string;
  username: string;
  avatarUrl: string | null;
  login: (email: string, password: string) => Promise<boolean>;
  unlock: (password: string) => Promise<boolean>;
  logout: () => void;
  updateProfile: (username: string, avatarUrl: string | null) => Promise<void>;

  // Vault
  credentials: Credential[];
  addCredential: (
    cred: Omit<Credential, 'id' | 'lastModified' | 'syncStatus' | 'offlineModified'>,
  ) => Promise<void>;
  updateCredential: (id: string, cred: Partial<Credential>) => Promise<void>;
  deleteCredential: (id: string) => Promise<void>;

  // Sync
  syncStatus: SyncStatus;
  syncLogs: SyncLog[];
  triggerSync: (deviceName?: string, deviceType?: string) => Promise<{ success: boolean }>;

  // Devices
  devices: Device[];
  getDevices: () => Promise<void>;

  // UI State
  activeScreen: ActiveScreen;
  setActiveScreen: (screen: ActiveScreen) => void;
  isOnline: boolean;
  setIsOnline: (online: boolean) => void;
}

const ArcaContext = createContext<ArcaContextType | null>(null);

// ── Helper: decrypt a server credential into the frontend Credential shape ─────

async function decryptCredential(
  enc: EncryptedCredential,
  vaultKey: CryptoKey,
): Promise<Credential> {
  let password = '';
  if (enc.cryptoVersion === CRYPTO_VERSION && enc.encryptedPassword && enc.iv) {
    password = await decryptFieldWithKey(enc.encryptedPassword, enc.iv, vaultKey);
  } else {
    // Legacy row with cryptoVersion 0 or missing fields — use as-is
    password = enc.encryptedPassword ?? '';
  }

  return {
    id: enc.id,
    siteName: enc.siteName,
    url: enc.url ?? '',
    username: enc.username,
    password,
    category: (enc.category as Credential['category']) ?? 'Other',
    notes: enc.notes,
    syncStatus: (enc.syncStatus as Credential['syncStatus']) ?? 'synced',
    offlineModified: enc.offlineModified ?? false,
    lastModified: enc.lastModified ?? new Date().toISOString(),
  };
}

// ── Provider ───────────────────────────────────────────────────────────────────

export function ArcaProvider({ children }: { children: React.ReactNode }) {
  const [isAuthenticated, setIsAuthenticated] = useState(false);
  const [isVaultUnlocked, setIsVaultUnlocked] = useState(false);
  const [userEmail, setUserEmail] = useState('');
  const [username, setUsername] = useState('');
  const [avatarUrl, setAvatarUrl] = useState<string | null>(null);
  const [credentials, setCredentials] = useState<Credential[]>([]);
  const [syncStatus, setSyncStatus] = useState<SyncStatus>('synced');
  const [syncLogs, setSyncLogs] = useState<SyncLog[]>([]);
  const [activeScreen, setActiveScreen] = useState<ActiveScreen>('login');
  const [isOnline, setIsOnline] = useState(true);
  const [devices, setDevices] = useState<Device[]>([]);

  // Vault key lives only in memory — never serialised, never sent anywhere.
  const [vaultKey, setVaultKey] = useState<CryptoKey | null>(null);

  // ── login ──────────────────────────────────────────────────────────────────

  const login = useCallback(async (email: string, password: string): Promise<boolean> => {
    try {
      // 1. Derive vault key + auth key from master password
      const { vaultKey: derivedKey, authKeyHex } = await deriveKeys(password, email);

      // 2. Authenticate with Supabase
      const { error: authError } = await supabase.auth.signInWithPassword({ email, password });
      if (authError) throw authError;

      // 3. Verify auth key with our backend (secondary check — proves knowledge of master password)
      const response = await apiClient.login(authKeyHex);
      if (!response.success) throw new Error('Invalid master password');

      // 4. Persist email for vault key re-derivation after page reload
      localStorage.setItem(EMAIL_STORAGE_KEY, email.toLowerCase().trim());

      setVaultKey(derivedKey);
      setIsAuthenticated(true);
      setUserEmail(email);
      setUsername(response.username || email.split('@')[0]);
      setAvatarUrl(response.avatarUrl || null);
      
      // Fetch initial data
      getDevices();
      
      return true;
    } catch (error) {
      console.error('Login error:', error);
      return false;
    }
  }, []);

  // ── unlock ─────────────────────────────────────────────────────────────────

  const unlock = useCallback(
    async (password: string): Promise<boolean> => {
      try {
        // Email is needed to re-derive the vault key (same salt as during registration)
        const email =
          userEmail ||
          localStorage.getItem(EMAIL_STORAGE_KEY) ||
          (await supabase.auth.getSession()).data.session?.user?.email ||
          '';

        if (!email) return false;

        // 1. Re-derive keys — this is the password verification (wrong password → wrong key → decrypt fails)
        const { vaultKey: derivedKey, authKeyHex } = await deriveKeys(password, email);

        // 2. Verify auth key with backend + fetch encrypted credentials
        const response = await apiClient.unlockVault(authKeyHex);
        if (!response.success) return false;

        // 3. Decrypt each credential client-side
        const decryptedCredentials = await Promise.all(
          (response.data ?? []).map((cred) => decryptCredential(cred, derivedKey)),
        );

        setVaultKey(derivedKey);
        setIsVaultUnlocked(true);
        setCredentials(decryptedCredentials);
        return true;
      } catch (error) {
        // AES-GCM throws if the key is wrong — this is the "incorrect password" path
        console.error('Unlock failed:', error);
        return false;
      }
    },
    [userEmail],
  );

  // ── logout ─────────────────────────────────────────────────────────────────

  const logout = useCallback(async () => {
    try {
      await supabase.auth.signOut();
    } catch (error) {
      console.error('Logout error:', error);
    }
    setIsAuthenticated(false);
    setIsVaultUnlocked(false);
    setUserEmail('');
    setVaultKey(null);
    setCredentials([]);
    setSyncLogs([]);
    setActiveScreen('login');
    // Note: intentionally keep EMAIL_STORAGE_KEY so the unlock screen can
    // still derive the key on next visit. Clear on explicit "forget me" if desired.
  }, []);

  // ── updateProfile ──────────────────────────────────────────────────────────

  const updateProfile = useCallback(async (newUsername: string, newAvatarUrl: string | null) => {
    const response = await apiClient.updateProfile(newUsername, newAvatarUrl);
    if (response.success) {
      setUsername(newUsername);
      setAvatarUrl(newAvatarUrl);
    }
  }, []);

  // ── addCredential ──────────────────────────────────────────────────────────

  const addCredential = useCallback(
    async (cred: Omit<Credential, 'id' | 'lastModified' | 'syncStatus' | 'offlineModified'>) => {
      if (!vaultKey) throw new Error('Vault is locked');

      const { ciphertext, iv } = await encryptFieldWithKey(cred.password, vaultKey);

      const saved = await apiClient.createCredential({
        siteName: cred.siteName,
        url: cred.url,
        username: cred.username,
        encryptedPassword: ciphertext,
        iv,
        cryptoVersion: CRYPTO_VERSION,
        category: cred.category,
        notes: cred.notes,
      });

      // Re-use the plaintext password we already have — no need to decrypt the response
      const newCred: Credential = {
        id: saved.id,
        siteName: saved.siteName,
        url: saved.url ?? '',
        username: saved.username,
        password: cred.password, // plaintext we encrypted above
        category: (saved.category as Credential['category']) ?? cred.category,
        notes: saved.notes,
        syncStatus: (saved.syncStatus as Credential['syncStatus']) ?? 'synced',
        offlineModified: saved.offlineModified ?? false,
        lastModified: saved.lastModified ?? new Date().toISOString(),
      };

      setCredentials((prev) => [newCred, ...prev]);
    },
    [vaultKey],
  );

  // ── updateCredential ───────────────────────────────────────────────────────

  const updateCredential = useCallback(
    async (id: string, updates: Partial<Credential>) => {
      if (!vaultKey) throw new Error('Vault is locked');

      // Find existing so we can re-encrypt any changed fields
      const existing = credentials.find((c) => c.id === id);
      const plainPassword = updates.password ?? existing?.password ?? '';

      const { ciphertext, iv } = await encryptFieldWithKey(plainPassword, vaultKey);

      const merged = { ...existing, ...updates };

      const saved = await apiClient.updateCredential(id, {
        siteName: merged.siteName ?? '',
        url: merged.url,
        username: merged.username ?? '',
        encryptedPassword: ciphertext,
        iv,
        cryptoVersion: CRYPTO_VERSION,
        category: merged.category ?? 'Other',
        notes: merged.notes,
      });

      const updatedCred: Credential = {
        id: saved.id,
        siteName: saved.siteName,
        url: saved.url ?? '',
        username: saved.username,
        password: plainPassword,
        category: (saved.category as Credential['category']) ?? 'Other',
        notes: saved.notes,
        syncStatus: (saved.syncStatus as Credential['syncStatus']) ?? 'synced',
        offlineModified: saved.offlineModified ?? false,
        lastModified: saved.lastModified ?? new Date().toISOString(),
      };

      setCredentials((prev) => prev.map((c) => (c.id === id ? updatedCred : c)));
    },
    [vaultKey, credentials],
  );

  // ── deleteCredential ───────────────────────────────────────────────────────

  const deleteCredential = useCallback(async (id: string) => {
    await apiClient.deleteCredential(id);
    setCredentials((prev) => prev.filter((c) => c.id !== id));
  }, []);

  // ── triggerSync ────────────────────────────────────────────────────────────

  const triggerSync = useCallback(async (deviceName = 'Web Browser', deviceType = 'browser') => {
    try {
      setSyncStatus('syncing');
      const response = await apiClient.triggerSync(deviceName, deviceType);
      if (response.success) {
        const logsResponse = await apiClient.getSyncLogs();
        if (logsResponse.success && logsResponse.data) {
          setSyncLogs(logsResponse.data);
        }
        setSyncStatus('synced');
        return { success: true };
      } else {
        setSyncStatus('error');
        return { success: false };
      }
    } catch {
      setSyncStatus('error');
      return { success: false };
    }
  }, []);

  // ── devices ─────────────────────────────────────────────────────────────────

  const getDevices = useCallback(async () => {
    try {
      const response = await apiClient.getDevices();
      if (response.success && response.data) {
        setDevices(response.data);
      }
    } catch (err) {
      console.error('Failed to fetch devices', err);
    }
  }, []);

  // ── Provider value ─────────────────────────────────────────────────────────

  return (
    <ArcaContext.Provider
      value={{
        isAuthenticated,
        isVaultUnlocked,
        userEmail,
        username,
        avatarUrl,
        login,
        unlock,
        logout,
        updateProfile,
        credentials,
        addCredential,
        updateCredential,
        deleteCredential,
        syncStatus,
        syncLogs,
        triggerSync,
        activeScreen,
        setActiveScreen,
        isOnline,
        setIsOnline,
        devices,
        getDevices,
      }}
    >
      {children}
    </ArcaContext.Provider>
  );
}

export function useArca() {
  const ctx = useContext(ArcaContext);
  if (!ctx) throw new Error('useArca must be used within ArcaProvider');
  return ctx;
}
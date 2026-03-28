import React, { createContext, useContext, useState, useCallback } from 'react';
import { Credential, SyncLog } from '../data/mockData';
import { apiClient } from '@/services/apiClient';
import { supabase } from '@/lib/supabaseClient';

type SyncStatus = 'synced' | 'syncing' | 'pending' | 'offline' | 'error';
type ActiveScreen = 'login' | 'unlock' | 'vault' | 'sync-logs';

interface ArcaContextType {
  // Auth
  isAuthenticated: boolean;
  isVaultUnlocked: boolean;
  userEmail: string;
  username: string;
  avatarUrl: string | null;
  login: (email: string, password: string, masterPassword: string) => Promise<boolean>;
  unlock: (masterPassword: string) => Promise<boolean>;
  logout: () => void;
  updateProfile: (username: string, avatarUrl: string | null) => Promise<void>;

  // Vault
  credentials: Credential[];
  addCredential: (cred: Omit<Credential, 'id' | 'lastModified' | 'syncStatus' | 'offlineModified'>, masterPassword: string) => Promise<void>;
  updateCredential: (id: string, cred: Partial<Credential>, masterPassword: string) => Promise<void>;
  deleteCredential: (id: string) => Promise<void>;

  // Sync
  syncStatus: SyncStatus;
  syncLogs: SyncLog[];
  triggerSync: () => Promise<void>;

  // UI State
  activeScreen: ActiveScreen;
  setActiveScreen: (screen: ActiveScreen) => void;
  isOnline: boolean;
  setIsOnline: (online: boolean) => void;
  
  // Store master password in session (for API calls)
  masterPassword: string | null;
  setMasterPassword: (pwd: string | null) => void;
}

const ArcaContext = createContext<ArcaContextType | null>(null);

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
  const [masterPassword, setMasterPassword] = useState<string | null>(null);

  const login = useCallback(async (email: string, password: string, masterPwd: string): Promise<boolean> => {
    try {
      // Authenticate with Supabase
      const { error: authError } = await supabase.auth.signInWithPassword({ email, password });
      if (authError) throw authError;

      // Validate master password with backend
      const response = await apiClient.login(masterPwd);
      if (!response.success) throw new Error('Invalid master password');

      setIsAuthenticated(true);
      setUserEmail(email);
      setMasterPassword(masterPwd);
      return true;
    } catch (error) {
      console.error('Login failed:', error);
      return false;
    }
  }, []);

  const unlock = useCallback(async (masterPwd: string): Promise<boolean> => {
    try {
      const response = await apiClient.unlockVault(masterPwd);
      if (!response.success) return false;

      setIsVaultUnlocked(true);
      setMasterPassword(masterPwd);

      // Fetch credentials after unlock
      const credsResponse = await apiClient.getCredentials(masterPwd);
      if (credsResponse.success && credsResponse.data) {
        setCredentials(credsResponse.data);
      }

      return true;
    } catch (error) {
      console.error('Unlock failed:', error);
      return false;
    }
  }, []);

  const logout = useCallback(async () => {
    try {
      await supabase.auth.signOut();
    } catch (error) {
      console.error('Logout error:', error);
    }
    setIsAuthenticated(false);
    setIsVaultUnlocked(false);
    setUserEmail('');
    setMasterPassword(null);
    setCredentials([]);
    setSyncLogs([]);
    setActiveScreen('login');
  }, []);

  const updateProfile = useCallback(async (newUsername: string, newAvatarUrl: string | null) => {
    try {
      const response = await apiClient.updateProfile(newUsername, newAvatarUrl);
      if (response.success) {
        setUsername(newUsername);
        setAvatarUrl(newAvatarUrl);
      }
    } catch (error) {
      console.error('Profile update failed:', error);
      throw error;
    }
  }, []);

  const addCredential = useCallback(async (cred: Omit<Credential, 'id' | 'lastModified' | 'syncStatus' | 'offlineModified'>, masterPwd: string) => {
    try {
      const response = await apiClient.createCredential(cred, masterPwd);
      if (response.success && response.data) {
        setCredentials(prev => [response.data, ...prev]);
      }
    } catch (error) {
      console.error('Add credential failed:', error);
      throw error;
    }
  }, []);

  const updateCredential = useCallback(async (id: string, updates: Partial<Credential>, masterPwd: string) => {
    try {
      const response = await apiClient.updateCredential(id, updates, masterPwd);
      if (response.success && response.data) {
        setCredentials(prev => prev.map(c => c.id === id ? response.data : c));
      }
    } catch (error) {
      console.error('Update credential failed:', error);
      throw error;
    }
  }, []);

  const deleteCredential = useCallback(async (id: string) => {
    try {
      const response = await apiClient.deleteCredential(id);
      if (response.success) {
        setCredentials(prev => prev.filter(c => c.id !== id));
      }
    } catch (error) {
      console.error('Delete credential failed:', error);
      throw error;
    }
  }, []);

  const triggerSync = useCallback(async () => {
    try {
      setSyncStatus('syncing');
      const response = await apiClient.triggerSync('Web Client', 'desktop');
      
      if (response.success) {
        // Fetch updated sync logs
        const logsResponse = await apiClient.getSyncLogs();
        if (logsResponse.success && logsResponse.data) {
          setSyncLogs(logsResponse.data);
        }
        setSyncStatus('synced');
      } else {
        setSyncStatus('error');
      }
    } catch (error) {
      console.error('Sync failed:', error);
      setSyncStatus('error');
    }
  }, []);

  return (
    <ArcaContext.Provider value={{
      isAuthenticated, isVaultUnlocked, userEmail, username, avatarUrl,
      login, unlock, logout, updateProfile,
      credentials, addCredential, updateCredential, deleteCredential,
      syncStatus, syncLogs, triggerSync,
      activeScreen, setActiveScreen,
      isOnline, setIsOnline,
      masterPassword, setMasterPassword,
    }}>
      {children}
    </ArcaContext.Provider>
  );
}

export function useArca() {
  const ctx = useContext(ArcaContext);
  if (!ctx) throw new Error('useArca must be used within ArcaProvider');
  return ctx;
}
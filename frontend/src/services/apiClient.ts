import { supabase } from '@/lib/supabaseClient';

const API_BASE = import.meta.env.VITE_API_URL || 'http://localhost:8080/api';

// ── Types ──────────────────────────────────────────────────────────────────────

/** Shape of a credential as returned from the server (password is ciphertext). */
export interface EncryptedCredential {
  id: string;
  siteName: string;
  url?: string;
  username: string;
  encryptedPassword: string; // AES-256-GCM ciphertext (base64)
  iv: string;                // AES-GCM IV (base64)
  cryptoVersion: number;
  category: string;
  notes?: string;
  syncStatus: string;
  offlineModified: boolean;
  lastModified: string;
  versionNumber?: number;
}

/** Shape of a credential payload sent to the server when creating/updating. */
export interface CredentialPayload {
  siteName: string;
  url?: string;
  username: string;
  encryptedPassword: string;
  iv: string;
  cryptoVersion: number;
  category: string;
  notes?: string;
}

// ── Auth headers ───────────────────────────────────────────────────────────────

async function getAuthHeaders(authKeyHex?: string): Promise<Record<string, string>> {
  const {
    data: { session },
  } = await supabase.auth.getSession();

  if (!session) {
    throw new Error('No active session');
  }

  const headers: Record<string, string> = {
    'Content-Type': 'application/json',
    Authorization: `Bearer ${session.access_token}`,
  };

  if (authKeyHex) {
    headers['X-Auth-Key'] = authKeyHex;
  }

  return headers;
}

// ── API client ─────────────────────────────────────────────────────────────────

export const apiClient = {
  // ──────────────────── AUTH ────────────────────────────────────────────────

  /**
   * Register a new user on the backend.
   * Sends the derived auth key hex (NOT the raw master password).
   */
  async register(email: string, authKeyHex: string, supabaseUserId: string) {
    const response = await fetch(`${API_BASE}/auth/register`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ email, authKeyHex, supabaseUserId }),
    });
    const data = await response.json();
    if (!response.ok) {
      throw new Error(data.message || data.error || 'Registration failed');
    }
    return data;
  },

  /**
   * Verify the user's auth key with the backend.
   * Supabase JWT is still required (identifies the user); authKeyHex is the
   * secondary proof that the user knows the master password.
   */
  async login(authKeyHex: string) {
    const headers = await getAuthHeaders(authKeyHex);
    const response = await fetch(`${API_BASE}/auth/login`, {
      method: 'POST',
      headers,
    });

    const text = await response.text();
    const data = text ? JSON.parse(text) : null;

    if (!response.ok) {
      throw new Error(data?.error || response.statusText || 'Login failed');
    }
    return data;
  },

  // ──────────────────── VAULT ───────────────────────────────────────────────

  /**
   * Unlock the vault: verify authKeyHex server-side, then return encrypted
   * credential blobs. Decryption happens on the frontend using the vault key.
   */
  async unlockVault(authKeyHex: string): Promise<{ success: boolean; data?: EncryptedCredential[] }> {
    const headers = await getAuthHeaders(authKeyHex);
    const response = await fetch(`${API_BASE}/vault/unlock`, {
      method: 'POST',
      headers,
    });
    const data = await response.json();
    if (!response.ok) {
      throw new Error(data.error || data.message || 'Vault unlock failed');
    }
    return data;
  },

  /**
   * Fetch all encrypted credentials for the authenticated user.
   * The caller is responsible for decrypting the passwords.
   */
  async getCredentials(): Promise<EncryptedCredential[]> {
    const headers = await getAuthHeaders();
    const response = await fetch(`${API_BASE}/vault/credentials`, {
      method: 'GET',
      headers,
    });
    const data = await response.json();
    if (!response.ok) {
      throw new Error(data.message || 'Failed to fetch credentials');
    }
    return data;
  },

  /**
   * Create a credential. The password must already be encrypted by the caller.
   */
  async createCredential(credential: CredentialPayload): Promise<EncryptedCredential> {
    const headers = await getAuthHeaders();
    const response = await fetch(`${API_BASE}/vault/credentials`, {
      method: 'POST',
      headers,
      body: JSON.stringify(credential),
    });
    const data = await response.json();
    if (!response.ok) {
      throw new Error(data.message || 'Failed to create credential');
    }
    return data;
  },

  /**
   * Update a credential. The password must already be encrypted by the caller.
   */
  async updateCredential(id: string, credential: CredentialPayload): Promise<EncryptedCredential> {
    const headers = await getAuthHeaders();
    const response = await fetch(`${API_BASE}/vault/credentials/${id}`, {
      method: 'PUT',
      headers,
      body: JSON.stringify(credential),
    });
    const data = await response.json();
    if (!response.ok) {
      throw new Error(data.message || 'Failed to update credential');
    }
    return data;
  },

  async deleteCredential(id: string) {
    const headers = await getAuthHeaders();
    const response = await fetch(`${API_BASE}/vault/credentials/${id}`, {
      method: 'DELETE',
      headers,
    });
    const data = await response.json();
    if (!response.ok) {
      throw new Error(data.message || 'Failed to delete credential');
    }
    return data;
  },

  // ──────────────────── SYNC ────────────────────────────────────────────────

  async getSyncLogs() {
    const headers = await getAuthHeaders();
    const response = await fetch(`${API_BASE}/sync/logs`, {
      method: 'GET',
      headers,
    });
    const data = await response.json();
    if (!response.ok) {
      throw new Error(data.message || 'Failed to fetch sync logs');
    }
    return data;
  },

  async triggerSync(deviceName = 'Web Client', deviceType = 'desktop') {
    const headers = await getAuthHeaders();
    const params = new URLSearchParams({ deviceName, deviceType });
    const response = await fetch(`${API_BASE}/sync/trigger?${params}`, {
      method: 'POST',
      headers,
    });
    const data = await response.json();
    if (!response.ok) {
      throw new Error(data.message || 'Failed to trigger sync');
    }
    return data;
  },

  // ──────────────────── USER ────────────────────────────────────────────────

  async getProfile() {
    const headers = await getAuthHeaders();
    const response = await fetch(`${API_BASE}/user/profile`, {
      method: 'GET',
      headers,
    });
    const data = await response.json();
    if (!response.ok) {
      throw new Error(data.message || 'Failed to fetch profile');
    }
    return data;
  },

  async updateProfile(username: string, avatarUrl?: string | null) {
    const headers = await getAuthHeaders();
    const response = await fetch(`${API_BASE}/user/profile`, {
      method: 'PUT',
      headers,
      body: JSON.stringify({ username, avatarUrl }),
    });
    const data = await response.json();
    if (!response.ok) {
      throw new Error(data.message || 'Failed to update profile');
    }
    return data;
  },

  // ──────────────────── DEVICES ───────────────────────────────────────────────

  async getDevices() {
    const headers = await getAuthHeaders();
    const response = await fetch(`${API_BASE}/user/devices`, {
      method: 'GET',
      headers,
    });
    const data = await response.json();
    if (!response.ok) {
      throw new Error(data.message || 'Failed to fetch devices');
    }
    return data;
  },
};

import { supabase } from '@/lib/supabaseClient';

const API_BASE = import.meta.env.VITE_API_URL || 'http://localhost:8080/api';

async function getAuthHeaders(masterPassword?: string): Promise<Record<string, string>> {
  const { data: { session } } = await supabase.auth.getSession();
  
  if (!session) {
    throw new Error('No active session');
  }

  const headers: Record<string, string> = {
    'Content-Type': 'application/json',
    'Authorization': `Bearer ${session.access_token}`
  };

  if (masterPassword) {
    headers['masterPassword'] = masterPassword;
  }

  return headers;
}

export const apiClient = {
  // ============== AUTH ==============
  
  async register(email: string, masterPassword: string, supabaseUserId?: string) {
    const response = await fetch(`${API_BASE}/auth/register`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ email, masterPassword, supabaseUserId })
    });
    const data = await response.json();
    if (!response.ok) {
      throw new Error(data.message || 'Registration failed');
    }
    return data;
  },

  async login(masterPassword: string) {
    try {
      const headers = await getAuthHeaders();
      const response = await fetch(`${API_BASE}/auth/login`, {
        method: 'POST',
        headers,
        body: JSON.stringify({ masterPassword })
      });
      const data = await response.json();
      if (!response.ok) {
        throw new Error(data.message || 'Login failed');
      }
      return data;
    } catch (error) {
      throw error;
    }
  },

  // ============== VAULT ==============

  async unlockVault(masterPassword: string) {
    try {
      const headers = await getAuthHeaders();
      const response = await fetch(`${API_BASE}/vault/unlock`, {
        method: 'POST',
        headers,
        body: JSON.stringify({ masterPassword })
      });
      const data = await response.json();
      if (!response.ok) {
        throw new Error(data.message || 'Vault unlock failed');
      }
      return data;
    } catch (error) {
      throw error;
    }
  },

  async getCredentials(masterPassword: string) {
    try {
      const headers = await getAuthHeaders(masterPassword);
      const response = await fetch(`${API_BASE}/vault/credentials`, {
        method: 'GET',
        headers
      });
      const data = await response.json();
      if (!response.ok) {
        throw new Error(data.message || 'Failed to fetch credentials');
      }
      return data;
    } catch (error) {
      throw error;
    }
  },

  async createCredential(credential: any, masterPassword: string) {
    try {
      const headers = await getAuthHeaders(masterPassword);
      const response = await fetch(`${API_BASE}/vault/credentials`, {
        method: 'POST',
        headers,
        body: JSON.stringify(credential)
      });
      const data = await response.json();
      if (!response.ok) {
        throw new Error(data.message || 'Failed to create credential');
      }
      return data;
    } catch (error) {
      throw error;
    }
  },

  async updateCredential(id: string, credential: any, masterPassword: string) {
    try {
      const headers = await getAuthHeaders(masterPassword);
      const response = await fetch(`${API_BASE}/vault/credentials/${id}`, {
        method: 'PUT',
        headers,
        body: JSON.stringify(credential)
      });
      const data = await response.json();
      if (!response.ok) {
        throw new Error(data.message || 'Failed to update credential');
      }
      return data;
    } catch (error) {
      throw error;
    }
  },

  async deleteCredential(id: string) {
    try {
      const headers = await getAuthHeaders();
      const response = await fetch(`${API_BASE}/vault/credentials/${id}`, {
        method: 'DELETE',
        headers
      });
      const data = await response.json();
      if (!response.ok) {
        throw new Error(data.message || 'Failed to delete credential');
      }
      return data;
    } catch (error) {
      throw error;
    }
  },

  // ============== SYNC ==============

  async getSyncLogs() {
    try {
      const headers = await getAuthHeaders();
      const response = await fetch(`${API_BASE}/sync/logs`, {
        method: 'GET',
        headers
      });
      const data = await response.json();
      if (!response.ok) {
        throw new Error(data.message || 'Failed to fetch sync logs');
      }
      return data;
    } catch (error) {
      throw error;
    }
  },

  async triggerSync(deviceName = 'Web Client', deviceType = 'desktop') {
    try {
      const headers = await getAuthHeaders();
      const params = new URLSearchParams();
      params.append('deviceName', deviceName);
      params.append('deviceType', deviceType);

      const url = `${API_BASE}/sync/trigger?${params.toString()}`;
      const response = await fetch(url, {
        method: 'POST',
        headers
      });
      const data = await response.json();
      if (!response.ok) {
        throw new Error(data.message || 'Failed to trigger sync');
      }
      return data;
    } catch (error) {
      throw error;
    }
  },

  // ============== USER ==============

  async getProfile() {
    try {
      const headers = await getAuthHeaders();
      const response = await fetch(`${API_BASE}/user/profile`, {
        method: 'GET',
        headers
      });
      const data = await response.json();
      if (!response.ok) {
        throw new Error(data.message || 'Failed to fetch profile');
      }
      return data;
    } catch (error) {
      throw error;
    }
  },

  async updateProfile(username: string, avatarUrl?: string | null) {
    try {
      const headers = await getAuthHeaders();
      const response = await fetch(`${API_BASE}/user/profile`, {
        method: 'PUT',
        headers,
        body: JSON.stringify({ username, avatarUrl })
      });
      const data = await response.json();
      if (!response.ok) {
        throw new Error(data.message || 'Failed to update profile');
      }
      return data;
    } catch (error) {
      throw error;
    }
  }
};

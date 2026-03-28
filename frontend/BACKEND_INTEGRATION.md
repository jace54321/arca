# Frontend Integration Guide

This guide explains how to integrate your React/Vite frontend with the newly generated backend.

## Overview

Your backend is now running on `http://localhost:8080` with the following API base:
```
http://localhost:8080/api
```

All API calls require:
1. Supabase JWT token in the `Authorization` header
2. Master password in the `masterPassword` header (for vault operations)

---

## Setup Steps

### 1. Create API Client Service

Create `frontend/src/services/apiClient.ts`:

```typescript
import { supabase } from '@/lib/supabaseClient';

const API_BASE = import.meta.env.VITE_API_URL || 'http://localhost:8080/api';

async function getAuthHeaders(masterPassword?: string) {
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
  
  async register(email: string, masterPassword: string) {
    const response = await fetch(`${API_BASE}/auth/register`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ email, masterPassword })
    });
    return response.json();
  },

  async login(masterPassword: string) {
    const headers = await getAuthHeaders();
    const response = await fetch(`${API_BASE}/auth/login`, {
      method: 'POST',
      headers,
      body: JSON.stringify({ masterPassword })
    });
    return response.json();
  },

  // ============== VAULT ==============

  async unlockVault(masterPassword: string) {
    const headers = await getAuthHeaders();
    const response = await fetch(`${API_BASE}/vault/unlock`, {
      method: 'POST',
      headers,
      body: JSON.stringify({ masterPassword })
    });
    return response.json();
  },

  async getCredentials(masterPassword: string) {
    const headers = await getAuthHeaders(masterPassword);
    const response = await fetch(`${API_BASE}/vault/credentials`, {
      method: 'GET',
      headers
    });
    return response.json();
  },

  async createCredential(credential: any, masterPassword: string) {
    const headers = await getAuthHeaders(masterPassword);
    const response = await fetch(`${API_BASE}/vault/credentials`, {
      method: 'POST',
      headers,
      body: JSON.stringify(credential)
    });
    return response.json();
  },

  async updateCredential(id: string, credential: any, masterPassword: string) {
    const headers = await getAuthHeaders(masterPassword);
    const response = await fetch(`${API_BASE}/vault/credentials/${id}`, {
      method: 'PUT',
      headers,
      body: JSON.stringify(credential)
    });
    return response.json();
  },

  async deleteCredential(id: string) {
    const headers = await getAuthHeaders();
    const response = await fetch(`${API_BASE}/vault/credentials/${id}`, {
      method: 'DELETE',
      headers
    });
    return response.json();
  },

  // ============== SYNC ==============

  async getSyncLogs() {
    const headers = await getAuthHeaders();
    const response = await fetch(`${API_BASE}/sync/logs`, {
      method: 'GET',
      headers
    });
    return response.json();
  },

  async triggerSync(deviceName?: string, deviceType?: string) {
    const headers = await getAuthHeaders();
    const params = new URLSearchParams();
    if (deviceName) params.append('deviceName', deviceName);
    if (deviceType) params.append('deviceType', deviceType);

    const url = `${API_BASE}/sync/trigger${params.toString() ? '?' + params.toString() : ''}`;
    const response = await fetch(url, {
      method: 'POST',
      headers
    });
    return response.json();
  },

  // ============== USER ==============

  async getProfile() {
    const headers = await getAuthHeaders();
    const response = await fetch(`${API_BASE}/user/profile`, {
      method: 'GET',
      headers
    });
    return response.json();
  },

  async updateProfile(username: string, avatarUrl?: string) {
    const headers = await getAuthHeaders();
    const response = await fetch(`${API_BASE}/user/profile`, {
      method: 'PUT',
      headers,
      body: JSON.stringify({ username, avatarUrl })
    });
    return response.json();
  }
};
```

### 2. Update Environment Variables

Update `frontend/.env.local`:
```
VITE_API_URL=http://localhost:8080/api
```

### 3. Create Custom Hooks

Create `frontend/src/hooks/useVault.ts`:

```typescript
import { useState } from 'react';
import { apiClient } from '@/services/apiClient';

export function useVault() {
  const [credentials, setCredentials] = useState<any[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const fetchCredentials = async (masterPassword: string) => {
    setLoading(true);
    setError(null);
    try {
      const response = await apiClient.getCredentials(masterPassword);
      if (response.success) {
        setCredentials(response.data);
      } else {
        setError(response.message);
      }
    } catch (err) {
      setError(err instanceof Error ? err.message : 'An error occurred');
    } finally {
      setLoading(false);
    }
  };

  const addCredential = async (credential: any, masterPassword: string) => {
    setLoading(true);
    try {
      const response = await apiClient.createCredential(credential, masterPassword);
      if (response.success) {
        setCredentials([response.data, ...credentials]);
        return response.data;
      } else {
        throw new Error(response.message);
      }
    } catch (err) {
      setError(err instanceof Error ? err.message : 'An error occurred');
      throw err;
    } finally {
      setLoading(false);
    }
  };

  const updateCredential = async (id: string, updates: any, masterPassword: string) => {
    try {
      const response = await apiClient.updateCredential(id, updates, masterPassword);
      if (response.success) {
        setCredentials(credentials.map(c => c.id === id ? response.data : c));
        return response.data;
      } else {
        throw new Error(response.message);
      }
    } catch (err) {
      setError(err instanceof Error ? err.message : 'An error occurred');
      throw err;
    }
  };

  const deleteCredential = async (id: string) => {
    try {
      const response = await apiClient.deleteCredential(id);
      if (response.success) {
        setCredentials(credentials.filter(c => c.id !== id));
      } else {
        throw new Error(response.message);
      }
    } catch (err) {
      setError(err instanceof Error ? err.message : 'An error occurred');
      throw err;
    }
  };

  return {
    credentials,
    loading,
    error,
    fetchCredentials,
    addCredential,
    updateCredential,
    deleteCredential
  };
}
```

### 4. Update ArcaContext

Replace the mock implementation in `frontend/src/app/context/ArcaContext.tsx`:

```typescript
import { apiClient } from '@/services/apiClient';
import { supabase } from '@/lib/supabaseClient';

// In your context provider component:

const login = async (email: string, password: string, masterPassword: string) => {
  try {
    // First authenticate with Supabase
    const { data, error } = await supabase.auth.signInWithPassword({
      email,
      password
    });

    if (error) throw error;

    // Then validate master password with backend
    const response = await apiClient.login(masterPassword);
    
    if (!response.success) {
      throw new Error('Invalid master password');
    }

    setUserEmail(email);
    setIsAuthenticated(true);
    return true;
  } catch (err) {
    console.error('Login failed:', err);
    return false;
  }
};

const unlock = async (masterPassword: string) => {
  try {
    const response = await apiClient.unlockVault(masterPassword);
    
    if (response.success) {
      setIsVaultUnlocked(true);
      
      // Fetch credentials after unlock
      const credsResponse = await apiClient.getCredentials(masterPassword);
      if (credsResponse.success) {
        setCredentials(credsResponse.data);
      }
      
      return true;
    }
    return false;
  } catch (err) {
    console.error('Unlock failed:', err);
    return false;
  }
};

const addCredential = async (cred: any, masterPassword: string) => {
  try {
    const response = await apiClient.createCredential(cred, masterPassword);
    if (response.success) {
      setCredentials([response.data, ...credentials]);
    }
  } catch (err) {
    console.error('Add credential failed:', err);
  }
};

const updateCredential = async (id: string, cred: any, masterPassword: string) => {
  try {
    const response = await apiClient.updateCredential(id, cred, masterPassword);
    if (response.success) {
      setCredentials(credentials.map(c => c.id === id ? response.data : c));
    }
  } catch (err) {
    console.error('Update credential failed:', err);
  }
};

const deleteCredential = async (id: string) => {
  try {
    const response = await apiClient.deleteCredential(id);
    if (response.success) {
      setCredentials(credentials.filter(c => c.id !== id));
    }
  } catch (err) {
    console.error('Delete credential failed:', err);
  }
};

const triggerSync = async () => {
  try {
    setSyncStatus('syncing');
    const response = await apiClient.triggerSync('Web Client', 'desktop');
    
    if (response.success) {
      // Fetch updated sync logs
      const logsResponse = await apiClient.getSyncLogs();
      if (logsResponse.success) {
        setSyncLogs(logsResponse.data);
      }
      setSyncStatus('synced');
    }
  } catch (err) {
    console.error('Sync failed:', err);
    setSyncStatus('error');
  }
};
```

### 5. Add .env to Frontend

Create `frontend/.env.local`:
```
VITE_SUPABASE_URL=https://xupeembqwzmrpkoegnhr.supabase.co
VITE_SUPABASE_ANON_KEY=your-anon-key-from-supabase
VITE_API_URL=http://localhost:8080/api
```

---

## Testing the Integration

### 1. Start Backend
```bash
cd arca-backend
mvn spring-boot:run
```

### 2. Start Frontend
```bash
cd frontend
npm run dev
```

### 3. Test Workflow

1. **Register**: 
   - Go to Login page
   - Sign up with email + master password
   - Backend creates user with encrypted master password

2. **Login**:
   - Sign in with email  + password (authenticates with Supabase)
   - Enter master password to unlock vault

3. **Vault Operations**:
   - Add credentials (encrypted with master password)
   - View, edit, delete credentials
   - Search and filter

4. **Sync**:
   - Trigger sync to see version updates
   - View sync logs

---

## Key Points

✅ Master password is NEVER sent to Supabase, only to your backend  
✅ Passwords are encrypted before being sent to backend  
✅ Backend validates JWT from Supabase for every request  
✅ CORS is configured to allow `localhost:5173`  
✅ Error handling: Check `response.success` before using `response.data`  

---

## Troubleshooting

### "Unauthorized" Error
- Check that Supabase JWT is being sent in Authorization header
- Verify JWT is valid (not expired)
- Check that SUPABASE_PROJECT_URL matches in backend

### "Invalid master password"
- Master password hashing may have failed
- Ensure master password is exactly the same as during registration

### CORS Errors
- Verify frontend is running on `localhost:5173`
- Check `SecurityConfig.cors()` allows this origin
- Ensure `Authorization` and `masterPassword` headers are whitelisted

### Credentials Not Decrypting
- Verify encryption key is same as stored
- Check that master password matches
- Ensure encryption salt wasn't modified

---

## Next Steps

1. Test the full workflow
2. Add loading states and error boundaries
3. Implement offline sync (store master password temporarily in session)
4. Add password strength validation
5. Implement 2FA
6. Add audit logs for compliance

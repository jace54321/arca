export interface Credential {
  id: string;
  siteName: string;
  url: string;
  username: string;
  password: string;
  category: 'Work' | 'Personal' | 'Social' | 'Other';
  notes?: string;
  syncStatus: 'synced' | 'syncing' | 'offline' | 'conflict';
  offlineModified?: boolean;
  lastModified: string;
}

export interface SyncLog {
  id: string;
  device: string;
  deviceType: 'mobile' | 'desktop' | 'browser';
  timestamp: string;
  status: 'synced' | 'conflict' | 'error';
  versionFrom: number;
  versionTo: number;
  message: string;
  isCurrentDevice?: boolean;
}

export interface Device {
  id: string;
  deviceName: string;
  deviceType: string;
  lastActive: string;
}

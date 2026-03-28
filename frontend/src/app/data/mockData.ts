export interface Credential {
  id: string;
  siteName: string;
  url: string;
  username: string;
  password: string;
  category: 'Work' | 'Personal' | 'Social' | 'Other';
  syncStatus: 'synced' | 'pending' | 'syncing' | 'offline' | 'error';
  offlineModified: boolean;
  lastModified: string;
  notes?: string;
}

export interface SyncLog {
  id: string;
  device: string;
  deviceType: 'mobile' | 'desktop';
  timestamp: string;
  status: 'synced' | 'conflict' | 'error';
  versionFrom: number;
  versionTo: number;
  message: string;
  isCurrentDevice: boolean;
}

export const mockCredentials: Credential[] = [
  {
    id: '1',
    siteName: 'GitHub',
    url: 'https://github.com',
    username: 'alex.morgan@dev.io',
    password: 'Gh!abc123xyz#8Secure',
    category: 'Work',
    syncStatus: 'synced',
    offlineModified: false,
    lastModified: '2026-03-15T10:30:00',
    notes: 'Main developer account',
  },
  {
    id: '2',
    siteName: 'Google',
    url: 'https://google.com',
    username: 'alex.morgan@gmail.com',
    password: 'G00gl3!S3cur3P@ss2026',
    category: 'Personal',
    syncStatus: 'pending',
    offlineModified: true,
    lastModified: '2026-03-17T08:12:00',
  },
  {
    id: '3',
    siteName: 'Netflix',
    url: 'https://netflix.com',
    username: 'alex.morgan@gmail.com',
    password: 'N3tfl!x2024#Pr3m!um',
    category: 'Personal',
    syncStatus: 'synced',
    offlineModified: false,
    lastModified: '2026-02-20T14:45:00',
  },
  {
    id: '4',
    siteName: 'Slack',
    url: 'https://slack.com',
    username: 'alex@company.io',
    password: 'Sl@ck!W0rksp@ce2026',
    category: 'Work',
    syncStatus: 'synced',
    offlineModified: false,
    lastModified: '2026-03-10T09:00:00',
  },
  {
    id: '5',
    siteName: 'Twitter / X',
    url: 'https://x.com',
    username: '@alexmorgan_dev',
    password: 'Tw!tt3rX_S3cur3#26',
    category: 'Social',
    syncStatus: 'pending',
    offlineModified: true,
    lastModified: '2026-03-17T07:30:00',
  },
  {
    id: '6',
    siteName: 'LinkedIn',
    url: 'https://linkedin.com',
    username: 'alex.morgan@dev.io',
    password: 'L!nk3dIn_Pr0f!l3@2026',
    category: 'Social',
    syncStatus: 'synced',
    offlineModified: false,
    lastModified: '2026-03-01T11:15:00',
  },
  {
    id: '7',
    siteName: 'AWS Console',
    url: 'https://aws.amazon.com',
    username: 'alex.morgan@company.io',
    password: 'AWSc0ns0le!Adm!n#2026',
    category: 'Work',
    syncStatus: 'synced',
    offlineModified: false,
    lastModified: '2026-03-05T16:20:00',
    notes: 'Production account – handle with care',
  },
  {
    id: '8',
    siteName: 'Spotify',
    url: 'https://spotify.com',
    username: 'alex.morgan@gmail.com',
    password: 'Sp0t!fy_Pr3m!um@2026',
    category: 'Personal',
    syncStatus: 'error',
    offlineModified: false,
    lastModified: '2026-03-12T18:00:00',
  },
];

export const mockSyncLogs: SyncLog[] = [
  {
    id: '1',
    device: 'Pixel 7 Pro',
    deviceType: 'mobile',
    timestamp: '2026-03-17T09:30:00',
    status: 'synced',
    versionFrom: 12,
    versionTo: 13,
    message: 'Vault updated successfully — 2 entries modified',
    isCurrentDevice: true,
  },
  {
    id: '2',
    device: 'MacBook Pro 16"',
    deviceType: 'desktop',
    timestamp: '2026-03-16T21:05:00',
    status: 'synced',
    versionFrom: 11,
    versionTo: 12,
    message: 'Vault updated successfully — 1 entry added',
    isCurrentDevice: false,
  },
  {
    id: '3',
    device: 'Pixel 7 Pro',
    deviceType: 'mobile',
    timestamp: '2026-03-16T14:48:00',
    status: 'conflict',
    versionFrom: 10,
    versionTo: 11,
    message: 'Conflict detected — remote version accepted. Local changes discarded.',
    isCurrentDevice: true,
  },
  {
    id: '4',
    device: 'MacBook Pro 16"',
    deviceType: 'desktop',
    timestamp: '2026-03-15T11:22:00',
    status: 'synced',
    versionFrom: 9,
    versionTo: 10,
    message: 'Vault updated successfully — 3 entries modified',
    isCurrentDevice: false,
  },
  {
    id: '5',
    device: 'iPad Air',
    deviceType: 'mobile',
    timestamp: '2026-03-14T16:55:00',
    status: 'error',
    versionFrom: 9,
    versionTo: 9,
    message: 'Sync failed — authentication token expired. Please re-authenticate.',
    isCurrentDevice: false,
  },
  {
    id: '6',
    device: 'Pixel 7 Pro',
    deviceType: 'mobile',
    timestamp: '2026-03-13T08:10:00',
    status: 'synced',
    versionFrom: 8,
    versionTo: 9,
    message: 'Vault updated successfully — initial sync after offline period',
    isCurrentDevice: true,
  },
  {
    id: '7',
    device: 'MacBook Pro 16"',
    deviceType: 'desktop',
    timestamp: '2026-03-10T19:30:00',
    status: 'synced',
    versionFrom: 7,
    versionTo: 8,
    message: 'Vault updated successfully — 4 entries modified',
    isCurrentDevice: false,
  },
  {
    id: '8',
    device: 'MacBook Pro 16"',
    deviceType: 'desktop',
    timestamp: '2026-03-08T10:00:00',
    status: 'synced',
    versionFrom: 6,
    versionTo: 7,
    message: 'Vault initialized — first sync from this device',
    isCurrentDevice: false,
  },
];

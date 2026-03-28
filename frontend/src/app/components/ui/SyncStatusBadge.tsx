import React from 'react';
import { Cloud, CloudOff, CloudUpload, RefreshCw, AlertCircle, CheckCircle2 } from 'lucide-react';

type SyncState = 'synced' | 'syncing' | 'pending' | 'offline' | 'error';

interface SyncStatusBadgeProps {
  status: SyncState;
  compact?: boolean;
}

const config: Record<SyncState, { icon: React.ReactNode; color: string; bg: string; label: string }> = {
  synced: {
    icon: <CheckCircle2 size={12} />,
    color: '#10B981',
    bg: 'rgba(16,185,129,0.12)',
    label: 'Synced',
  },
  syncing: {
    icon: <RefreshCw size={12} className="animate-spin" style={{ animationDuration: '1.2s' }} />,
    color: '#3B82F6',
    bg: 'rgba(59,130,246,0.12)',
    label: 'Syncing…',
  },
  pending: {
    icon: <CloudUpload size={12} />,
    color: '#FF4500',
    bg: 'rgba(255,69,0,0.12)',
    label: 'Pending',
  },
  offline: {
    icon: <CloudOff size={12} />,
    color: '#475569',
    bg: 'rgba(71,85,105,0.12)',
    label: 'Offline',
  },
  error: {
    icon: <AlertCircle size={12} />,
    color: '#EF4444',
    bg: 'rgba(239,68,68,0.12)',
    label: 'Sync Failed',
  },
};

export function SyncStatusBadge({ status, compact = false }: SyncStatusBadgeProps) {
  const cfg = config[status];

  return (
    <div style={{
      display: 'inline-flex',
      alignItems: 'center',
      gap: '4px',
      padding: compact ? '2px 6px' : '3px 8px',
      borderRadius: '9999px',
      backgroundColor: cfg.bg,
      color: cfg.color,
      fontSize: '11px',
      fontFamily: "'Ubuntu', sans-serif",
      fontWeight: 500,
      whiteSpace: 'nowrap',
    }}>
      {cfg.icon}
      {!compact && <span>{cfg.label}</span>}
    </div>
  );
}

import React, { useState, useEffect } from 'react';
import { Smartphone, Monitor, CheckCircle2, AlertTriangle, XCircle, RefreshCw } from 'lucide-react';
import { SyncStatusBadge } from '../components/ui/SyncStatusBadge';
import { useArca } from '../context/ArcaContext';
import { apiClient } from '@/services/apiClient';
import { ArcaButton } from '../components/ui/ArcaButton';
import type { SyncLog } from '@/types';

type FilterStatus = 'all' | 'synced' | 'conflict' | 'error';

const STATUS_MAP: Record<SyncLog['status'], 'synced' | 'error' | 'pending'> = {
  synced: 'synced',
  conflict: 'pending',
  error: 'error',
};

function formatTimestamp(iso: string): string {
  const d = new Date(iso);
  return d.toLocaleDateString('en-US', { month: 'short', day: 'numeric', year: 'numeric' }) +
    ' · ' + d.toLocaleTimeString('en-US', { hour: '2-digit', minute: '2-digit' });
}

export function SyncLogsPage() {
  const { triggerSync } = useArca();
  const [filter, setFilter] = useState<FilterStatus>('all');
  const [syncLogs, setSyncLogs] = useState<SyncLog[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [isSyncing, setIsSyncing] = useState(false);
  const [error, setError] = useState<string | null>(null);

  // Fetch sync logs on mount
  useEffect(() => {
    const fetchLogs = async () => {
      try {
        setIsLoading(true);
        setError(null);
        const response = await apiClient.getSyncLogs();
        if (response.success && response.data) {
          setSyncLogs(response.data);
        } else {
          setError(response.message || 'Failed to fetch sync logs');
        }
      } catch (err) {
        const errorMsg = err instanceof Error ? err.message : 'Failed to fetch sync logs';
        setError(errorMsg);
      } finally {
        setIsLoading(false);
      }
    };

    fetchLogs();
  }, []);

  const handleTriggerSync = async () => {
    try {
      setIsSyncing(true);
      const response = await triggerSync('WebBrowser', 'desktop');
      if (response?.success) {
        // Refresh logs after sync
        const logsResponse = await apiClient.getSyncLogs();
        if (logsResponse.success && logsResponse.data) {
          setSyncLogs(logsResponse.data);
        }
      }
    } catch (err) {
      const errorMsg = err instanceof Error ? err.message : 'Sync failed';
      setError(errorMsg);
      setTimeout(() => setError(null), 3000);
    } finally {
      setIsSyncing(false);
    }
  };

  const filtered = syncLogs.filter(log => filter === 'all' || log.status === filter);

  const statusIcon: Record<SyncLog['status'], React.ReactNode> = {
    synced: <CheckCircle2 size={12} color="#10B981" />,
    conflict: <AlertTriangle size={12} color="#FF4500" />,
    error: <XCircle size={12} color="#F90000" />,
  };

  const FILTER_LABELS: Record<FilterStatus, string> = {
    all: 'ALL EVENTS',
    synced: 'SYNCED',
    conflict: 'CONFLICT',
    error: 'ERROR',
  };

  return (
    <div style={{ padding: '32px 36px', maxWidth: '1100px' }}>

      {/* Tactical header */}
      <div style={{ marginBottom: '32px', display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start' }}>
        <div>
          <div style={{ display: 'flex', alignItems: 'center', gap: '10px', marginBottom: '8px' }}>
            <div style={{ width: '20px', height: '1px', backgroundColor: '#F90000' }} />
            <span style={{ fontFamily: "'JetBrains Mono', monospace", fontSize: '10px', color: '#F90000', letterSpacing: '0.2em' }}>
              // SYNC LOGS
            </span>
          </div>
          <h1 style={{ fontFamily: "'Ubuntu', sans-serif", fontSize: '22px', fontWeight: 700, color: '#F1F5F9', margin: '0 0 6px', letterSpacing: '-0.01em', textTransform: 'uppercase' }}>
            Sync History
          </h1>
          <p style={{ fontFamily: "'JetBrains Mono', monospace", fontSize: '11px', color: '#2A3040', margin: 0, letterSpacing: '0.06em' }}>
            COMPLETE RECORD OF EVERY VAULT SYNCHRONIZATION
          </p>
        </div>
        <ArcaButton 
          variant="secondary" 
          size="sm" 
          onClick={handleTriggerSync} 
          loading={isSyncing}
          leftIcon={<RefreshCw size={14} />}
        >
          {isSyncing ? 'SYNCING...' : 'SYNC NOW'}
        </ArcaButton>
      </div>

      {/* Error message */}
      {error && (
        <div style={{
          padding: '12px 16px',
          backgroundColor: 'rgba(249,0,0,0.1)',
          border: '1px solid rgba(249,0,0,0.3)',
          borderRadius: '4px',
          marginBottom: '20px',
          fontFamily: "'JetBrains Mono', monospace",
          fontSize: '11px',
          color: '#F90000',
        }}>
          {error}
        </div>
      )}

      {/* Loading state */}
      {isLoading ? (
        <div style={{ padding: '48px', textAlign: 'center', border: '1px solid rgba(54,60,69,0.5)' }}>
          <p style={{ fontFamily: "'JetBrains Mono', monospace", fontSize: '11px', color: '#2A3040', margin: 0, letterSpacing: '0.1em' }}>
            LOADING SYNC HISTORY...
          </p>
        </div>
      ) : (
        <>
      {/* Filter bar */}
      <div style={{ display: 'flex', gap: '2px', marginBottom: '20px', alignItems: 'center' }}>
        {(['all', 'synced', 'conflict', 'error'] as FilterStatus[]).map(f => (
          <button
            key={f}
            onClick={() => setFilter(f)}
            style={{
              padding: '7px 16px', borderRadius: '0', cursor: 'pointer',
              backgroundColor: filter === f ? '#F90000' : 'transparent',
              border: `1px solid ${filter === f ? '#F90000' : 'rgba(54,60,69,0.5)'}`,
              color: filter === f ? '#0A0C0F' : '#475569',
              fontFamily: "'JetBrains Mono', monospace", fontSize: '10px', fontWeight: 500,
              letterSpacing: '0.12em', transition: 'all 150ms ease',
              marginRight: f !== 'error' ? '-1px' : 0,
            }}
            onMouseEnter={e => { if (filter !== f) { e.currentTarget.style.color = '#F1F5F9'; e.currentTarget.style.borderColor = 'rgba(249,0,0,0.3)'; } }}
            onMouseLeave={e => { if (filter !== f) { e.currentTarget.style.color = '#475569'; e.currentTarget.style.borderColor = 'rgba(54,60,69,0.5)'; } }}
          >
            {FILTER_LABELS[f]}
          </button>
        ))}
        <div style={{ marginLeft: 'auto' }}>
          <span style={{ fontFamily: "'JetBrains Mono', monospace", fontSize: '10px', color: '#2A3040', letterSpacing: '0.1em' }}>
            {filtered.length.toString().padStart(2, '0')} EVENT{filtered.length !== 1 ? 'S' : ''}
          </span>
        </div>
      </div>

      {/* Table */}
      <div style={{ border: '1px solid rgba(54,60,69,0.5)', overflow: 'hidden' }}>
        {/* Header row */}
        <div style={{
          display: 'grid', gridTemplateColumns: '200px 200px 110px 110px 1fr',
          padding: '10px 20px',
          borderBottom: '1px solid rgba(54,60,69,0.5)',
          backgroundColor: '#0D1014',
        }}>
          {['DEVICE', 'TIMESTAMP', 'STATUS', 'VERSION', 'MESSAGE'].map(col => (
            <span key={col} style={{
              fontFamily: "'JetBrains Mono', monospace", fontSize: '9px', fontWeight: 500,
              color: '#2A3040', letterSpacing: '0.18em',
            }}>
              {col}
            </span>
          ))}
        </div>

        {filtered.length === 0 ? (
          <div style={{ padding: '48px', textAlign: 'center' }}>
            <p style={{ fontFamily: "'JetBrains Mono', monospace", fontSize: '11px', color: '#2A3040', margin: 0, letterSpacing: '0.1em' }}>
              NO SYNC EVENTS FOUND
            </p>
          </div>
        ) : (
          filtered.map((log, index) => (
            <div
              key={log.id}
              style={{
                display: 'grid', gridTemplateColumns: '200px 200px 110px 110px 1fr',
                padding: '14px 20px',
                borderBottom: index < filtered.length - 1 ? '1px solid rgba(54,60,69,0.3)' : 'none',
                transition: 'background-color 150ms ease',
                alignItems: 'center',
                backgroundColor: 'transparent',
              }}
              onMouseEnter={e => (e.currentTarget.style.backgroundColor = 'rgba(241,245,249,0.02)')}
              onMouseLeave={e => (e.currentTarget.style.backgroundColor = 'transparent')}
            >
              {/* Device */}
              <div style={{ display: 'flex', alignItems: 'center', gap: '10px' }}>
                <div style={{ color: '#2A3040', flexShrink: 0 }}>
                  {log.deviceType === 'mobile' ? <Smartphone size={14} /> : <Monitor size={14} />}
                </div>
                <div>
                  <div style={{ display: 'flex', alignItems: 'center', gap: '6px' }}>
                    <span style={{ fontFamily: "'Ubuntu', sans-serif", fontSize: '12px', color: '#94A3B8', fontWeight: 500 }}>
                      {log.device}
                    </span>
                    {log.isCurrentDevice && (
                      <span style={{
                        fontSize: '9px', color: '#F90000',
                        backgroundColor: 'rgba(249,0,0,0.08)', padding: '1px 5px',
                        fontFamily: "'JetBrains Mono', monospace", letterSpacing: '0.08em',
                      }}>
                        THIS DEVICE
                      </span>
                    )}
                  </div>
                  <span style={{ fontFamily: "'JetBrains Mono', monospace", fontSize: '9px', color: '#2A3040', letterSpacing: '0.06em', textTransform: 'uppercase' }}>
                    {log.deviceType}
                  </span>
                </div>
              </div>

              {/* Timestamp */}
              <span style={{ fontFamily: "'JetBrains Mono', monospace", fontSize: '10px', color: '#475569', letterSpacing: '0.04em' }}>
                {formatTimestamp(log.timestamp)}
              </span>

              {/* Status */}
              <SyncStatusBadge status={STATUS_MAP[log.status]} />

              {/* Version */}
              <span style={{ fontFamily: "'JetBrains Mono', monospace", fontSize: '10px', color: '#2A3040', letterSpacing: '0.04em' }}>
                v{log.versionFrom} → v{log.versionTo}
              </span>

              {/* Message */}
              <div style={{ display: 'flex', alignItems: 'center', gap: '6px', minWidth: 0 }}>
                {statusIcon[log.status]}
                <span style={{
                  fontFamily: "'Ubuntu', sans-serif", fontSize: '12px',
                  color: log.status === 'error' ? '#F90000' : '#475569',
                  overflow: 'hidden', textOverflow: 'ellipsis', whiteSpace: 'nowrap',
                }}>
                  {log.message}
                </span>
              </div>
            </div>
          ))
        )}
      </div>
        </>
      )}
    </div>
  );
}

import React from 'react';
import { Lock, Settings, LogOut, LayoutGrid, History, Folder, RefreshCw } from 'lucide-react';
import { SyncStatusBadge } from '../ui/SyncStatusBadge';
import { useArca } from '../../context/ArcaContext';
import { useNavigate, useLocation } from 'react-router';

export function Sidebar() {
  const { userEmail, username, avatarUrl, logout, syncStatus, triggerSync, credentials } = useArca();
  const navigate = useNavigate();
  const location = useLocation();

  const isActive = (path: string) => location.pathname === path;

  const navItems = [
    { icon: <LayoutGrid size={16} />, label: 'ALL ENTRIES', path: '/vault', count: credentials.length },
    { icon: <Folder size={14} />, label: 'WORK', path: '/vault/category/work', sub: true, count: credentials.filter(c => c.category === 'Work').length },
    { icon: <Folder size={14} />, label: 'PERSONAL', path: '/vault/category/personal', sub: true, count: credentials.filter(c => c.category === 'Personal').length },
    { icon: <Folder size={14} />, label: 'SOCIAL', path: '/vault/category/social', sub: true, count: credentials.filter(c => c.category === 'Social').length },
    { icon: <History size={16} />, label: 'SYNC LOGS', path: '/vault/sync-logs' },
    { icon: <Settings size={16} />, label: 'SETTINGS', path: '/vault/settings' },
  ];

  return (
    <aside style={{
      width: '232px',
      flexShrink: 0,
      backgroundColor: '#0D1014',
      borderRight: '1px solid rgba(249,0,0,0.1)',
      display: 'flex',
      flexDirection: 'column',
      height: '100vh',
      position: 'sticky',
      top: 0,
    }}>
      {/* Subtle grid bg */}
      <div style={{
        position: 'absolute', inset: 0, pointerEvents: 'none',
        backgroundImage: `
          linear-gradient(rgba(249,0,0,0.03) 1px, transparent 1px),
          linear-gradient(90deg, rgba(249,0,0,0.03) 1px, transparent 1px)
        `,
        backgroundSize: '40px 40px',
        zIndex: 0,
      }} />

      {/* Logo area */}
      <div style={{ padding: '20px 20px 18px', borderBottom: '1px solid rgba(249,0,0,0.08)', position: 'relative', zIndex: 1 }}>
        <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
            <div style={{ width: '26px', height: '26px', borderRadius: '4px', backgroundColor: '#F90000', display: 'flex', alignItems: 'center', justifyContent: 'center', flexShrink: 0 }}>
              <Lock size={14} color="#0A0C0F" />
            </div>
            <span style={{ fontFamily: "'Ubuntu', sans-serif", fontSize: '18px', fontWeight: 700, color: '#F1F5F9', letterSpacing: '-0.01em' }}>
              Arc<span style={{ color: '#F90000' }}>a</span>
            </span>
          </div>
          <SyncStatusBadge status={syncStatus as any} compact />
        </div>
      </div>

      {/* Nav */}
      <nav style={{ flex: 1, padding: '12px 8px', overflowY: 'auto', position: 'relative', zIndex: 1 }}>

        {/* Section label */}
        <div style={{
          padding: '4px 12px 8px',
          fontFamily: "'JetBrains Mono', monospace", fontSize: '9px',
          color: 'rgba(249,0,0,0.5)', letterSpacing: '0.2em',
        }}>
          // VAULT
        </div>

        {navItems.map((item) => {
          const active = isActive(item.path);
          return (
            <button
              key={item.path}
              onClick={() => navigate(item.path)}
              style={{
                width: '100%',
                display: 'flex',
                alignItems: 'center',
                gap: '9px',
                padding: item.sub ? '7px 10px 7px 28px' : '9px 10px',
                marginBottom: '1px',
                borderRadius: '3px',
                background: active ? 'rgba(249,0,0,0.08)' : 'none',
                border: 'none',
                borderLeft: active ? '2px solid #F90000' : '2px solid transparent',
                cursor: 'pointer',
                color: active ? '#F1F5F9' : '#475569',
                fontFamily: "'JetBrains Mono', monospace",
                fontSize: item.sub ? '10px' : '11px',
                fontWeight: item.sub ? 400 : 500,
                letterSpacing: '0.1em',
                transition: 'all 150ms ease',
                textAlign: 'left',
              }}
              onMouseEnter={e => { if (!active) { e.currentTarget.style.backgroundColor = 'rgba(241,245,249,0.04)'; e.currentTarget.style.color = '#94A3B8'; } }}
              onMouseLeave={e => { if (!active) { e.currentTarget.style.backgroundColor = 'transparent'; e.currentTarget.style.color = '#475569'; } }}
            >
              <span style={{ color: active ? '#F90000' : 'inherit', flexShrink: 0 }}>{item.icon}</span>
              <span style={{ flex: 1 }}>{item.label}</span>
              {item.count !== undefined && (
                <span style={{
                  fontSize: '10px', color: active ? '#F90000' : '#2A3040',
                  fontFamily: "'JetBrains Mono', monospace",
                  letterSpacing: '0.04em',
                }}>
                  {item.count.toString().padStart(2, '0')}
                </span>
              )}
            </button>
          );
        })}

        {/* Sync button */}
        <div style={{ marginTop: '16px', padding: '0 2px' }}>
          <div style={{ height: '1px', backgroundColor: 'rgba(249,0,0,0.08)', marginBottom: '12px' }} />
          <button
            onClick={triggerSync}
            style={{
              width: '100%', display: 'flex', alignItems: 'center', gap: '8px',
              padding: '8px 10px', borderRadius: '3px', background: 'none',
              border: '1px solid rgba(249,0,0,0.15)',
              cursor: 'pointer', color: '#475569',
              fontFamily: "'JetBrains Mono', monospace", fontSize: '10px',
              letterSpacing: '0.1em', transition: 'all 200ms ease',
            }}
            onMouseEnter={e => { e.currentTarget.style.borderColor = '#F90000'; e.currentTarget.style.color = '#F90000'; e.currentTarget.style.backgroundColor = 'rgba(249,0,0,0.05)'; }}
            onMouseLeave={e => { e.currentTarget.style.borderColor = 'rgba(249,0,0,0.15)'; e.currentTarget.style.color = '#475569'; e.currentTarget.style.backgroundColor = 'transparent'; }}
          >
            <RefreshCw size={13} className={syncStatus === 'syncing' ? 'animate-spin' : ''} style={syncStatus === 'syncing' ? { animationDuration: '1.2s' } : {}} />
            {syncStatus === 'syncing' ? 'SYNCING…' : 'SYNC NOW'}
          </button>
        </div>
      </nav>

      {/* User info */}
      <div style={{ padding: '14px 16px', borderTop: '1px solid rgba(249,0,0,0.08)', position: 'relative', zIndex: 1 }}>
        <div style={{ display: 'flex', alignItems: 'center', gap: '10px', marginBottom: '10px' }}>
          <div
            onClick={() => navigate('/vault/settings')}
            style={{
              width: '32px', height: '32px', borderRadius: '3px', flexShrink: 0,
              backgroundColor: '#F90000', overflow: 'hidden',
              display: 'flex', alignItems: 'center', justifyContent: 'center',
              cursor: 'pointer', border: '1px solid rgba(249,0,0,0.3)',
              transition: 'box-shadow 200ms ease',
            }}
            onMouseEnter={e => (e.currentTarget.style.boxShadow = '0 0 10px rgba(249,0,0,0.4)')}
            onMouseLeave={e => (e.currentTarget.style.boxShadow = 'none')}
          >
            {avatarUrl ? (
              <img src={avatarUrl} alt="avatar" style={{ width: '100%', height: '100%', objectFit: 'cover' }} />
            ) : (
              <span style={{ fontFamily: "'Ubuntu', sans-serif", fontSize: '13px', fontWeight: 700, color: '#0A0C0F', lineHeight: 1 }}>
                {username ? username.charAt(0).toUpperCase() : 'A'}
              </span>
            )}
          </div>
          <div style={{ flex: 1, minWidth: 0 }}>
            <div style={{ fontFamily: "'Ubuntu', sans-serif", fontSize: '12px', fontWeight: 500, color: '#F1F5F9', overflow: 'hidden', textOverflow: 'ellipsis', whiteSpace: 'nowrap' }}>
              {username || 'User'}
            </div>
            <div style={{ fontFamily: "'JetBrains Mono', monospace", fontSize: '9px', color: '#2A3040', overflow: 'hidden', textOverflow: 'ellipsis', whiteSpace: 'nowrap', letterSpacing: '0.04em' }}>
              {userEmail}
            </div>
          </div>
        </div>
        <button
          onClick={logout}
          style={{
            display: 'flex', alignItems: 'center', gap: '6px', background: 'none', border: 'none',
            cursor: 'pointer', color: '#2A3040',
            fontFamily: "'JetBrains Mono', monospace", fontSize: '9px',
            letterSpacing: '0.12em', padding: 0, transition: 'color 200ms ease',
          }}
          onMouseEnter={e => (e.currentTarget.style.color = '#F90000')}
          onMouseLeave={e => (e.currentTarget.style.color = '#2A3040')}
        >
          <LogOut size={12} /> LOG OUT
        </button>
      </div>
    </aside>
  );
}

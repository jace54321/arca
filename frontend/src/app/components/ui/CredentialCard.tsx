import React, { useState } from 'react';
import { Copy, Eye, EyeOff, MoreVertical, Pencil, Trash2, Check } from 'lucide-react';
import { SyncStatusBadge } from './SyncStatusBadge';
import { Credential } from '../../data/mockData';

interface CredentialCardProps {
  credential: Credential;
  onEdit: (credential: Credential) => void;
  onDelete: (id: string) => void;
  dimmed?: boolean;
  animationDelay?: number;
}

const SITE_COLORS: Record<string, string> = {
  G: '#F90000', S: '#2ECC71', N: '#E50914', T: '#1DA1F2',
  L: '#0A66C2', A: '#FF9900', P: '#1ED760', F: '#1877F2',
};

export function CredentialCard({ credential, onEdit, onDelete, dimmed = false, animationDelay = 0 }: CredentialCardProps) {
  const [isHovered, setIsHovered] = useState(false);
  const [showPassword, setShowPassword] = useState(false);
  const [showMenu, setShowMenu] = useState(false);
  const [copied, setCopied] = useState<'username' | 'password' | null>(null);

  const firstLetter = credential.siteName.charAt(0).toUpperCase();
  const avatarColor = SITE_COLORS[firstLetter] || '#F90000';

  const maskedUsername = credential.username.length > 3
    ? credential.username.slice(0, 3) + '••••••'
    : credential.username;

  const handleCopy = async (type: 'username' | 'password', value: string) => {
    await navigator.clipboard.writeText(value).catch(() => {});
    setCopied(type);
    setTimeout(() => setCopied(null), 2000);
  };

  return (
    <div
      style={{
        backgroundColor: isHovered ? '#111418' : '#0D1014',
        borderLeft: credential.offlineModified ? '2px solid #F90000' : '2px solid transparent',
        padding: '16px 18px',
        minHeight: '96px',
        cursor: 'pointer',
        transition: 'all 180ms ease',
        opacity: dimmed ? 0.25 : 1,
        position: 'relative',
        animation: `fadeInUp 280ms ${animationDelay}ms both cubic-bezier(0.4,0,0.2,1)`,
      }}
      onMouseEnter={() => setIsHovered(true)}
      onMouseLeave={() => { setIsHovered(false); setShowMenu(false); }}
      onClick={() => onEdit(credential)}
    >
      {/* Top row */}
      <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: '12px' }}>
        <div style={{ display: 'flex', alignItems: 'center', gap: '10px', minWidth: 0 }}>
          <div style={{
            width: '24px', height: '24px', borderRadius: '3px',
            backgroundColor: avatarColor,
            display: 'flex', alignItems: 'center', justifyContent: 'center',
            flexShrink: 0, fontSize: '11px', fontWeight: 700, color: '#0A0C0F',
            fontFamily: "'Ubuntu', sans-serif",
          }}>
            {firstLetter}
          </div>
          <span style={{ fontFamily: "'Ubuntu', sans-serif", fontSize: '13px', fontWeight: 500, color: '#F1F5F9', overflow: 'hidden', textOverflow: 'ellipsis', whiteSpace: 'nowrap' }}>
            {credential.siteName}
          </span>
        </div>
        <button
          onClick={e => { e.stopPropagation(); setShowMenu(v => !v); }}
          style={{ background: 'none', border: 'none', cursor: 'pointer', color: '#2A3040', padding: '4px', borderRadius: '2px', display: 'flex', alignItems: 'center', transition: 'color 150ms ease' }}
          onMouseEnter={e => (e.currentTarget.style.color = '#F90000')}
          onMouseLeave={e => (e.currentTarget.style.color = '#2A3040')}
        >
          <MoreVertical size={14} />
        </button>
        {showMenu && (
          <div
            onClick={e => e.stopPropagation()}
            style={{
              position: 'absolute', top: '38px', right: '10px', zIndex: 50,
              backgroundColor: '#111418', border: '1px solid rgba(54,60,69,0.6)', borderRadius: '3px',
              padding: '3px', minWidth: '130px', boxShadow: '0 8px 24px rgba(0,0,0,0.6)',
            }}
          >
            <button
              onClick={(e) => { e.stopPropagation(); setShowMenu(false); onEdit(credential); }}
              style={{ display: 'flex', alignItems: 'center', gap: '8px', width: '100%', padding: '7px 10px', background: 'none', border: 'none', cursor: 'pointer', color: '#94A3B8', fontFamily: "'JetBrains Mono', monospace", fontSize: '10px', letterSpacing: '0.08em', borderRadius: '2px', transition: 'all 150ms ease' }}
              onMouseEnter={e => { e.currentTarget.style.backgroundColor = 'rgba(241,245,249,0.05)'; e.currentTarget.style.color = '#F1F5F9'; }}
              onMouseLeave={e => { e.currentTarget.style.backgroundColor = 'transparent'; e.currentTarget.style.color = '#94A3B8'; }}
            >
              <Pencil size={12} /> EDIT
            </button>
            <button
              onClick={(e) => { e.stopPropagation(); setShowMenu(false); onDelete(credential.id); }}
              style={{ display: 'flex', alignItems: 'center', gap: '8px', width: '100%', padding: '7px 10px', background: 'none', border: 'none', cursor: 'pointer', color: '#F90000', fontFamily: "'JetBrains Mono', monospace", fontSize: '10px', letterSpacing: '0.08em', borderRadius: '2px', transition: 'all 150ms ease' }}
              onMouseEnter={e => (e.currentTarget.style.backgroundColor = 'rgba(249,0,0,0.08)')}
              onMouseLeave={e => (e.currentTarget.style.backgroundColor = 'transparent')}
            >
              <Trash2 size={12} /> DELETE
            </button>
          </div>
        )}
      </div>

      {/* Username row */}
      <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: '6px' }}>
        <span style={{ fontFamily: "'JetBrains Mono', monospace", fontSize: '11px', color: '#475569', overflow: 'hidden', textOverflow: 'ellipsis', whiteSpace: 'nowrap' }}>
          {maskedUsername}
        </span>
        <button
          onClick={e => { e.stopPropagation(); handleCopy('username', credential.username); }}
          style={{ background: 'none', border: 'none', cursor: 'pointer', color: copied === 'username' ? '#10B981' : '#2A3040', padding: '2px', display: 'flex', alignItems: 'center', flexShrink: 0, transition: 'color 150ms ease' }}
          title="Copy username"
          onMouseEnter={e => { if (copied !== 'username') e.currentTarget.style.color = '#F90000'; }}
          onMouseLeave={e => { if (copied !== 'username') e.currentTarget.style.color = '#2A3040'; }}
        >
          {copied === 'username' ? <Check size={12} /> : <Copy size={12} />}
        </button>
      </div>

      {/* Password row */}
      <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
        <span style={{ fontFamily: "'JetBrains Mono', monospace", fontSize: '11px', color: '#2A3040', overflow: 'hidden', textOverflow: 'ellipsis', whiteSpace: 'nowrap', flex: 1 }}>
          {showPassword ? credential.password : '••••••••••••'}
        </span>
        <div style={{ display: 'flex', gap: '2px', flexShrink: 0 }}>
          <button
            onClick={e => { e.stopPropagation(); setShowPassword(v => !v); }}
            style={{ background: 'none', border: 'none', cursor: 'pointer', color: showPassword ? '#F90000' : '#2A3040', padding: '2px', display: 'flex', alignItems: 'center', transition: 'color 150ms ease' }}
            title={showPassword ? 'Hide' : 'Reveal'}
            onMouseEnter={e => (e.currentTarget.style.color = '#F90000')}
            onMouseLeave={e => { if (!showPassword) e.currentTarget.style.color = '#2A3040'; }}
          >
            {showPassword ? <EyeOff size={12} /> : <Eye size={12} />}
          </button>
          <button
            onClick={e => { e.stopPropagation(); handleCopy('password', credential.password); }}
            style={{ background: 'none', border: 'none', cursor: 'pointer', color: copied === 'password' ? '#10B981' : '#2A3040', padding: '2px', display: 'flex', alignItems: 'center', transition: 'color 150ms ease' }}
            title="Copy password"
            onMouseEnter={e => { if (copied !== 'password') e.currentTarget.style.color = '#F90000'; }}
            onMouseLeave={e => { if (copied !== 'password') e.currentTarget.style.color = '#2A3040'; }}
          >
            {copied === 'password' ? <Check size={12} /> : <Copy size={12} />}
          </button>
        </div>
      </div>

      {/* Bottom strip */}
      <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginTop: '12px', paddingTop: '10px', borderTop: '1px solid rgba(54,60,69,0.3)' }}>
        <span style={{ fontSize: '9px', color: '#2A3040', fontFamily: "'JetBrains Mono', monospace", letterSpacing: '0.12em' }}>
          // {credential.category.toUpperCase()}
        </span>
        <SyncStatusBadge status={credential.syncStatus as any} />
      </div>

      {/* Hover accent line */}
      {isHovered && (
        <div style={{ position: 'absolute', bottom: 0, left: 0, right: 0, height: '1px', backgroundColor: 'rgba(249,0,0,0.3)' }} />
      )}
    </div>
  );
}

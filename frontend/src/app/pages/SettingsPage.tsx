import React, { useState, useRef } from 'react';
import { Shield, Smartphone, WifiOff, RefreshCw, Key, Trash2, User, Camera, Check, AlertCircle } from 'lucide-react';
import { ArcaButton } from '../components/ui/ArcaButton';
import { useArca } from '../context/ArcaContext';
import { apiClient } from '@/services/apiClient';

export function SettingsPage() {
  const { isOnline, setIsOnline, triggerSync, username, avatarUrl, updateProfile, userEmail } = useArca();
  const [autoSync, setAutoSync] = useState(true);
  const [clearConfirm, setClearConfirm] = useState(false);

  // Profile state
  const [editUsername, setEditUsername] = useState(username);
  const [editAvatarUrl, setEditAvatarUrl] = useState<string | null>(avatarUrl);
  const [profileSaved, setProfileSaved] = useState(false);
  const [profileError, setProfileError] = useState<string | null>(null);
  const [isSavingProfile, setIsSavingProfile] = useState(false);
  const [avatarHovered, setAvatarHovered] = useState(false);
  const fileInputRef = useRef<HTMLInputElement>(null);

  const handleAvatarChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (!file) return;
    const reader = new FileReader();
    reader.onload = (ev) => {
      setEditAvatarUrl(ev.target?.result as string);
    };
    reader.readAsDataURL(file);
  };

  const handleProfileSave = async () => {
    const trimmedUsername = editUsername.trim() || username;
    
    try {
      setIsSavingProfile(true);
      setProfileError(null);
      setProfileSaved(false);
      
      const response = await apiClient.updateProfile(trimmedUsername, editAvatarUrl || undefined);
      
      if (response.success) {
        updateProfile(trimmedUsername, editAvatarUrl);
        setProfileSaved(true);
        setTimeout(() => setProfileSaved(false), 2000);
      } else {
        setProfileError(response.message || 'Failed to update profile');
        setTimeout(() => setProfileError(null), 3000);
      }
    } catch (error) {
      const errorMsg = error instanceof Error ? error.message : 'Failed to update profile';
      setProfileError(errorMsg);
      setTimeout(() => setProfileError(null), 3000);
    } finally {
      setIsSavingProfile(false);
    }
  };

  const handleRemoveAvatar = () => {
    setEditAvatarUrl(null);
    if (fileInputRef.current) fileInputRef.current.value = '';
  };

  const SectionLabel = ({ num, label, icon }: { num: string; label: string; icon: React.ReactNode }) => (
    <div style={{ display: 'flex', alignItems: 'center', gap: '10px', marginBottom: '12px' }}>
      <div style={{ width: '20px', height: '1px', backgroundColor: '#F90000' }} />
      <span style={{ fontFamily: "'JetBrains Mono', monospace", fontSize: '10px', color: '#F90000', letterSpacing: '0.2em' }}>
        {num} // {label}
      </span>
    </div>
  );

  return (
    <div style={{ padding: '32px 36px', maxWidth: '680px' }}>

      {/* Page header */}
      <div style={{ marginBottom: '36px' }}>
        <div style={{ display: 'flex', alignItems: 'center', gap: '10px', marginBottom: '8px' }}>
          <div style={{ width: '20px', height: '1px', backgroundColor: '#F90000' }} />
          <span style={{ fontFamily: "'JetBrains Mono', monospace", fontSize: '10px', color: '#F90000', letterSpacing: '0.2em' }}>
            // SETTINGS
          </span>
        </div>
        <h1 style={{ fontFamily: "'Ubuntu', sans-serif", fontSize: '22px', fontWeight: 700, color: '#F1F5F9', margin: '0 0 6px', letterSpacing: '-0.01em', textTransform: 'uppercase' }}>
          Vault Configuration
        </h1>
        <p style={{ fontFamily: "'JetBrains Mono', monospace", fontSize: '11px', color: '#2A3040', margin: 0, letterSpacing: '0.06em' }}>
          SECURITY PREFERENCES AND ACCOUNT SETTINGS
        </p>
      </div>

      {/* ── PROFILE ── */}
      <div style={{ marginBottom: '36px' }}>
        <SectionLabel num="01" label="PROFILE" icon={<User size={14} />} />
        <div style={{ border: '1px solid rgba(54,60,69,0.5)', backgroundColor: '#0D1014' }}>
          <div style={{ padding: '24px', display: 'flex', alignItems: 'flex-start', gap: '24px' }}>
            {/* Avatar */}
            <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', gap: '8px', flexShrink: 0 }}>
              <div
                style={{ position: 'relative', cursor: 'pointer' }}
                onClick={() => fileInputRef.current?.click()}
                onMouseEnter={() => setAvatarHovered(true)}
                onMouseLeave={() => setAvatarHovered(false)}
              >
                <div style={{
                  width: '72px', height: '72px', borderRadius: '3px',
                  backgroundColor: '#F90000', overflow: 'hidden',
                  display: 'flex', alignItems: 'center', justifyContent: 'center',
                  border: avatarHovered ? '2px solid #F90000' : '2px solid rgba(54,60,69,0.5)',
                  boxShadow: avatarHovered ? '0 0 16px rgba(249,0,0,0.3)' : 'none',
                  transition: 'all 200ms ease',
                }}>
                  {editAvatarUrl ? (
                    <img src={editAvatarUrl} alt="Profile" style={{ width: '100%', height: '100%', objectFit: 'cover' }} />
                  ) : (
                    <span style={{ fontFamily: "'Ubuntu', sans-serif", fontSize: '26px', fontWeight: 700, color: '#0A0C0F', lineHeight: 1 }}>
                      {editUsername ? editUsername.charAt(0).toUpperCase() : 'A'}
                    </span>
                  )}
                </div>
                {avatarHovered && (
                  <div style={{
                    position: 'absolute', inset: 0, borderRadius: '3px',
                    backgroundColor: 'rgba(0,0,0,0.6)',
                    display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'center', gap: '2px',
                  }}>
                    <Camera size={18} color="#F1F5F9" />
                    <span style={{ fontFamily: "'JetBrains Mono', monospace", fontSize: '8px', color: '#F1F5F9', letterSpacing: '0.1em' }}>UPLOAD</span>
                  </div>
                )}
              </div>
              <input ref={fileInputRef} type="file" accept="image/*" onChange={handleAvatarChange} style={{ display: 'none' }} />
              {editAvatarUrl && (
                <button
                  onClick={handleRemoveAvatar}
                  style={{
                    background: 'none', border: 'none', cursor: 'pointer',
                    fontFamily: "'JetBrains Mono', monospace", fontSize: '9px', color: '#2A3040',
                    letterSpacing: '0.1em', padding: 0, transition: 'color 150ms ease',
                  }}
                  onMouseEnter={e => (e.currentTarget.style.color = '#F90000')}
                  onMouseLeave={e => (e.currentTarget.style.color = '#2A3040')}
                >
                  REMOVE
                </button>
              )}
            </div>

            {/* Fields */}
            <div style={{ flex: 1, display: 'flex', flexDirection: 'column', gap: '14px' }}>
              <div>
                <label style={{ fontFamily: "'JetBrains Mono', monospace", fontSize: '9px', fontWeight: 500, color: '#2A3040', display: 'block', marginBottom: '6px', letterSpacing: '0.18em' }}>
                  DISPLAY NAME
                </label>
                <input
                  value={editUsername}
                  onChange={e => setEditUsername(e.target.value)}
                  placeholder="Your display name"
                  style={{
                    width: '100%', boxSizing: 'border-box',
                    backgroundColor: '#0A0C0F', border: '1px solid rgba(54,60,69,0.5)', borderRadius: '3px',
                    color: '#F1F5F9', padding: '9px 12px',
                    fontFamily: "'Ubuntu', sans-serif", fontSize: '13px', outline: 'none',
                    transition: 'border-color 200ms ease, box-shadow 200ms ease',
                  }}
                  onFocus={e => { e.target.style.borderColor = '#F90000'; e.target.style.boxShadow = '0 0 0 2px rgba(249,0,0,0.12)'; }}
                  onBlur={e => { e.target.style.borderColor = 'rgba(54,60,69,0.5)'; e.target.style.boxShadow = 'none'; }}
                />
              </div>
              <div>
                <label style={{ fontFamily: "'JetBrains Mono', monospace", fontSize: '9px', fontWeight: 500, color: '#2A3040', display: 'block', marginBottom: '6px', letterSpacing: '0.18em' }}>
                  EMAIL ADDRESS
                </label>
                <input
                  value={userEmail}
                  readOnly
                  style={{
                    width: '100%', boxSizing: 'border-box',
                    backgroundColor: '#07080A', border: '1px solid rgba(54,60,69,0.25)', borderRadius: '3px',
                    color: '#2A3040', padding: '9px 12px',
                    fontFamily: "'JetBrains Mono', monospace", fontSize: '11px', outline: 'none',
                    cursor: 'default', letterSpacing: '0.04em',
                  }}
                />
              </div>
              <div style={{ display: 'flex', justifyContent: 'flex-end', gap: '12px', alignItems: 'center' }}>
                {profileError && (
                  <div style={{ display: 'flex', alignItems: 'center', gap: '6px', color: '#F90000', fontFamily: "'JetBrains Mono', monospace", fontSize: '11px' }}>
                    <AlertCircle size={14} />
                    <span>{profileError}</span>
                  </div>
                )}
                <ArcaButton
                  variant={profileSaved ? 'secondary' : 'primary'}
                  size="sm"
                  onClick={handleProfileSave}
                  disabled={isSavingProfile || profileSaved}
                  leftIcon={profileSaved ? <Check size={13} /> : undefined}
                >
                  {isSavingProfile ? 'SAVING...' : profileSaved ? 'SAVED' : 'SAVE CHANGES'}
                </ArcaButton>
              </div>
            </div>
          </div>
        </div>
      </div>

      {/* Dynamic sections */}
      {[
        {
          num: '02',
          title: 'SECURITY',
          icon: <Shield size={14} />,
          items: [
            {
              label: 'Auto-lock Timeout',
              desc: 'Lock vault after period of inactivity',
              control: (
                <select style={{ backgroundColor: '#0D1014', border: '1px solid rgba(54,60,69,0.5)', borderRadius: '3px', color: '#F1F5F9', padding: '6px 10px', fontFamily: "'JetBrains Mono', monospace", fontSize: '11px', letterSpacing: '0.06em', outline: 'none', cursor: 'pointer' }}>
                  <option>5 MIN</option>
                  <option>15 MIN</option>
                  <option>30 MIN</option>
                  <option>1 HOUR</option>
                  <option>NEVER</option>
                </select>
              ),
            },
            {
              label: 'Master Password Hint',
              desc: 'Stored locally only — never transmitted',
              control: <ArcaButton variant="secondary" size="sm">SET HINT</ArcaButton>,
            },
          ],
        },
        {
          num: '03',
          title: 'SYNC',
          icon: <RefreshCw size={14} />,
          items: [
            {
              label: 'Auto-sync',
              desc: 'Automatically sync when changes are detected',
              control: (
                <button
                  onClick={() => setAutoSync(v => !v)}
                  style={{
                    width: '40px', height: '22px', borderRadius: '2px', border: 'none', cursor: 'pointer',
                    backgroundColor: autoSync ? '#F90000' : 'rgba(54,60,69,0.5)', transition: 'all 200ms ease',
                    position: 'relative',
                  }}
                >
                  <div style={{
                    position: 'absolute', top: '2px',
                    left: autoSync ? '20px' : '2px',
                    width: '18px', height: '18px', borderRadius: '1px', backgroundColor: '#F1F5F9',
                    transition: 'left 200ms ease',
                  }} />
                </button>
              ),
            },
            {
              label: 'Simulate Offline Mode',
              desc: isOnline ? 'Currently ONLINE — click to simulate offline' : 'Currently OFFLINE (simulated)',
              control: (
                <ArcaButton
                  variant={isOnline ? 'secondary' : 'danger'}
                  size="sm"
                  onClick={() => setIsOnline(!isOnline)}
                  leftIcon={<WifiOff size={13} />}
                >
                  {isOnline ? 'GO OFFLINE' : 'GO ONLINE'}
                </ArcaButton>
              ),
            },
            {
              label: 'Force Sync',
              desc: 'Manually trigger full vault synchronization',
              control: <ArcaButton variant="secondary" size="sm" leftIcon={<RefreshCw size={13} />} onClick={triggerSync}>SYNC NOW</ArcaButton>,
            },
          ],
        },
        {
          num: '04',
          title: 'DEVICES',
          icon: <Smartphone size={14} />,
          items: [
            { label: 'MacBook Pro 16"', desc: 'Desktop · Last active Mar 16, 2026', control: <span style={{ fontSize: '10px', color: '#10B981', fontFamily: "'JetBrains Mono', monospace", letterSpacing: '0.1em' }}>ACTIVE</span> },
            { label: 'Pixel 7 Pro', desc: 'Mobile · Last active Mar 17, 2026', control: <span style={{ fontSize: '10px', color: '#F90000', fontFamily: "'JetBrains Mono', monospace", letterSpacing: '0.1em' }}>THIS DEVICE</span> },
          ],
        },
        {
          num: '05',
          title: 'DANGER ZONE',
          icon: <Trash2 size={14} />,
          danger: true,
          items: [
            {
              label: 'Delete Account',
              desc: 'Permanently destroy account and all vault data. Irreversible.',
              control: (
                clearConfirm
                  ? <div style={{ display: 'flex', gap: '8px' }}>
                      <ArcaButton variant="secondary" size="sm" onClick={() => setClearConfirm(false)}>CANCEL</ArcaButton>
                      <ArcaButton variant="danger" size="sm">CONFIRM DELETE</ArcaButton>
                    </div>
                  : <ArcaButton variant="danger" size="sm" onClick={() => setClearConfirm(true)}>DELETE ACCOUNT</ArcaButton>
              ),
            },
          ],
        },
      ].map(section => (
        <div key={section.title} style={{ marginBottom: '36px' }}>
          <SectionLabel num={section.num} label={section.title} icon={section.icon} />
          <div style={{
            border: `1px solid ${section.danger ? 'rgba(249,0,0,0.2)' : 'rgba(54,60,69,0.5)'}`,
            backgroundColor: '#0D1014',
            overflow: 'hidden',
          }}>
            {section.items.map((item, i) => (
              <div
                key={item.label}
                style={{
                  display: 'flex', alignItems: 'center', justifyContent: 'space-between',
                  padding: '16px 20px',
                  borderBottom: i < section.items.length - 1 ? '1px solid rgba(54,60,69,0.3)' : 'none',
                }}
              >
                <div>
                  <div style={{ fontFamily: "'Ubuntu', sans-serif", fontSize: '13px', fontWeight: 500, color: '#94A3B8', marginBottom: '2px' }}>
                    {item.label}
                  </div>
                  <div style={{ fontFamily: "'JetBrains Mono', monospace", fontSize: '10px', color: '#2A3040', letterSpacing: '0.04em' }}>
                    {item.desc}
                  </div>
                </div>
                <div style={{ flexShrink: 0, marginLeft: '16px' }}>{item.control}</div>
              </div>
            ))}
          </div>
        </div>
      ))}
    </div>
  );
}

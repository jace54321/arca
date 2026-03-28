import React, { useState, useEffect } from 'react';
import { X, Dices, Check } from 'lucide-react';
import { ArcaButton } from './ArcaButton';
import { ArcaInput, ArcaTextarea } from './ArcaInput';
import { PasswordStrengthMeter } from './PasswordStrengthMeter';
import { Credential } from '../../data/mockData';

interface AddEditPanelProps {
  isOpen: boolean;
  credential: Credential | null;
  onClose: () => void;
  onSave: (data: any) => void;
  onDelete?: (id: string) => void;
  isOffline?: boolean;
}

type Category = 'Work' | 'Personal' | 'Social' | 'Other';
const CATEGORIES: Category[] = ['Work', 'Personal', 'Social', 'Other'];

function generatePassword(): string {
  const chars = 'abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789!@#$%^&*';
  return Array.from({ length: 20 }, () => chars[Math.floor(Math.random() * chars.length)]).join('');
}

export function AddEditPanel({ isOpen, credential, onClose, onSave, onDelete, isOffline = false }: AddEditPanelProps) {
  const [siteName, setSiteName] = useState('');
  const [url, setUrl] = useState('');
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [category, setCategory] = useState<Category>('Personal');
  const [notes, setNotes] = useState('');
  const [saving, setSaving] = useState(false);
  const [saved, setSaved] = useState(false);
  const [flashPassword, setFlashPassword] = useState(false);

  const isEdit = !!credential;

  useEffect(() => {
    if (credential) {
      setSiteName(credential.siteName);
      setUrl(credential.url);
      setUsername(credential.username);
      setPassword(credential.password);
      setCategory(credential.category);
      setNotes(credential.notes || '');
    } else {
      setSiteName(''); setUrl(''); setUsername(''); setPassword('');
      setCategory('Personal'); setNotes('');
    }
    setSaved(false);
  }, [credential, isOpen]);

  const handleGenerate = () => {
    setFlashPassword(true);
    setPassword(generatePassword());
    setTimeout(() => setFlashPassword(false), 200);
  };

  const handleSave = async () => {
    setSaving(true);
    await new Promise(r => setTimeout(r, 800));
    onSave({ siteName, url, username, password, category, notes });
    setSaving(false);
    setSaved(true);
    setTimeout(() => { setSaved(false); onClose(); }, 1000);
  };

  return (
    <>
      {/* Backdrop */}
      <div
        onClick={onClose}
        style={{
          position: 'fixed', inset: 0, zIndex: 40,
          backgroundColor: 'rgba(20,24,30,0.7)',
          backdropFilter: 'blur(4px)',
          opacity: isOpen ? 1 : 0,
          pointerEvents: isOpen ? 'auto' : 'none',
          transition: 'opacity 300ms cubic-bezier(0.4,0,0.2,1)',
        }}
      />
      {/* Panel */}
      <div
        style={{
          position: 'fixed', top: 0, right: 0, bottom: 0, zIndex: 50,
          width: '420px',
          backgroundColor: '#1F2329',
          borderLeft: '1px solid #363C45',
          transform: isOpen ? 'translateX(0)' : 'translateX(100%)',
          transition: 'transform 300ms cubic-bezier(0.4,0,0.2,1)',
          display: 'flex',
          flexDirection: 'column',
          overflowY: 'auto',
        }}
      >
        {/* Header */}
        <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', padding: '20px 24px', borderBottom: '1px solid #363C45', flexShrink: 0 }}>
          <h3 style={{ fontFamily: "'Ubuntu', sans-serif", fontSize: '20px', fontWeight: 500, color: '#F1F5F9', margin: 0 }}>
            {isEdit ? 'Edit Entry' : 'New Entry'}
          </h3>
          <button onClick={onClose} style={{ background: 'none', border: 'none', cursor: 'pointer', color: '#94A3B8', display: 'flex', padding: '4px', borderRadius: '4px', textTransform: 'none', letterSpacing: 'normal' }}
            onMouseEnter={e => (e.currentTarget.style.color = '#F90000')}
            onMouseLeave={e => (e.currentTarget.style.color = '#94A3B8')}>
            <X size={20} />
          </button>
        </div>

        {/* Form */}
        <div style={{ flex: 1, padding: '24px', display: 'flex', flexDirection: 'column', gap: '20px', overflowY: 'auto' }}>
          <ArcaInput
            label="Site Name"
            placeholder="e.g. GitHub, Google, Netflix"
            value={siteName}
            onChange={e => setSiteName(e.target.value)}
          />
          <ArcaInput
            label="URL"
            placeholder="https://…"
            value={url}
            onChange={e => setUrl(e.target.value)}
            type="url"
          />
          <ArcaInput
            label="Username / Email"
            placeholder="username or email@example.com"
            value={username}
            onChange={e => setUsername(e.target.value)}
          />

          {/* Password + Generate */}
          <div style={{ display: 'flex', flexDirection: 'column', gap: '8px' }}>
            <div style={{ display: 'flex', gap: '8px', alignItems: 'flex-end' }}>
              <div style={{ flex: 1 }}>
                <ArcaInput
                  label="Password"
                  isPassword
                  value={password}
                  onChange={e => setPassword(e.target.value)}
                  style={{ backgroundColor: flashPassword ? 'rgba(249,0,0,0.10)' : '#1F2329' }}
                />
              </div>
              <button
                onClick={handleGenerate}
                title="Generate password"
                style={{
                  height: '44px', width: '44px', flexShrink: 0,
                  display: 'flex', alignItems: 'center', justifyContent: 'center',
                  backgroundColor: '#272C33', border: '1px solid #363C45', borderRadius: '6px',
                  cursor: 'pointer', color: '#94A3B8', transition: 'all 200ms ease',
                  textTransform: 'none', letterSpacing: 'normal',
                }}
                onMouseEnter={e => { e.currentTarget.style.borderColor = '#F90000'; e.currentTarget.style.color = '#F90000'; }}
                onMouseLeave={e => { e.currentTarget.style.borderColor = '#363C45'; e.currentTarget.style.color = '#94A3B8'; }}
              >
                <Dices size={18} />
              </button>
            </div>
            <PasswordStrengthMeter password={password} />
          </div>

          {/* Category */}
          <div style={{ display: 'flex', flexDirection: 'column', gap: '8px' }}>
            <label style={{ fontFamily: "'Ubuntu', sans-serif", fontSize: '13px', fontWeight: 500, letterSpacing: '0.04em', textTransform: 'uppercase', color: '#94A3B8' }}>
              Category
            </label>
            <div style={{ display: 'flex', gap: '8px' }}>
              {CATEGORIES.map(cat => (
                <button
                  key={cat}
                  onClick={() => setCategory(cat)}
                  style={{
                    padding: '8px 14px', borderRadius: '6px', cursor: 'pointer',
                    backgroundColor: category === cat ? '#F90000' : '#272C33',
                    border: `1px solid ${category === cat ? '#F90000' : '#363C45'}`,
                    color: category === cat ? '#14181E' : '#94A3B8',
                    fontSize: '13px', fontFamily: "'Ubuntu', sans-serif", fontWeight: 500,
                    transition: 'all 200ms ease', textTransform: 'none', letterSpacing: 'normal',
                  }}
                >
                  {cat}
                </button>
              ))}
            </div>
          </div>

          <ArcaTextarea
            label="Notes"
            placeholder="Optional notes…"
            value={notes}
            onChange={e => setNotes(e.target.value)}
            rows={3}
          />
        </div>

        {/* Footer */}
        <div style={{
          padding: '16px 24px', borderTop: '1px solid #363C45',
          display: 'flex', alignItems: 'center', justifyContent: 'space-between',
          flexShrink: 0, backgroundColor: '#1F2329',
        }}>
          {isEdit && onDelete ? (
            <ArcaButton variant="danger" size="sm" onClick={() => { onDelete(credential!.id); onClose(); }}>
              Delete
            </ArcaButton>
          ) : <div />}
          <div style={{ display: 'flex', gap: '8px' }}>
            <ArcaButton variant="secondary" size="sm" onClick={onClose}>Cancel</ArcaButton>
            <ArcaButton
              variant="primary"
              size="sm"
              loading={saving}
              onClick={handleSave}
              disabled={!siteName || !password}
              leftIcon={saved ? <Check size={14} /> : undefined}
            >
              {saved ? 'Saved!' : isOffline ? 'Save Locally' : 'Save Entry'}
            </ArcaButton>
          </div>
        </div>
      </div>

      <style>{`
        @keyframes fadeInUp {
          from { opacity: 0; transform: translateY(12px); }
          to { opacity: 1; transform: translateY(0); }
        }
      `}</style>
    </>
  );
}

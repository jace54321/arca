import React, { useState, useRef } from 'react';
import { Lock, Unlock } from 'lucide-react';
import { ArcaButton } from '../components/ui/ArcaButton';
import { ArcaInput } from '../components/ui/ArcaInput';
import { useArca } from '../context/ArcaContext';
import { useNavigate } from 'react-router';

export function UnlockPage() {
  const [password, setPassword] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [unlocked, setUnlocked] = useState(false);
  const [shake, setShake] = useState(false);
  const [rotate, setRotate] = useState(0);
  const { unlock, setActiveScreen } = useArca();
  const navigate = useNavigate();

  const handleUnlock = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!password) return;
    setLoading(true);
    setError('');
    const ok = await unlock(password);
    setLoading(false);
    if (ok) {
      setRotate(45);
      setUnlocked(true);
      setTimeout(() => {
        setActiveScreen('vault');
        navigate('/vault');
      }, 700);
    } else {
      setShake(true);
      setRotate(-15);
      setError('Incorrect password. Try again.');
      setTimeout(() => { setShake(false); setRotate(0); }, 700);
    }
  };

  return (
    <div style={{
      minHeight: '100vh',
      backgroundColor: '#14181E',
      display: 'flex',
      alignItems: 'center',
      justifyContent: 'center',
      padding: '24px',
      backgroundImage: `
        radial-gradient(ellipse at 50% 50%, rgba(249,0,0,0.04) 0%, transparent 60%),
        linear-gradient(rgba(255,255,255,0.015) 1px, transparent 1px),
        linear-gradient(90deg, rgba(255,255,255,0.015) 1px, transparent 1px)
      `,
      backgroundSize: '100% 100%, 32px 32px, 32px 32px',
    }}>
      <div
        style={{
          width: '100%',
          maxWidth: '440px',
          display: 'flex',
          flexDirection: 'column',
          alignItems: 'center',
          gap: '32px',
          animation: unlocked ? 'dissolveUp 500ms ease forwards' : undefined,
        }}
      >
        {/* Lock icon */}
        <div style={{ position: 'relative', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
          <div style={{
            position: 'absolute',
            width: '120px', height: '120px',
            borderRadius: '9999px',
            background: 'radial-gradient(circle, rgba(249,0,0,0.18) 0%, transparent 70%)',
          }} />
          <div
            style={{
              transition: 'transform 400ms cubic-bezier(0.34,1.56,0.64,1)',
              transform: `rotate(${rotate}deg)`,
              animation: shake ? 'lockShake 0.6s ease' : undefined,
              color: unlocked ? '#10B981' : '#F90000',
            }}
          >
            {unlocked ? <Unlock size={64} /> : <Lock size={64} />}
          </div>
        </div>

        {/* Text */}
        <div style={{ textAlign: 'center' }}>
          <h1 style={{ fontFamily: "'Ubuntu', sans-serif", fontSize: '28px', fontWeight: 700, color: '#F1F5F9', margin: '0 0 8px', letterSpacing: '-0.02em' }}>
            Unlock Your Vault
          </h1>
          <p style={{ fontFamily: "'Ubuntu', sans-serif", fontSize: '14px', color: '#94A3B8', margin: 0, maxWidth: '360px', lineHeight: 1.6 }}>
            Enter your Master Password to decrypt your credentials on this device.
          </p>
        </div>

        {/* Card */}
        <div style={{
          width: '100%',
          backgroundColor: '#1F2329',
          border: '1px solid #363C45',
          borderRadius: '12px',
          padding: '32px',
        }}>
          <form onSubmit={handleUnlock} style={{ display: 'flex', flexDirection: 'column', gap: '20px' }}>
            <ArcaInput
              label="Master Password"
              isPassword
              large
              placeholder="••••••••••••••••"
              value={password}
              onChange={e => setPassword(e.target.value)}
              error={error}
              autoFocus
            />
            <ArcaButton type="submit" variant="primary" fullWidth loading={loading} size="lg">
              Unlock Vault
            </ArcaButton>
          </form>
          <p style={{ fontSize: '12px', color: '#475569', margin: '16px 0 0', fontFamily: "'Ubuntu', sans-serif", lineHeight: 1.6, textAlign: 'center' }}>
            This is the only way to unlock your vault. It is never sent anywhere.
          </p>
        </div>

        {/* Back to login */}
        <button
          onClick={() => navigate('/login')}
          style={{ background: 'none', border: 'none', cursor: 'pointer', color: '#475569', fontSize: '13px', fontFamily: "'Ubuntu', sans-serif", textTransform: 'none', letterSpacing: 'normal' }}
          onMouseEnter={e => (e.currentTarget.style.color = '#94A3B8')}
          onMouseLeave={e => (e.currentTarget.style.color = '#475569')}
        >
          ← Back to login
        </button>
      </div>

      <style>{`
        @keyframes lockShake {
          0%, 100% { transform: rotate(0deg); }
          20% { transform: rotate(-20deg); }
          40% { transform: rotate(10deg); }
          60% { transform: rotate(-10deg); }
          80% { transform: rotate(5deg); }
        }
        @keyframes dissolveUp {
          from { opacity: 1; transform: translateY(0); }
          to { opacity: 0; transform: translateY(-24px); }
        }
      `}</style>
    </div>
  );
}

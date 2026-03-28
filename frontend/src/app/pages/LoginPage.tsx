import React, { useState, useRef } from 'react';
import { Shield, WifiOff, Key, Lock } from 'lucide-react';
import { ArcaButton } from '../components/ui/ArcaButton';
import { ArcaInput } from '../components/ui/ArcaInput';
import { PasswordStrengthMeter } from '../components/ui/PasswordStrengthMeter';
import { useArca } from '../context/ArcaContext';
import { useNavigate } from 'react-router';
import { apiClient } from '@/services/apiClient';
import { supabase } from '@/lib/supabaseClient';

type Tab = 'login' | 'register';

export function LoginPage() {
  const [tab, setTab] = useState<Tab>('login');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [agreed, setAgreed] = useState(false);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [shake, setShake] = useState(false);
  const [masterFocused, setMasterFocused] = useState(false);
  const cardRef = useRef<HTMLDivElement>(null);
  const { login, setActiveScreen } = useArca();
  const navigate = useNavigate();

  const handleLogin = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!email || !password) { 
      setError('Please fill in all fields.'); 
      triggerShake(); 
      return; 
    }
    
    setLoading(true);
    setError('');
    
    try {
      // Login with Supabase + master password for vault
      const ok = await login(email, password, password);
      if (ok) {
        setActiveScreen('unlock');
        navigate('/unlock');
      } else {
        setError('Invalid credentials. Please try again.');
        triggerShake();
      }
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Login failed');
      triggerShake();
    } finally {
      setLoading(false);
    }
  };

  const handleRegister = async (e: React.FormEvent) => {
    e.preventDefault();
    
    if (!email || !password || !confirmPassword) { 
      setError('Please fill in all fields.'); 
      return; 
    }
    if (password !== confirmPassword) { 
      setError('Passwords do not match.'); 
      return; 
    }
    if (!agreed) { 
      setError('You must acknowledge the master password warning.'); 
      return; 
    }
    
    setLoading(true);
    setError('');
    
    try {
      // First, sign up with Supabase to get the user ID
      const { data: authData, error: authError } = await supabase.auth.signUp({
        email,
        password: password, // Use master password as Supabase password too
      });
      
      if (authError) throw authError;
      if (!authData.user?.id) throw new Error('Failed to get Supabase user ID');
      
      // Then register user with backend (email + master password + Supabase user ID)
      const registerResponse = await apiClient.register(email, password, authData.user.id);
      if (!registerResponse.success) {
        throw new Error(registerResponse.message || 'Registration failed');
      }
      
      setError('Account created! You can now log in.');
      setTab('login');
      setPassword('');
      setConfirmPassword('');
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Registration failed');
      triggerShake();
    } finally {
      setLoading(false);
    }
  };

  const triggerShake = () => {
    setShake(true);
    setTimeout(() => setShake(false), 700);
  };

  return (
    <div style={{
      minHeight: '100vh', backgroundColor: '#14181E',
      display: 'flex', alignItems: 'center', justifyContent: 'center',
      padding: '24px',
      backgroundImage: `
        radial-gradient(ellipse at 50% 50%, rgba(249,0,0,0.03) 0%, transparent 70%),
        linear-gradient(rgba(255,255,255,0.015) 1px, transparent 1px),
        linear-gradient(90deg, rgba(255,255,255,0.015) 1px, transparent 1px)
      `,
      backgroundSize: '100% 100%, 32px 32px, 32px 32px',
    }}>
      <div
        ref={cardRef}
        style={{
          width: '100%',
          maxWidth: '900px',
          display: 'flex',
          backgroundColor: '#1F2329',
          border: '1px solid #363C45',
          borderRadius: '16px',
          overflow: 'hidden',
          boxShadow: masterFocused ? '0 0 0 2px rgba(249,0,0,0.20), 0 32px 80px rgba(0,0,0,0.6)' : '0 32px 80px rgba(0,0,0,0.5)',
          transition: 'box-shadow 300ms ease',
          animation: shake ? 'shake 0.6s ease' : undefined,
        }}
      >
        {/* Left panel */}
        <div style={{
          flex: '1', padding: '48px', display: 'flex', flexDirection: 'column', justifyContent: 'center',
          borderRight: '1px solid #363C45',
          background: 'linear-gradient(135deg, #1F2329 0%, #14181E 100%)',
        }}>
          {/* Wordmark */}
          <div style={{ marginBottom: '32px' }}>
            <div style={{ display: 'flex', alignItems: 'center', gap: '10px', marginBottom: '12px' }}>
              <div style={{ width: '36px', height: '36px', borderRadius: '8px', backgroundColor: '#F90000', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
                <Lock size={20} color="#14181E" />
              </div>
              <h1 style={{ fontFamily: "'Ubuntu', sans-serif", fontSize: '32px', fontWeight: 700, color: '#F1F5F9', margin: 0, letterSpacing: '-0.02em' }}>
                <span>Arc</span><span style={{ color: '#F90000' }}>a</span>
              </h1>
            </div>
            <p style={{ fontFamily: "'Ubuntu', sans-serif", fontSize: '18px', fontWeight: 300, color: '#94A3B8', margin: 0, lineHeight: 1.5 }}>
              Your vault. Your key. No one else's.
            </p>
          </div>

          {/* Trust pillars */}
          <div style={{ display: 'flex', flexDirection: 'column', gap: '16px' }}>
            {[
              { icon: <Shield size={16} />, text: 'Zero-knowledge architecture' },
              { icon: <WifiOff size={16} />, text: 'Works offline, syncs silently' },
              { icon: <Key size={16} />, text: 'Only you hold the key' },
            ].map((pillar, i) => (
              <div key={i} style={{ display: 'flex', alignItems: 'center', gap: '12px' }}>
                <div style={{ color: '#F90000', flexShrink: 0 }}>{pillar.icon}</div>
                <span style={{ fontFamily: "'Ubuntu', sans-serif", fontSize: '14px', color: '#94A3B8' }}>{pillar.text}</span>
              </div>
            ))}
          </div>

          {/* Decorative lock */}
          <div style={{ marginTop: '48px', opacity: 0.08 }}>
            <Lock size={80} color="#F90000" />
          </div>
        </div>

        {/* Right panel */}
        <div style={{ width: '400px', padding: '48px', display: 'flex', flexDirection: 'column' }}>
          {/* Tab switcher */}
          <div style={{ display: 'flex', borderBottom: '1px solid #363C45', marginBottom: '32px' }}>
            {(['login', 'register'] as Tab[]).map(t => (
              <button
                key={t}
                onClick={() => { setTab(t); setError(''); }}
                style={{
                  flex: 1, padding: '12px 0',
                  fontFamily: "'Ubuntu', sans-serif", fontSize: '14px', fontWeight: 500,
                  background: 'none', border: 'none', cursor: 'pointer',
                  color: tab === t ? '#F1F5F9' : '#475569',
                  borderBottom: tab === t ? '2px solid #F90000' : '2px solid transparent',
                  marginBottom: '-1px',
                  transition: 'all 200ms ease',
                  textTransform: 'none', letterSpacing: 'normal',
                }}
              >
                {t === 'login' ? 'Log In' : 'Create Account'}
              </button>
            ))}
          </div>

          {tab === 'login' ? (
            <form onSubmit={handleLogin} style={{ display: 'flex', flexDirection: 'column', gap: '20px' }}>
              <ArcaInput
                label="Email Address"
                type="email"
                placeholder="you@example.com"
                value={email}
                onChange={e => setEmail(e.target.value)}
              />
              <ArcaInput
                label="Master Password"
                isPassword
                large
                placeholder="Enter your master password"
                value={password}
                onChange={e => setPassword(e.target.value)}
                onFocus={() => setMasterFocused(true)}
                onBlur={() => setMasterFocused(false)}
              />
              {error && <p style={{ fontSize: '13px', color: '#EF4444', margin: 0, fontFamily: "'Ubuntu', sans-serif" }}>{error}</p>}
              <ArcaButton type="submit" variant="primary" fullWidth loading={loading} size="md">
                Log In
              </ArcaButton>
              <p style={{ fontSize: '12px', color: '#475569', margin: 0, fontFamily: "'Ubuntu', sans-serif", lineHeight: 1.6, textAlign: 'center' }}>
                Forgot your Master Password? Unfortunately, we can't help — it's never sent to us.
              </p>
            </form>
          ) : (
            <form onSubmit={handleRegister} style={{ display: 'flex', flexDirection: 'column', gap: '20px' }}>
              <ArcaInput
                label="Email Address"
                type="email"
                placeholder="you@example.com"
                value={email}
                onChange={e => setEmail(e.target.value)}
              />
              <div style={{ display: 'flex', flexDirection: 'column', gap: '8px' }}>
                <ArcaInput
                  label="Master Password"
                  isPassword
                  large
                  placeholder="Create a strong master password"
                  value={password}
                  onChange={e => setPassword(e.target.value)}
                  onFocus={() => setMasterFocused(true)}
                  onBlur={() => setMasterFocused(false)}
                />
                <PasswordStrengthMeter password={password} />
              </div>
              <ArcaInput
                label="Confirm Master Password"
                isPassword
                placeholder="Confirm your master password"
                value={confirmPassword}
                onChange={e => setConfirmPassword(e.target.value)}
                error={confirmPassword && password !== confirmPassword ? 'Passwords do not match' : ''}
              />
              <label style={{ display: 'flex', gap: '10px', alignItems: 'flex-start', cursor: 'pointer', textTransform: 'none', letterSpacing: 'normal', fontSize: '13px', color: '#94A3B8', fontWeight: 400 }}>
                <input
                  type="checkbox"
                  checked={agreed}
                  onChange={e => setAgreed(e.target.checked)}
                  style={{ marginTop: '2px', accentColor: '#F90000', flexShrink: 0 }}
                />
                <span>I understand my Master Password cannot be recovered. I will keep it safe.</span>
              </label>
              {error && <p style={{ fontSize: '13px', color: '#EF4444', margin: 0, fontFamily: "'Ubuntu', sans-serif" }}>{error}</p>}
              <ArcaButton type="submit" variant="primary" fullWidth loading={loading} disabled={!agreed} size="md">
                Create Account
              </ArcaButton>
            </form>
          )}
        </div>
      </div>

      <style>{`
        @keyframes shake {
          0%, 100% { transform: translateX(0); }
          15% { transform: translateX(-4px); }
          30% { transform: translateX(4px); }
          45% { transform: translateX(-3px); }
          60% { transform: translateX(3px); }
          75% { transform: translateX(-2px); }
          90% { transform: translateX(2px); }
        }
      `}</style>
    </div>
  );
}

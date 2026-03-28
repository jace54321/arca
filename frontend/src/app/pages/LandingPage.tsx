import React, { useEffect, useRef, useState } from 'react';
import { useNavigate } from 'react-router';
import { Shield, Wifi, Key, Lock, ChevronDown, ArrowRight, Zap, EyeOff } from 'lucide-react';

const HERO_BG = 'https://images.unsplash.com/photo-1580046939256-c377c5b099f1?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=M3w3Nzg4Nzd8MHwxfHNlYXJjaHwxfHxoYWNrZXIlMjBkYXJrJTIwaG9vZGVkJTIwZmlndXJlJTIwY3liZXJwdW5rJTIwc2lsaG91ZXR0ZXxlbnwxfHx8fDE3NzM5MDM3ODN8MA&ixlib=rb-4.1.0&q=80&w=1080';

export function LandingPage() {
  const navigate = useNavigate();
  const [scrollY, setScrollY] = useState(0);
  const [glitching, setGlitching] = useState(false);
  const [soundOn, setSoundOn] = useState(false);
  const heroRef = useRef<HTMLDivElement>(null);
  const glitchTimer = useRef<ReturnType<typeof setTimeout> | null>(null);

  // Scroll tracking
  useEffect(() => {
    const handler = () => setScrollY(window.scrollY);
    window.addEventListener('scroll', handler, { passive: true });
    return () => window.removeEventListener('scroll', handler);
  }, []);

  // Periodic glitch
  useEffect(() => {
    const trigger = () => {
      setGlitching(true);
      setTimeout(() => setGlitching(false), 180);
      glitchTimer.current = setTimeout(trigger, 4000 + Math.random() * 5000);
    };
    glitchTimer.current = setTimeout(trigger, 2000);
    return () => { if (glitchTimer.current) clearTimeout(glitchTimer.current); };
  }, []);

  const scrollToFeatures = () => {
    document.getElementById('features')?.scrollIntoView({ behavior: 'smooth' });
  };

  const parallaxY = scrollY * 0.35;

  return (
    <div style={{ backgroundColor: '#0A0C0F', minHeight: '100vh', overflowX: 'hidden' }}>

      {/* ── NAV ── */}
      <nav style={{
        position: 'fixed', top: 0, left: 0, right: 0, zIndex: 100,
        display: 'flex', alignItems: 'center', justifyContent: 'space-between',
        padding: '0 40px', height: '56px',
        backgroundColor: scrollY > 40 ? 'rgba(10,12,15,0.92)' : 'transparent',
        backdropFilter: scrollY > 40 ? 'blur(12px)' : 'none',
        borderBottom: scrollY > 40 ? '1px solid rgba(249,0,0,0.12)' : '1px solid rgba(255,255,255,0.04)',
        transition: 'all 400ms ease',
      }}>
        <div style={{ display: 'flex', alignItems: 'center', gap: '10px' }}>
          <div style={{ width: '26px', height: '26px', borderRadius: '5px', backgroundColor: '#F90000', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
            <Lock size={14} color="#0A0C0F" />
          </div>
          <span style={{ fontFamily: "'Ubuntu', sans-serif", fontSize: '18px', fontWeight: 700, color: '#F1F5F9', letterSpacing: '-0.01em' }}>
            Arc<span style={{ color: '#F90000' }}>a</span>
          </span>
        </div>

        <div style={{ display: 'flex', alignItems: 'center', gap: '36px' }}>
          {['ABOUT', 'FEATURES', 'SECURITY', 'HOW IT WORKS'].map(item => (
            <button
              key={item}
              onClick={() => document.getElementById(item.toLowerCase().replace(/ /g, '-'))?.scrollIntoView({ behavior: 'smooth' })}
              style={{
                background: 'none', border: 'none', cursor: 'pointer',
                fontFamily: "'JetBrains Mono', monospace", fontSize: '11px', fontWeight: 500,
                color: '#475569', letterSpacing: '0.12em',
                transition: 'color 200ms ease', padding: 0, textTransform: 'uppercase',
              }}
              onMouseEnter={e => (e.currentTarget.style.color = '#F1F5F9')}
              onMouseLeave={e => (e.currentTarget.style.color = '#475569')}
            >
              {item}
            </button>
          ))}
        </div>

        <div style={{ display: 'flex', alignItems: 'center', gap: '16px' }}>
          <button
            onClick={() => navigate('/login')}
            style={{
              background: 'none', border: 'none', cursor: 'pointer',
              fontFamily: "'JetBrains Mono', monospace", fontSize: '11px',
              color: '#F90000', letterSpacing: '0.12em', textTransform: 'uppercase', padding: 0,
              transition: 'opacity 200ms ease',
            }}
            onMouseEnter={e => (e.currentTarget.style.opacity = '0.7')}
            onMouseLeave={e => (e.currentTarget.style.opacity = '1')}
          >
            LOG IN ›
          </button>
          <button
            onClick={() => navigate('/login')}
            style={{
              padding: '8px 20px', borderRadius: '4px',
              backgroundColor: '#F90000', border: 'none', cursor: 'pointer',
              fontFamily: "'JetBrains Mono', monospace", fontSize: '11px', fontWeight: 500,
              color: '#0A0C0F', letterSpacing: '0.12em', textTransform: 'uppercase',
              transition: 'all 200ms ease',
            }}
            onMouseEnter={e => { e.currentTarget.style.backgroundColor = '#D40000'; e.currentTarget.style.boxShadow = '0 0 20px rgba(249,0,0,0.35)'; }}
            onMouseLeave={e => { e.currentTarget.style.backgroundColor = '#F90000'; e.currentTarget.style.boxShadow = 'none'; }}
          >
            JOIN NOW ›
          </button>
        </div>
      </nav>

      {/* ── HERO ── */}
      <section
        ref={heroRef}
        style={{
          position: 'relative', height: '100vh', overflow: 'hidden',
          display: 'flex', flexDirection: 'column',
        }}
      >
        {/* Background image with parallax */}
        <div style={{
          position: 'absolute', inset: '-10%',
          backgroundImage: `url(${HERO_BG})`,
          backgroundSize: 'cover', backgroundPosition: 'center',
          transform: `translateY(${parallaxY}px)`,
          filter: 'brightness(0.28) contrast(1.2)',
          willChange: 'transform',
        }} />

        {/* Grid overlay */}
        <div style={{
          position: 'absolute', inset: 0, pointerEvents: 'none',
          backgroundImage: `
            linear-gradient(rgba(249,0,0,0.04) 1px, transparent 1px),
            linear-gradient(90deg, rgba(249,0,0,0.04) 1px, transparent 1px)
          `,
          backgroundSize: '80px 80px',
          opacity: glitching ? 0.5 : 1,
          transition: 'opacity 80ms',
        }} />

        {/* Scan-line texture */}
        <div style={{
          position: 'absolute', inset: 0, pointerEvents: 'none',
          backgroundImage: 'repeating-linear-gradient(0deg, rgba(0,0,0,0.18) 0px, rgba(0,0,0,0.18) 1px, transparent 1px, transparent 3px)',
        }} />

        {/* Vignette */}
        <div style={{
          position: 'absolute', inset: 0, pointerEvents: 'none',
          background: 'radial-gradient(ellipse at center, transparent 30%, rgba(0,0,0,0.75) 100%)',
        }} />

        {/* ── HUD: top-right ── */}
        <div style={{
          position: 'absolute', top: '70px', right: '32px',
          fontFamily: "'JetBrains Mono', monospace", fontSize: '11px', color: 'rgba(241,245,249,0.3)',
          textAlign: 'right', lineHeight: 1.8, letterSpacing: '0.08em', userSelect: 'none',
        }}>
          <div>++</div>
          <div style={{ cursor: 'pointer' }} onClick={scrollToFeatures}>SCROLL</div>
        </div>

        {/* ── HUD: bottom-left ── */}
        <div style={{ position: 'absolute', bottom: '100px', left: '40px' }}>
          <div style={{
            fontFamily: "'JetBrains Mono', monospace", fontSize: '10px',
            color: 'rgba(241,245,249,0.35)', letterSpacing: '0.18em', marginBottom: '16px',
            textTransform: 'uppercase', userSelect: 'none',
          }}>
            ARCA — ZERO-KNOWLEDGE VAULT SYSTEM
          </div>
          <div style={{
            fontFamily: "'Ubuntu', sans-serif",
            fontSize: 'clamp(52px, 7vw, 96px)',
            fontWeight: 700,
            color: '#F1F5F9',
            lineHeight: 0.9,
            letterSpacing: '-0.03em',
            textTransform: 'uppercase',
            opacity: glitching ? 0.85 : 1,
            transition: 'opacity 80ms',
          }}>
            <div style={{ display: 'flex', alignItems: 'center', gap: '16px' }}>
              <ArcaLogo />
              YOUR
            </div>
            <div>VAULT.</div>
            <div>
              YOUR <span style={{ color: '#F90000' }}>KEY.</span>
            </div>
          </div>
        </div>

        {/* ── HUD: bottom-right ── */}
        <div style={{
          position: 'absolute', bottom: '32px', right: '32px',
          display: 'flex', flexDirection: 'column', alignItems: 'flex-end', gap: '4px',
          userSelect: 'none',
        }}>
          <div style={{ fontFamily: "'JetBrains Mono', monospace", fontSize: '9px', color: 'rgba(241,245,249,0.3)', letterSpacing: '0.12em' }}>SOUND</div>
          <button
            onClick={() => setSoundOn(v => !v)}
            style={{
              display: 'flex', alignItems: 'center', gap: '8px', background: 'none', border: 'none',
              cursor: 'pointer', padding: 0,
              fontFamily: "'JetBrains Mono', monospace", fontSize: '10px', letterSpacing: '0.1em',
            }}
          >
            <span style={{ color: !soundOn ? 'rgba(241,245,249,0.5)' : 'rgba(241,245,249,0.2)' }}>OFF</span>
            <div style={{ width: '28px', height: '2px', backgroundColor: 'rgba(241,245,249,0.15)' }}>
              <div style={{ width: soundOn ? '100%' : '0%', height: '100%', backgroundColor: '#F90000', transition: 'width 200ms ease' }} />
            </div>
            <span style={{ color: soundOn ? '#F90000' : 'rgba(241,245,249,0.2)' }}>ON</span>
          </button>
        </div>

        {/* ── Corner brackets ── */}
        <CornerBrackets />

        {/* Scroll indicator */}
        <button
          onClick={scrollToFeatures}
          style={{
            position: 'absolute', bottom: '32px', left: '50%', transform: 'translateX(-50%)',
            background: 'none', border: 'none', cursor: 'pointer', color: 'rgba(241,245,249,0.3)',
            display: 'flex', flexDirection: 'column', alignItems: 'center', gap: '6px',
            animation: 'float 2.4s ease-in-out infinite',
          }}
        >
          <ChevronDown size={18} />
        </button>
      </section>

      {/* ── FEATURES ── */}
      <section id="features" style={{ backgroundColor: '#0D1014', padding: '120px 40px' }}>
        <div style={{ maxWidth: '1100px', margin: '0 auto' }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: '12px', marginBottom: '64px' }}>
            <div style={{ width: '32px', height: '1px', backgroundColor: '#F90000' }} />
            <span style={{ fontFamily: "'JetBrains Mono', monospace", fontSize: '11px', color: '#F90000', letterSpacing: '0.18em', textTransform: 'uppercase' }}>
              01 // FEATURES
            </span>
          </div>
          <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(300px, 1fr))', gap: '2px' }}>
            {[
              { icon: <Shield size={20} />, title: 'ZERO-KNOWLEDGE', label: 'Architecture', desc: "Your master password never leaves your device. End-to-end encrypted. We can't read it — nobody can.", num: '01' },
              { icon: <Wifi size={20} />, title: 'OFFLINE-FIRST', label: 'Resilience', desc: 'Full vault access even when the network drops. Changes queue silently, sync when you reconnect.', num: '02' },
              { icon: <Key size={20} />, title: 'MASTER KEY', label: 'Control', desc: 'One password unlocks everything. AES-256 encrypted vault with PBKDF2 key derivation. Military-grade.', num: '03' },
              { icon: <Zap size={20} />, title: 'INSTANT SYNC', label: 'Multi-device', desc: 'Seamless sync across all your devices. Real-time conflict resolution. Your vault, everywhere.', num: '04' },
              { icon: <EyeOff size={20} />, title: 'PRIVATE BY', label: 'Design', desc: 'No analytics, no telemetry, no ads. Arca sees nothing. Your data belongs only to you.', num: '05' },
              { icon: <Lock size={20} />, title: 'AUTO-LOCK', label: 'Security', desc: 'Vault locks automatically on inactivity. Configurable timeout. Biometric unlock on mobile.', num: '06' },
            ].map((f, i) => (
              <FeatureCard key={i} {...f} />
            ))}
          </div>
        </div>
      </section>

      {/* ── SECURITY SECTION ── */}
      <section id="security" style={{ backgroundColor: '#0A0C0F', padding: '120px 40px', position: 'relative', overflow: 'hidden' }}>
        <div style={{
          position: 'absolute', inset: 0, pointerEvents: 'none',
          backgroundImage: `
            linear-gradient(rgba(249,0,0,0.025) 1px, transparent 1px),
            linear-gradient(90deg, rgba(249,0,0,0.025) 1px, transparent 1px)
          `,
          backgroundSize: '60px 60px',
        }} />
        <div style={{ maxWidth: '1100px', margin: '0 auto', position: 'relative' }}>
          <div style={{ display: 'flex', gap: '80px', alignItems: 'center', flexWrap: 'wrap' }}>
            <div style={{ flex: '1', minWidth: '280px' }}>
              <div style={{ display: 'flex', alignItems: 'center', gap: '12px', marginBottom: '32px' }}>
                <div style={{ width: '32px', height: '1px', backgroundColor: '#F90000' }} />
                <span style={{ fontFamily: "'JetBrains Mono', monospace", fontSize: '11px', color: '#F90000', letterSpacing: '0.18em', textTransform: 'uppercase' }}>
                  02 // SECURITY
                </span>
              </div>
              <h2 style={{
                fontFamily: "'Ubuntu', sans-serif", fontSize: 'clamp(36px, 4vw, 56px)',
                fontWeight: 700, color: '#F1F5F9', margin: '0 0 24px',
                textTransform: 'uppercase', lineHeight: 0.95, letterSpacing: '-0.02em',
              }}>
                BUILT FOR<br /><span style={{ color: '#F90000' }}>ADVERSARIAL</span><br />CONDITIONS.
              </h2>
              <p style={{ fontFamily: "'Ubuntu', sans-serif", fontSize: '15px', color: '#64748B', lineHeight: 1.75, margin: '0 0 40px' }}>
                Arca treats every network as hostile. Your credentials are encrypted before they ever leave your RAM.
                Even if our servers were compromised, attackers would find nothing but ciphertext.
              </p>
              <button
                onClick={() => navigate('/login')}
                style={{
                  display: 'inline-flex', alignItems: 'center', gap: '10px',
                  padding: '14px 28px', backgroundColor: '#F90000', border: 'none',
                  borderRadius: '4px', cursor: 'pointer',
                  fontFamily: "'JetBrains Mono', monospace", fontSize: '12px', fontWeight: 500,
                  color: '#0A0C0F', letterSpacing: '0.1em', textTransform: 'uppercase',
                  transition: 'all 200ms ease',
                }}
                onMouseEnter={e => { e.currentTarget.style.boxShadow = '0 0 30px rgba(249,0,0,0.4)'; e.currentTarget.style.backgroundColor = '#D40000'; }}
                onMouseLeave={e => { e.currentTarget.style.boxShadow = 'none'; e.currentTarget.style.backgroundColor = '#F90000'; }}
              >
                ENTER VAULT <ArrowRight size={14} />
              </button>
            </div>
            <div style={{ flex: '1', minWidth: '280px' }}>
              {[
                { label: 'Encryption', value: 'AES-256-GCM' },
                { label: 'Key Derivation', value: 'PBKDF2 / 600k rounds' },
                { label: 'Transport', value: 'TLS 1.3 only' },
                { label: 'Storage', value: 'Local-first, E2E encrypted' },
                { label: 'Server knowledge', value: 'ZERO' },
                { label: 'Password recovery', value: 'IMPOSSIBLE (by design)' },
              ].map((spec, i) => (
                <div key={i} style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', padding: '18px 0', borderBottom: '1px solid rgba(54,60,69,0.6)' }}>
                  <span style={{ fontFamily: "'Ubuntu', sans-serif", fontSize: '13px', color: '#475569' }}>{spec.label}</span>
                  <span style={{
                    fontFamily: "'JetBrains Mono', monospace", fontSize: '12px',
                    color: spec.value.includes('ZERO') || spec.value.includes('IMPOSSIBLE') ? '#F90000' : '#F1F5F9',
                    letterSpacing: '0.04em',
                  }}>
                    {spec.value}
                  </span>
                </div>
              ))}
            </div>
          </div>
        </div>
      </section>

      {/* ── HOW IT WORKS ── */}
      <section id="how-it-works" style={{ backgroundColor: '#0D1014', padding: '120px 40px' }}>
        <div style={{ maxWidth: '1100px', margin: '0 auto' }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: '12px', marginBottom: '64px' }}>
            <div style={{ width: '32px', height: '1px', backgroundColor: '#F90000' }} />
            <span style={{ fontFamily: "'JetBrains Mono', monospace", fontSize: '11px', color: '#F90000', letterSpacing: '0.18em', textTransform: 'uppercase' }}>
              03 // HOW IT WORKS
            </span>
          </div>
          <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(220px, 1fr))', gap: '40px' }}>
            {[
              { step: '01', title: 'Create Account', desc: "Enter your email and create a master password. It's hashed locally — we never see it." },
              { step: '02', title: 'Unlock Vault', desc: 'On each session, re-enter your master password to decrypt and unlock your vault.' },
              { step: '03', title: 'Store & Manage', desc: 'Add credentials, generate strong passwords, organize by category. All encrypted at rest.' },
              { step: '04', title: 'Sync Silently', desc: 'Changes sync across devices automatically. Go offline anytime — Arca keeps working.' },
            ].map((s, i) => (
              <div key={i} style={{ position: 'relative', paddingTop: '16px' }}>
                <div style={{
                  position: 'absolute', top: 0, left: 0,
                  fontFamily: "'JetBrains Mono', monospace", fontSize: '72px', fontWeight: 700,
                  color: 'rgba(249,0,0,0.06)', lineHeight: 1, letterSpacing: '-0.03em', userSelect: 'none',
                }}>
                  {s.step}
                </div>
                <div style={{ position: 'relative', paddingTop: '40px' }}>
                  <div style={{ fontFamily: "'JetBrains Mono', monospace", fontSize: '10px', color: '#F90000', letterSpacing: '0.18em', marginBottom: '10px', textTransform: 'uppercase' }}>
                    STEP {s.step}
                  </div>
                  <div style={{ fontFamily: "'Ubuntu', sans-serif", fontSize: '16px', fontWeight: 700, color: '#F1F5F9', marginBottom: '12px', textTransform: 'uppercase', letterSpacing: '-0.01em' }}>
                    {s.title}
                  </div>
                  <div style={{ fontFamily: "'Ubuntu', sans-serif", fontSize: '13px', color: '#475569', lineHeight: 1.75 }}>
                    {s.desc}
                  </div>
                </div>
              </div>
            ))}
          </div>
        </div>
      </section>

      {/* ── FINAL CTA ── */}
      <section style={{ backgroundColor: '#0A0C0F', padding: '140px 40px', position: 'relative', overflow: 'hidden', textAlign: 'center' }}>
        <div style={{
          position: 'absolute', top: '50%', left: '50%', transform: 'translate(-50%,-50%)',
          width: '600px', height: '600px', borderRadius: '50%',
          background: 'radial-gradient(circle, rgba(249,0,0,0.08) 0%, transparent 70%)',
          pointerEvents: 'none',
        }} />
        <div style={{ position: 'relative', maxWidth: '700px', margin: '0 auto' }}>
          <div style={{ fontFamily: "'JetBrains Mono', monospace", fontSize: '10px', color: '#F90000', letterSpacing: '0.22em', marginBottom: '24px', textTransform: 'uppercase' }}>
            — YOUR VAULT AWAITS —
          </div>
          <h2 style={{
            fontFamily: "'Ubuntu', sans-serif", fontSize: 'clamp(40px, 6vw, 80px)',
            fontWeight: 700, color: '#F1F5F9', margin: '0 0 24px',
            textTransform: 'uppercase', lineHeight: 0.92, letterSpacing: '-0.03em',
          }}>
            TAKE BACK<br /><span style={{ color: '#F90000' }}>CONTROL.</span>
          </h2>
          <p style={{ fontFamily: "'Ubuntu', sans-serif", fontSize: '15px', color: '#475569', lineHeight: 1.75, margin: '0 0 48px' }}>
            Join Arca. One master password. Complete ownership. No compromises.
          </p>
          <div style={{ display: 'flex', justifyContent: 'center', gap: '16px', flexWrap: 'wrap' }}>
            <button
              onClick={() => navigate('/login')}
              style={{
                padding: '16px 40px', backgroundColor: '#F90000', border: 'none',
                borderRadius: '4px', cursor: 'pointer',
                fontFamily: "'JetBrains Mono', monospace", fontSize: '12px', fontWeight: 500,
                color: '#0A0C0F', letterSpacing: '0.12em', textTransform: 'uppercase',
                display: 'inline-flex', alignItems: 'center', gap: '10px',
                transition: 'all 200ms ease',
              }}
              onMouseEnter={e => { e.currentTarget.style.boxShadow = '0 0 40px rgba(249,0,0,0.45)'; e.currentTarget.style.backgroundColor = '#D40000'; }}
              onMouseLeave={e => { e.currentTarget.style.boxShadow = 'none'; e.currentTarget.style.backgroundColor = '#F90000'; }}
            >
              CREATE FREE ACCOUNT <ArrowRight size={14} />
            </button>
            <button
              onClick={() => navigate('/login')}
              style={{
                padding: '16px 40px', backgroundColor: 'transparent',
                border: '1px solid rgba(249,0,0,0.3)', borderRadius: '4px', cursor: 'pointer',
                fontFamily: "'JetBrains Mono', monospace", fontSize: '12px', fontWeight: 500,
                color: '#F90000', letterSpacing: '0.12em', textTransform: 'uppercase',
                transition: 'all 200ms ease',
              }}
              onMouseEnter={e => { e.currentTarget.style.borderColor = '#F90000'; e.currentTarget.style.boxShadow = '0 0 20px rgba(249,0,0,0.15)'; }}
              onMouseLeave={e => { e.currentTarget.style.borderColor = 'rgba(249,0,0,0.3)'; e.currentTarget.style.boxShadow = 'none'; }}
            >
              LOG IN
            </button>
          </div>
        </div>
      </section>

      {/* ── FOOTER ── */}
      <footer style={{
        backgroundColor: '#07080A', padding: '32px 40px',
        borderTop: '1px solid rgba(54,60,69,0.5)',
        display: 'flex', justifyContent: 'space-between', alignItems: 'center', flexWrap: 'wrap', gap: '12px',
      }}>
        <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
          <div style={{ width: '20px', height: '20px', borderRadius: '4px', backgroundColor: '#F90000', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
            <Lock size={11} color="#0A0C0F" />
          </div>
          <span style={{ fontFamily: "'Ubuntu', sans-serif", fontSize: '14px', fontWeight: 700, color: '#F1F5F9' }}>
            Arc<span style={{ color: '#F90000' }}>a</span>
          </span>
        </div>
        <span style={{ fontFamily: "'JetBrains Mono', monospace", fontSize: '10px', color: '#2A3040', letterSpacing: '0.08em' }}>
          ZERO-KNOWLEDGE · AES-256 · LOCAL-FIRST · © 2026 ARCA VAULT SYSTEMS
        </span>
        <div style={{ display: 'flex', gap: '4px', alignItems: 'center' }}>
          <div style={{ width: '6px', height: '6px', borderRadius: '50%', backgroundColor: '#10B981' }} />
          <span style={{ fontFamily: "'JetBrains Mono', monospace", fontSize: '10px', color: '#2A3040', letterSpacing: '0.08em' }}>
            ALL SYSTEMS OPERATIONAL
          </span>
        </div>
      </footer>

      <style>{`
        @keyframes float {
          0%, 100% { transform: translateX(-50%) translateY(0); }
          50% { transform: translateX(-50%) translateY(8px); }
        }
        @keyframes bracketPulse {
          0%, 100% { opacity: 0.25; }
          50% { opacity: 0.45; }
        }
        ::selection { background: rgba(249,0,0,0.25); color: #F1F5F9; }
        html { scroll-behavior: smooth; }
      `}</style>
    </div>
  );
}

/* ── Sub-components ── */

function CornerBrackets() {
  const S = 24;
  const baseStyle: React.CSSProperties = {
    position: 'absolute', width: S, height: S, pointerEvents: 'none',
    animation: 'bracketPulse 3s ease-in-out infinite',
  };
  const line: React.CSSProperties = { position: 'absolute', backgroundColor: 'rgba(249,0,0,0.5)' };

  const Bracket = ({ pos }: { pos: 'tl' | 'tr' | 'bl' | 'br' }) => {
    const isT = pos.startsWith('t'), isL = pos.endsWith('l');
    return (
      <div style={{
        ...baseStyle,
        top: isT ? 80 : undefined, bottom: !isT ? 80 : undefined,
        left: isL ? 32 : undefined, right: !isL ? 32 : undefined,
      }}>
        <div style={{ ...line, width: S, height: 1, top: isT ? 0 : 'auto', bottom: isT ? 'auto' : 0 }} />
        <div style={{ ...line, width: 1, height: S, left: isL ? 0 : 'auto', right: isL ? 'auto' : 0 }} />
      </div>
    );
  };

  return (
    <>
      <Bracket pos="tl" /><Bracket pos="tr" /><Bracket pos="bl" /><Bracket pos="br" />
    </>
  );
}

function ArcaLogo() {
  return (
    <div style={{
      width: '64px', height: '64px', borderRadius: '12px',
      backgroundColor: '#F90000', display: 'inline-flex', alignItems: 'center', justifyContent: 'center',
      flexShrink: 0,
    }}>
      <Lock size={32} color="#0A0C0F" />
    </div>
  );
}

function FeatureCard({ icon, title, label, desc, num }: { icon: React.ReactNode; title: string; label: string; desc: string; num: string }) {
  const [hovered, setHovered] = useState(false);
  return (
    <div
      onMouseEnter={() => setHovered(true)}
      onMouseLeave={() => setHovered(false)}
      style={{
        padding: '36px 32px', position: 'relative', cursor: 'default',
        backgroundColor: hovered ? '#111418' : 'transparent',
        border: `1px solid ${hovered ? 'rgba(249,0,0,0.25)' : 'rgba(54,60,69,0.5)'}`,
        transition: 'all 250ms ease',
      }}
    >
      <div style={{
        position: 'absolute', top: '16px', right: '20px',
        fontFamily: "'JetBrains Mono', monospace", fontSize: '11px',
        color: hovered ? 'rgba(249,0,0,0.4)' : 'rgba(54,60,69,0.8)',
        letterSpacing: '0.08em', transition: 'color 250ms ease',
      }}>
        {num}
      </div>
      <div style={{
        width: '40px', height: '40px', borderRadius: '6px', marginBottom: '20px',
        backgroundColor: hovered ? 'rgba(249,0,0,0.12)' : 'rgba(54,60,69,0.3)',
        display: 'flex', alignItems: 'center', justifyContent: 'center',
        color: hovered ? '#F90000' : '#475569', transition: 'all 250ms ease',
      }}>
        {icon}
      </div>
      <div style={{ fontFamily: "'Ubuntu', sans-serif", fontSize: '16px', fontWeight: 700, color: '#F1F5F9', textTransform: 'uppercase', letterSpacing: '-0.01em', lineHeight: 1 }}>
        {title}
      </div>
      <div style={{ fontFamily: "'JetBrains Mono', monospace", fontSize: '10px', color: '#F90000', letterSpacing: '0.1em', marginBottom: '14px', marginTop: '2px' }}>
        // {label.toUpperCase()}
      </div>
      <div style={{ fontFamily: "'Ubuntu', sans-serif", fontSize: '13px', color: '#475569', lineHeight: 1.75 }}>
        {desc}
      </div>
      <div style={{
        position: 'absolute', bottom: 0, left: 0,
        height: '2px', backgroundColor: '#F90000',
        width: hovered ? '100%' : '0%', transition: 'width 300ms ease',
      }} />
    </div>
  );
}

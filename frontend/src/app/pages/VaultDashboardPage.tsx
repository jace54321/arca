import { useState, useMemo } from 'react';
import { Search, Plus, Lock } from 'lucide-react';
import { Sidebar } from '../components/layout/Sidebar';
import { CredentialCard } from '../components/ui/CredentialCard';
import { AddEditPanel } from '../components/ui/AddEditPanel';
import { ArcaButton } from '../components/ui/ArcaButton';
import { Outlet } from 'react-router';
import { useArca } from '../context/ArcaContext';
import { Credential } from '@/types';

export function VaultLayout() {
  const { isOnline } = useArca();

  return (
    <div style={{ display: 'flex', minHeight: '100vh', backgroundColor: '#0A0C0F' }}>
      <Sidebar />
      <main style={{ flex: 1, overflowY: 'auto', minWidth: 0, position: 'relative' }}>
        {/* Subtle grid overlay on entire main area */}
        <div style={{
          position: 'fixed', inset: 0, pointerEvents: 'none', zIndex: 0,
          backgroundImage: `
            linear-gradient(rgba(249,0,0,0.025) 1px, transparent 1px),
            linear-gradient(90deg, rgba(249,0,0,0.025) 1px, transparent 1px)
          `,
          backgroundSize: '60px 60px',
        }} />
        {/* Scan-line texture */}
        <div style={{
          position: 'fixed', inset: 0, pointerEvents: 'none', zIndex: 0,
          backgroundImage: 'repeating-linear-gradient(0deg, rgba(0,0,0,0.06) 0px, rgba(0,0,0,0.06) 1px, transparent 1px, transparent 4px)',
        }} />

        {!isOnline && (
          <div style={{
            position: 'relative', zIndex: 1,
            padding: '9px 24px', display: 'flex', alignItems: 'center', gap: '10px',
            backgroundColor: 'rgba(249,0,0,0.06)',
            borderBottom: '1px solid rgba(249,0,0,0.2)',
          }}>
            <span style={{ color: '#F90000', fontSize: '10px', fontFamily: "'JetBrains Mono', monospace", letterSpacing: '0.12em' }}>
              ⚡ OFFLINE — CHANGES WILL SYNC ON RECONNECT
            </span>
          </div>
        )}
        <div style={{ position: 'relative', zIndex: 1 }}>
          <Outlet />
        </div>
      </main>
    </div>
  );
}

export function VaultDashboardPage() {
  const { credentials, addCredential, updateCredential, deleteCredential, isOnline } = useArca();
  const [search, setSearch] = useState('');
  const [panelOpen, setPanelOpen] = useState(false);
  const [editingCredential, setEditingCredential] = useState<Credential | null>(null);

  const filtered = useMemo(() => {
    if (!search.trim()) return credentials;
    const q = search.toLowerCase();
    return credentials.filter(c =>
      c.siteName.toLowerCase().includes(q) ||
      c.username.toLowerCase().includes(q) ||
      c.url.toLowerCase().includes(q)
    );
  }, [credentials, search]);

  const handleEdit = (credential: Credential) => {
    setEditingCredential(credential);
    setPanelOpen(true);
  };

  const handleAdd = () => {
    setEditingCredential(null);
    setPanelOpen(true);
  };

  const handleSave = async (data: any) => {
    try {
      if (editingCredential) {
        await updateCredential(editingCredential.id, data);
      } else {
        await addCredential(data);
      }
      setPanelOpen(false);
    } catch (error) {
      console.error('Save failed:', error);
    }
  };

  const handleDelete = async (id: string) => {
    try {
      await deleteCredential(id);
    } catch (error) {
      console.error('Delete failed:', error);
    }
  };

  return (
    <div style={{ padding: '32px 36px', maxWidth: '1200px' }}>

      {/* Tactical page header */}
      <div style={{ marginBottom: '28px' }}>
        <div style={{ display: 'flex', alignItems: 'center', gap: '10px', marginBottom: '6px' }}>
          <div style={{ width: '20px', height: '1px', backgroundColor: '#F90000' }} />
          <span style={{ fontFamily: "'JetBrains Mono', monospace", fontSize: '10px', color: '#F90000', letterSpacing: '0.2em' }}>
            // VAULT DASHBOARD
          </span>
        </div>

        {/* Stats strip */}
        <div style={{ display: 'flex', gap: '0', marginTop: '16px' }}>
          {[
            { label: 'TOTAL ENTRIES', value: credentials.length.toString().padStart(2, '0') },
            { label: 'PENDING SYNC', value: credentials.filter(c => c.syncStatus === 'syncing').length.toString().padStart(2, '0'), red: true },
            { label: 'OFFLINE MODIFIED', value: credentials.filter(c => c.offlineModified).length.toString().padStart(2, '0'), red: true },
          ].map((stat, i) => (
            <div
              key={stat.label}
              style={{
                padding: '12px 20px',
                borderTop: `1px solid ${stat.red && parseInt(stat.value) > 0 ? 'rgba(249,0,0,0.3)' : 'rgba(54,60,69,0.5)'}`,
                borderBottom: `1px solid ${stat.red && parseInt(stat.value) > 0 ? 'rgba(249,0,0,0.3)' : 'rgba(54,60,69,0.5)'}`,
                borderLeft: i === 0 ? `1px solid ${stat.red && parseInt(stat.value) > 0 ? 'rgba(249,0,0,0.3)' : 'rgba(54,60,69,0.5)'}` : 'none',
                borderRight: `1px solid ${stat.red && parseInt(stat.value) > 0 ? 'rgba(249,0,0,0.3)' : 'rgba(54,60,69,0.5)'}`,
              }}
            >
              <div style={{ fontFamily: "'JetBrains Mono', monospace", fontSize: '22px', fontWeight: 700, color: stat.red && parseInt(stat.value) > 0 ? '#F90000' : '#F1F5F9', lineHeight: 1, marginBottom: '4px' }}>
                {stat.value}
              </div>
              <div style={{ fontFamily: "'JetBrains Mono', monospace", fontSize: '9px', color: '#2A3040', letterSpacing: '0.15em' }}>
                {stat.label}
              </div>
            </div>
          ))}
        </div>
      </div>

      {/* Search + Add row */}
      <div style={{ display: 'flex', gap: '10px', alignItems: 'center', marginBottom: '24px' }}>
        <div style={{ flex: 1, position: 'relative' }}>
          <div style={{ position: 'absolute', left: '13px', top: '50%', transform: 'translateY(-50%)', color: '#2A3040', pointerEvents: 'none' }}>
            <Search size={14} />
          </div>
          <input
            placeholder="SEARCH ENTRIES…"
            value={search}
            onChange={e => setSearch(e.target.value)}
            style={{
              width: '100%', height: '40px', paddingLeft: '38px', paddingRight: '14px',
              backgroundColor: '#0D1014', border: '1px solid rgba(54,60,69,0.6)', borderRadius: '3px',
              color: '#F1F5F9', fontFamily: "'JetBrains Mono', monospace", fontSize: '11px',
              letterSpacing: '0.08em', outline: 'none', boxSizing: 'border-box',
              transition: 'border-color 200ms ease, box-shadow 200ms ease',
            }}
            onFocus={e => { e.target.style.borderColor = '#F90000'; e.target.style.boxShadow = '0 0 0 2px rgba(249,0,0,0.15)'; }}
            onBlur={e => { e.target.style.borderColor = 'rgba(54,60,69,0.6)'; e.target.style.boxShadow = 'none'; }}
          />
        </div>
        <ArcaButton variant="primary" leftIcon={<Plus size={14} />} onClick={handleAdd}>
          NEW ENTRY
        </ArcaButton>
      </div>

      {/* Grid */}
      {credentials.length === 0 ? (
        <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'center', minHeight: '300px', gap: '16px' }}>
          <div style={{ padding: '20px', border: '1px solid rgba(54,60,69,0.4)', borderRadius: '4px' }}>
            <Lock size={40} color="#1F2329" />
          </div>
          <div style={{ fontFamily: "'JetBrains Mono', monospace", fontSize: '11px', color: '#2A3040', letterSpacing: '0.12em' }}>
            VAULT IS EMPTY
          </div>
          <p style={{ fontFamily: "'Ubuntu', sans-serif", fontSize: '13px', color: '#475569', margin: 0 }}>
            Add your first password to get started.
          </p>
          <ArcaButton variant="primary" leftIcon={<Plus size={14} />} onClick={handleAdd}>
            NEW ENTRY
          </ArcaButton>
        </div>
      ) : filtered.length === 0 ? (
        <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'center', minHeight: '200px', gap: '8px' }}>
          <div style={{ fontFamily: "'JetBrains Mono', monospace", fontSize: '10px', color: '#2A3040', letterSpacing: '0.16em' }}>
            NO MATCH
          </div>
          <p style={{ fontFamily: "'Ubuntu', sans-serif", fontSize: '13px', color: '#475569', margin: 0 }}>
            No entries match "<span style={{ color: '#F1F5F9' }}>{search}</span>"
          </p>
        </div>
      ) : (
        <>
          <div style={{ fontFamily: "'JetBrains Mono', monospace", fontSize: '9px', color: '#2A3040', letterSpacing: '0.15em', marginBottom: '12px' }}>
            {filtered.length} RESULT{filtered.length !== 1 ? 'S' : ''} — ENCRYPTED AT REST
          </div>
          <div style={{
            display: 'grid',
            gridTemplateColumns: 'repeat(auto-fill, minmax(320px, 1fr))',
            gap: '1px',
            backgroundColor: 'rgba(54,60,69,0.3)',
          }}>
            {credentials.map((cred, index) => {
              const isMatch = !search.trim() || filtered.some(f => f.id === cred.id);
              return (
                <CredentialCard
                  key={cred.id}
                  credential={cred}
                  onEdit={handleEdit}
                  onDelete={handleDelete}
                  dimmed={!!search.trim() && !isMatch}
                  animationDelay={Math.min(index, 5) * 40}
                />
              );
            })}
          </div>
        </>
      )}

      <AddEditPanel
        isOpen={panelOpen}
        credential={editingCredential}
        onClose={() => setPanelOpen(false)}
        onSave={handleSave}
        onDelete={handleDelete}
        isOffline={!isOnline}
      />

      <style>{`
        @keyframes fadeInUp {
          from { opacity: 0; transform: translateY(10px); }
          to { opacity: 1; transform: translateY(0); }
        }
        input::placeholder { color: #2A3040; }
      `}</style>
    </div>
  );
}

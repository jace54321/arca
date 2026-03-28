import React, { useMemo } from 'react';

interface PasswordStrengthMeterProps {
  password: string;
}

function calculateStrength(password: string): number {
  if (!password) return 0;
  let score = 0;
  if (password.length >= 8) score++;
  if (password.length >= 14) score++;
  if (/[A-Z]/.test(password) && /[a-z]/.test(password)) score++;
  if (/[0-9]/.test(password)) score++;
  if (/[^A-Za-z0-9]/.test(password)) score++;
  return Math.min(4, Math.ceil(score * 0.8));
}

const labels = ['', 'Weak', 'Fair', 'Strong', 'Very Strong'];
const colors = ['#363C45', '#EF4444', '#FF4500', '#10B981', '#10B981'];
const activeColors = ['#363C45', '#EF4444', '#FF4500', '#10B981', '#22D3EE'];

export function PasswordStrengthMeter({ password }: PasswordStrengthMeterProps) {
  const strength = useMemo(() => calculateStrength(password), [password]);

  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: '6px' }}>
      <div style={{ display: 'flex', gap: '4px', alignItems: 'center' }}>
        {[1, 2, 3, 4].map((segment) => (
          <div
            key={segment}
            style={{
              flex: 1,
              height: '4px',
              borderRadius: '9999px',
              backgroundColor: strength >= segment ? activeColors[strength] : '#363C45',
              transition: 'background-color 300ms cubic-bezier(0.4,0,0.2,1)',
            }}
          />
        ))}
        <span style={{
          marginLeft: '8px',
          fontSize: '12px',
          fontFamily: "'Ubuntu', sans-serif",
          color: strength > 0 ? activeColors[strength] : '#475569',
          minWidth: '72px',
          transition: 'color 300ms ease',
        }}>
          {labels[strength] || (password.length > 0 ? 'Weak' : '')}
        </span>
      </div>
    </div>
  );
}

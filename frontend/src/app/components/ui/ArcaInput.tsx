import React, { useState, forwardRef } from 'react';
import { Eye, EyeOff } from 'lucide-react';

interface ArcaInputProps extends React.InputHTMLAttributes<HTMLInputElement> {
  label?: string;
  helperText?: string;
  error?: string;
  large?: boolean;
  isPassword?: boolean;
  rightElement?: React.ReactNode;
}

export const ArcaInput = forwardRef<HTMLInputElement, ArcaInputProps>(
  ({ label, helperText, error, large = false, isPassword = false, rightElement, style, id, ...props }, ref) => {
    const [showPassword, setShowPassword] = useState(false);
    const [isFocused, setIsFocused] = useState(false);
    const [flashRed, setFlashRed] = useState(false);

    const inputId = id || label?.toLowerCase().replace(/\s+/g, '-');

    const handleReveal = () => {
      if (!showPassword) {
        setFlashRed(true);
        setTimeout(() => setFlashRed(false), 200);
      }
      setShowPassword(!showPassword);
    };

    const inputType = isPassword ? (showPassword ? 'text' : 'password') : props.type;

    return (
      <div style={{ display: 'flex', flexDirection: 'column', gap: '6px', width: '100%' }}>
        {label && (
          <label
            htmlFor={inputId}
            style={{
              fontFamily: "'Ubuntu', sans-serif",
              fontSize: '13px',
              fontWeight: 500,
              letterSpacing: '0.04em',
              textTransform: 'uppercase',
              color: '#94A3B8',
              display: 'block',
            }}
          >
            {label}
          </label>
        )}
        <div style={{ position: 'relative', width: '100%' }}>
          <input
            ref={ref}
            id={inputId}
            {...props}
            type={inputType}
            onFocus={(e) => {
              setIsFocused(true);
              props.onFocus?.(e);
            }}
            onBlur={(e) => {
              setIsFocused(false);
              props.onBlur?.(e);
            }}
            style={{
              width: '100%',
              height: large ? '52px' : '44px',
              padding: '12px 16px',
              paddingRight: (isPassword || rightElement) ? '48px' : '16px',
              backgroundColor: flashRed ? 'rgba(249,0,0,0.08)' : '#1F2329',
              border: `1px solid ${error ? '#EF4444' : isFocused ? '#F90000' : '#363C45'}`,
              borderRadius: '6px',
              color: '#F1F5F9',
              fontFamily: (isPassword && showPassword) ? "'JetBrains Mono', monospace" : "'Ubuntu', sans-serif",
              fontSize: '14px',
              outline: 'none',
              boxShadow: isFocused ? (error ? '0 0 0 3px rgba(239,68,68,0.20)' : '0 0 0 3px rgba(249,0,0,0.20)') : 'none',
              transition: 'all 200ms cubic-bezier(0.4,0,0.2,1)',
              ...style,
            }}
          />
          {isPassword && (
            <button
              type="button"
              onClick={handleReveal}
              tabIndex={-1}
              style={{
                position: 'absolute',
                right: '12px',
                top: '50%',
                transform: 'translateY(-50%)',
                background: 'none',
                border: 'none',
                cursor: 'pointer',
                color: '#475569',
                display: 'flex',
                alignItems: 'center',
                padding: '4px',
                textTransform: 'none',
                letterSpacing: 'normal',
              }}
              onMouseEnter={e => (e.currentTarget.style.color = '#F90000')}
              onMouseLeave={e => (e.currentTarget.style.color = '#475569')}
            >
              {showPassword ? <EyeOff size={18} /> : <Eye size={18} />}
            </button>
          )}
          {!isPassword && rightElement && (
            <div style={{
              position: 'absolute',
              right: '12px',
              top: '50%',
              transform: 'translateY(-50%)',
              display: 'flex',
              alignItems: 'center',
            }}>
              {rightElement}
            </div>
          )}
        </div>
        {error ? (
          <span style={{ fontSize: '12px', color: '#EF4444', fontFamily: "'Ubuntu', sans-serif" }}>{error}</span>
        ) : helperText ? (
          <span style={{ fontSize: '12px', color: '#94A3B8', fontFamily: "'Ubuntu', sans-serif" }}>{helperText}</span>
        ) : null}
      </div>
    );
  }
);

ArcaInput.displayName = 'ArcaInput';

interface ArcaTextareaProps extends React.TextareaHTMLAttributes<HTMLTextAreaElement> {
  label?: string;
  helperText?: string;
  error?: string;
}

export function ArcaTextarea({ label, helperText, error, style, id, ...props }: ArcaTextareaProps) {
  const [isFocused, setIsFocused] = useState(false);
  const inputId = id || label?.toLowerCase().replace(/\s+/g, '-');

  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: '6px', width: '100%' }}>
      {label && (
        <label htmlFor={inputId} style={{ fontFamily: "'Ubuntu', sans-serif", fontSize: '13px', fontWeight: 500, letterSpacing: '0.04em', textTransform: 'uppercase', color: '#94A3B8' }}>
          {label}
        </label>
      )}
      <textarea
        id={inputId}
        {...props}
        rows={props.rows || 3}
        onFocus={(e) => { setIsFocused(true); props.onFocus?.(e); }}
        onBlur={(e) => { setIsFocused(false); props.onBlur?.(e); }}
        style={{
          width: '100%',
          padding: '12px 16px',
          backgroundColor: '#1F2329',
          border: `1px solid ${error ? '#EF4444' : isFocused ? '#F90000' : '#363C45'}`,
          borderRadius: '6px',
          color: '#F1F5F9',
          fontFamily: "'Ubuntu', sans-serif",
          fontSize: '14px',
          outline: 'none',
          boxShadow: isFocused ? '0 0 0 3px rgba(249,0,0,0.20)' : 'none',
          transition: 'all 200ms cubic-bezier(0.4,0,0.2,1)',
          resize: 'vertical',
          ...style,
        }}
      />
      {error ? (
        <span style={{ fontSize: '12px', color: '#EF4444' }}>{error}</span>
      ) : helperText ? (
        <span style={{ fontSize: '12px', color: '#94A3B8' }}>{helperText}</span>
      ) : null}
    </div>
  );
}

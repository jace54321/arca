import React from 'react';
import { Loader2 } from 'lucide-react';

type ButtonVariant = 'primary' | 'secondary' | 'ghost' | 'danger';
type ButtonSize = 'sm' | 'md' | 'lg';

interface ArcaButtonProps extends React.ButtonHTMLAttributes<HTMLButtonElement> {
  variant?: ButtonVariant;
  size?: ButtonSize;
  loading?: boolean;
  leftIcon?: React.ReactNode;
  fullWidth?: boolean;
}

export function ArcaButton({
  variant = 'primary',
  size = 'md',
  loading = false,
  leftIcon,
  fullWidth = false,
  children,
  disabled,
  style,
  ...props
}: ArcaButtonProps) {
  const baseStyle: React.CSSProperties = {
    fontFamily: "'JetBrains Mono', monospace",
    fontWeight: 500,
    letterSpacing: '0.1em',
    textTransform: 'uppercase',
    display: 'inline-flex',
    alignItems: 'center',
    justifyContent: 'center',
    gap: '8px',
    cursor: disabled || loading ? 'not-allowed' : 'pointer',
    opacity: disabled || loading ? 0.35 : 1,
    transition: 'all 180ms ease',
    border: 'none',
    outline: 'none',
    borderRadius: '3px',
    whiteSpace: 'nowrap',
    width: fullWidth ? '100%' : undefined,
  };

  const sizeStyles: Record<ButtonSize, React.CSSProperties> = {
    sm: { padding: '7px 14px', fontSize: '10px', height: '30px' },
    md: { padding: '10px 20px', fontSize: '11px', height: '38px' },
    lg: { padding: '13px 26px', fontSize: '12px', height: '48px' },
  };

  const variantStyles: Record<ButtonVariant, React.CSSProperties> = {
    primary: {
      backgroundColor: '#F90000',
      color: '#0A0C0F',
      border: 'none',
    },
    secondary: {
      backgroundColor: 'transparent',
      color: '#94A3B8',
      border: '1px solid rgba(54,60,69,0.6)',
    },
    ghost: {
      backgroundColor: 'transparent',
      color: '#475569',
      border: 'none',
      textTransform: 'none',
      letterSpacing: 'normal',
    },
    danger: {
      backgroundColor: 'transparent',
      color: '#F90000',
      border: '1px solid rgba(249,0,0,0.4)',
    },
  };

  const handleMouseEnter = (e: React.MouseEvent<HTMLButtonElement>) => {
    if (disabled || loading) return;
    const target = e.currentTarget;
    if (variant === 'primary') {
      target.style.backgroundColor = '#D40000';
      target.style.boxShadow = '0 0 16px rgba(249,0,0,0.3)';
    } else if (variant === 'secondary') {
      target.style.borderColor = 'rgba(249,0,0,0.4)';
      target.style.color = '#F1F5F9';
    } else if (variant === 'ghost') {
      target.style.color = '#F90000';
    } else if (variant === 'danger') {
      target.style.backgroundColor = 'rgba(249,0,0,0.08)';
      target.style.borderColor = '#F90000';
    }
  };

  const handleMouseLeave = (e: React.MouseEvent<HTMLButtonElement>) => {
    const target = e.currentTarget;
    Object.assign(target.style, variantStyles[variant]);
    target.style.opacity = disabled || loading ? '0.35' : '1';
    target.style.boxShadow = 'none';
  };

  const handleFocus = (e: React.FocusEvent<HTMLButtonElement>) => {
    if (variant === 'primary') {
      e.currentTarget.style.boxShadow = '0 0 0 2px rgba(249,0,0,0.25)';
    }
  };

  const handleBlur = (e: React.FocusEvent<HTMLButtonElement>) => {
    e.currentTarget.style.boxShadow = 'none';
  };

  return (
    <button
      style={{
        ...baseStyle,
        ...sizeStyles[size],
        ...variantStyles[variant],
        ...style,
      }}
      disabled={disabled || loading}
      onMouseEnter={handleMouseEnter}
      onMouseLeave={handleMouseLeave}
      onFocus={handleFocus}
      onBlur={handleBlur}
      {...props}
    >
      {loading ? <Loader2 size={14} className="animate-spin" /> : leftIcon}
      {loading ? 'Loading…' : children}
    </button>
  );
}
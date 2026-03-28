-- Arca Password Manager Database Schema
-- Target: Supabase PostgreSQL
-- Run this script in Supabase SQL Editor to create all tables

-- Users Table
CREATE TABLE IF NOT EXISTS users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    email VARCHAR(255) NOT NULL UNIQUE,
    master_password_hash VARCHAR(255) NOT NULL,
    encryption_salt VARCHAR(255) NOT NULL,
    username VARCHAR(255),
    avatar_url TEXT,
    vault_version INTEGER DEFAULT 1,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_users_email ON users(email);

-- Credentials Table
CREATE TABLE IF NOT EXISTS credentials (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    site_name VARCHAR(255) NOT NULL,
    url TEXT,
    username VARCHAR(255) NOT NULL,
    encrypted_password TEXT NOT NULL,
    category VARCHAR(50) NOT NULL,
    notes TEXT,
    sync_status VARCHAR(50) DEFAULT 'synced',
    offline_modified BOOLEAN DEFAULT false,
    version_number INTEGER DEFAULT 1,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    last_modified TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_credentials_user_id ON credentials(user_id);
CREATE INDEX idx_credentials_last_modified ON credentials(last_modified DESC);

-- Sync Logs Table
CREATE TABLE IF NOT EXISTS sync_logs (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    device VARCHAR(255) NOT NULL,
    device_type VARCHAR(50) NOT NULL,
    timestamp TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(50) NOT NULL,
    version_from INTEGER,
    version_to INTEGER,
    message TEXT,
    is_current_device BOOLEAN DEFAULT false
);

CREATE INDEX idx_sync_logs_user_id ON sync_logs(user_id);
CREATE INDEX idx_sync_logs_timestamp ON sync_logs(timestamp DESC);

-- Enable Row Level Security (Optional - for future multi-tenant support)
-- ALTER TABLE users ENABLE ROW LEVEL SECURITY;
-- ALTER TABLE credentials ENABLE ROW LEVEL SECURITY;
-- ALTER TABLE sync_logs ENABLE ROW LEVEL SECURITY;

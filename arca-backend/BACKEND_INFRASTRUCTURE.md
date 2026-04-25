# Arca Password Manager - Backend (Simplified)

## Overview
Clean, minimal Spring Boot backend for Arca password manager. Uses Supabase PostgreSQL, JWT auth via Supabase, and minimal business logic.

**Tech Stack:**
- Java 21 + Spring Boot 4.0.5
- Spring Data JPA + PostgreSQL
- Spring Security (Supabase JWT validation)
- CORS enabled for dev

---

## Architecture

```
src/main/java/com/arca/arca_backend/
├── entity/
│   ├── User.java           # User linked to Supabase auth
│   └── Credential.java     # Encrypted passwords
├── repository/
│   ├── UserRepository.java
│   └── CredentialRepository.java
├── service/
│   ├── UserService.java    # User CRUD
│   └── VaultService.java   # Credential CRUD
├── controller/
│   ├── AuthController.java  # POST /register, GET /me
│   └── VaultController.java # GET/POST/PUT/DELETE /credentials
├── config/
│   ├── SecurityConfig.java  # JWT + CORS
│   └── PasswordEncoderConfig.java
└── ArcaBackendApplication.java
```

---

## API Endpoints

| Method | Path | Auth | Purpose |
|--------|------|------|---------|
| POST | `/api/auth/register` | ✗ | Register new user (supabaseUserId, email) |
| GET | `/api/auth/me` | ✓ | Get current user profile |
| GET | `/api/vault/credentials` | ✓ | List user's credentials |
| POST | `/api/vault/credentials` | ✓ | Create credential (send encrypted) |
| PUT | `/api/vault/credentials/{id}` | ✓ | Update credential |
| DELETE | `/api/vault/credentials/{id}` | ✓ | Delete credential |

---

## Database Schema

**users:**
- `id` (UUID, PK)
- `supabase_user_id` (VARCHAR, UNIQUE) - Links to Supabase auth
- `email` (VARCHAR, UNIQUE)
- `created_at`, `updated_at` (TIMESTAMP)

**credentials:**
- `id` (UUID, PK)
- `user_id` (UUID, FK → users)
- `site_name`, `url`, `username` (TEXT)
- `encrypted_password` (TEXT) - Client-side encrypted
- `category`, `notes` (TEXT)
- `sync_status`, `version_number` (INT)
- `created_at`, `last_modified` (TIMESTAMP)

---

## Environment Variables

```
SUPABASE_DB_URL=jdbc:postgresql://...?user=...&password=...
SUPABASE_PROJECT_URL=https://<project>.supabase.co
```

---

## How It Works

1. **Auth:** Users sign up via Supabase (frontend). JWT token sent with requests.
2. **User Record:** Backend creates DB record on first login (POST `/api/auth/register`)
3. **Credentials:** All stored encrypted. Client handles encryption/decryption.
4. **No Master Password:** Removed from backend. Client-side only.
5. **Sync:** Basic status tracking. Client manages actual sync logic.

---

## Running Locally

```bash
./mvnw spring-boot:run
```

Server runs on `http://localhost:8080`
device (VARCHAR NOT NULL)                -- Device name
device_type (VARCHAR NOT NULL)           -- 'mobile'|'desktop'
timestamp (TIMESTAMP)
status (VARCHAR NOT NULL)                -- 'synced'|'conflict'|'error'
version_from (INT)
version_to (INT)
message (TEXT)                           -- User-readable description
is_current_device (BOOLEAN)
```

---

## API Endpoints

### Authentication

#### 1. Register User
```
POST /api/auth/register
Content-Type: application/json

{
  "email": "user@example.com",
  "masterPassword": "MySecureVaultPassword123!"
}

Response: 201 Created
{
  "success": true,
  "message": "User registered successfully",
  "data": {
    "id": "uuid",
    "email": "user@example.com",
    "username": "User",
    "avatarUrl": null
  }
}
```

#### 2. Login (Validate Master Password)
```
POST /api/auth/login
Authorization: Bearer <SUPABASE_JWT>
Content-Type: application/json

{
  "masterPassword": "MySecureVaultPassword123!"
}

Response: 200 OK
{
  "success": true,
  "token": "session-token",
  "user": {
    "id": "uuid",
    "email": "user@example.com",
    "username": "User",
    "avatarUrl": null
  }
}
```

### Vault Operations

#### 3. Unlock Vault
```
POST /api/vault/unlock
Authorization: Bearer <SUPABASE_JWT>
Content-Type: application/json

{
  "masterPassword": "MySecureVaultPassword123!"
}

Response: 200 OK
{
  "success": true,
  "message": "Vault unlocked"
}
```

#### 4. Get All Credentials
```
GET /api/vault/credentials
Authorization: Bearer <SUPABASE_JWT>
Header: masterPassword: MySecureVaultPassword123!

Response: 200 OK
{
  "success": true,
  "message": "Credentials retrieved",
  "data": [
    {
      "id": "uuid",
      "siteName": "GitHub",
      "url": "https://github.com",
      "username": "john@example.com",
      "password": "decrypted_password",
      "category": "Work",
      "notes": "Personal GitHub account",
      "syncStatus": "synced",
      "offlineModified": false,
      "lastModified": "2026-03-28T10:30:00Z"
    }
  ]
}
```

#### 5. Create Credential
```
POST /api/vault/credentials
Authorization: Bearer <SUPABASE_JWT>
Header: masterPassword: MySecureVaultPassword123!
Content-Type: application/json

{
  "siteName": "GitHub",
  "url": "https://github.com",
  "username": "john@example.com",
  "password": "my_github_password",
  "category": "Work",
  "notes": "Personal GitHub account"
}

Response: 201 Created
{
  "success": true,
  "message": "Credential created",
  "data": { /* credential object with encrypted password */ }
}
```

#### 6. Update Credential
```
PUT /api/vault/credentials/{id}
Authorization: Bearer <SUPABASE_JWT>
Header: masterPassword: MySecureVaultPassword123!
Content-Type: application/json

{
  "password": "new_password",
  "notes": "Updated notes"
}

Response: 200 OK
{
  "success": true,
  "message": "Credential updated",
  "data": { /* updated credential */ }
}
```

#### 7. Delete Credential
```
DELETE /api/vault/credentials/{id}
Authorization: Bearer <SUPABASE_JWT>

Response: 200 OK
{
  "success": true,
  "message": "Credential deleted"
}
```

### Sync Operations

#### 8. Get Sync Logs
```
GET /api/sync/logs
Authorization: Bearer <SUPABASE_JWT>

Response: 200 OK
{
  "success": true,
  "message": "Sync logs retrieved",
  "data": [
    {
      "id": "uuid",
      "device": "Pixel 7 Pro",
      "deviceType": "mobile",
      "timestamp": "2026-03-28T10:30:00Z",
      "status": "synced",
      "versionFrom": 1,
      "versionTo": 2,
      "message": "Vault synchronized successfully",
      "isCurrentDevice": true
    }
  ]
}
```

#### 9. Trigger Sync
```
POST /api/sync/trigger?deviceName=WebClient&deviceType=desktop
Authorization: Bearer <SUPABASE_JWT>

Response: 200 OK
{
  "success": true,
  "message": "Sync triggered successfully",
  "data": {
    "id": "uuid",
    "device": "WebClient",
    "deviceType": "desktop",
    "timestamp": "2026-03-28T10:35:00Z",
    "status": "synced",
    "versionFrom": 2,
    "versionTo": 3,
    "message": "Vault synchronized successfully",
    "isCurrentDevice": true
  }
}
```

### User Profile

#### 10. Get User Profile
```
GET /api/user/profile
Authorization: Bearer <SUPABASE_JWT>

Response: 200 OK
{
  "success": true,
  "message": "Profile retrieved",
  "data": {
    "id": "uuid",
    "email": "user@example.com",
    "username": "John Doe",
    "avatarUrl": "https://..."
  }
}
```

#### 11. Update User Profile
```
PUT /api/user/profile
Authorization: Bearer <SUPABASE_JWT>
Content-Type: application/json

{
  "username": "John Doe Updated",
  "avatarUrl": "https://..."
}

Response: 200 OK
{
  "success": true,
  "message": "Profile updated",
  "data": { /* updated user */ }
}
```

---

## Security Features

### 1. JWT Validation
- All protected endpoints require valid Supabase JWT token
- Token verified against Supabase JWKS endpoint (public key verification)
- User ID extracted from JWT claims, NOT from request body
- Stateless authentication (no server-side sessions)

### 2. Master Password Protection
- Master password hashed with Bcrypt (strength 12)
- Never stored in plaintext
- Required for every vault operation (getting credentials, creating, etc.)

### 3. Credential Encryption
- All passwords encrypted with AES-256
- Encryption key derived from master password + unique salt
- Key derivation: SHA-256(masterPassword + encryptionSalt)
- Passwords decrypted only when needed (for response DTOs)

### 4. CORS
- Restricted to `localhost:5173`, `localhost:3000`, `127.0.0.1:5173`
- Add production domain when deploying
- Supports credentials and authorization headers

### 5. Data Isolation
- All queries filtered by `userId` (extracted from JWT)
- Users can only access their own credentials and sync logs
- Foreign key constraints in database

---

## Integration Steps

### 1. Add Maven Dependency
Already added to `pom.xml`:
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-oauth2-resource-server</artifactId>
</dependency>
```

### 2. Create Database Tables
Run this SQL on Supabase:
```sql
CREATE TABLE users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    email VARCHAR NOT NULL UNIQUE,
    master_password_hash VARCHAR NOT NULL,
    encryption_salt VARCHAR NOT NULL,
    username VARCHAR,
    avatar_url TEXT,
    vault_version INT DEFAULT 1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE credentials (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    site_name VARCHAR NOT NULL,
    url VARCHAR,
    username VARCHAR NOT NULL,
    encrypted_password TEXT NOT NULL,
    category VARCHAR NOT NULL,
    notes TEXT,
    sync_status VARCHAR DEFAULT 'synced',
    offline_modified BOOLEAN DEFAULT false,
    version_number INT DEFAULT 1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_modified TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE sync_logs (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    device VARCHAR NOT NULL,
    device_type VARCHAR NOT NULL,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR NOT NULL,
    version_from INT,
    version_to INT,
    message TEXT,
    is_current_device BOOLEAN DEFAULT false
);

CREATE INDEX idx_credentials_user_id ON credentials(user_id);
CREATE INDEX idx_sync_logs_user_id ON sync_logs(user_id);
```

### 3. Environment Variables
Update `/.env`:
```
SUPABASE_DB_URL=jdbc:postgresql://aws-1-ap-southeast-1.pooler.supabase.com:6543/postgres?user=postgres.xupeembqwzmrpkoegnhr&password=YOUR_PASSWORD
SUPABASE_PROJECT_URL=https://xupeembqwzmrpkoegnhr.supabase.co
```

### 4. Frontend Integration
Update frontend API client to:
```typescript
const API_BASE = "http://localhost:8080/api";
const token = await supabase.auth.getSession(); // Get JWT

// Example: Get credentials
const response = await fetch(`${API_BASE}/vault/credentials`, {
  headers: {
    "Authorization": `Bearer ${token.data.session.access_token}`,
    "masterPassword": masterPassword
  }
});
```

### 5. Test the Backend
```bash
cd arca-backend
mvn clean install
mvn spring-boot:run

# Test endpoints with Postman/curl
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com","masterPassword":"Password123!"}'
```

---

## Key Implementation Details

### Encryption Key Derivation
```java
// Master Password + Salt = Encryption Key
String keyString = deriveKeyFromPassword("MyPassword123!", "unique-salt-per-user");
// Key is derived once per login/vault unlock
```

### Password Encryption in VaultService
```java
// Store: Encrypt plaintext before saving
String encryptedPassword = encryptionUtil.encrypt(plaintext, keyString);
credential.setEncryptedPassword(encryptedPassword);

// Retrieve: Decrypt when sending to frontend
String decrypted = encryptionUtil.decrypt(encrypted, keyString);
```

### User ID from JWT
```java
// In any controller
String userId = extractUserIdFromContext();
// This is guaranteed to be the authenticated user's ID
// NOT from request body (defense against tampering)
```

### Sync Conflict Resolution
- Manual merging: Keep both versions in separate credentials
- Auto-resolution: Last-write-wins (use `versionNumber` timestamp)
- Conflict tracking: `SyncLog` records which version won

---

## Next Steps

1. ✅ Create database tables (SQL provided above)
2. ✅ Update environment variables with Supabase credentials
3. ✅ Build and test: `mvn clean package`
4. ✅ Run: `mvn spring-boot:run`
5. ✅ Update frontend to call these endpoints
6. ✅ Add more validation as needed
7. ✅ Implement audit logging for compliance
8. ✅ Add rate limiting for brute force protection

---

## Files Created

Total: **26 files** (3 entities, 8 DTOs, 3 repositories, 3 services, 4 controllers, 1 utility, 2 configs, + pom.xml update)

All code follows Spring Boot best practices:
- Clean architecture (entity → repository → service → controller)
- Separation of concerns
- Proper error handling
- Lombok annotations for less boilerplate
- JPA for database access
- Spring Security for auth
- Stateless JWT validation

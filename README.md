# ARCA

Arca is a zero-knowledge password vault built with Spring Boot and React, enabling users to securely store, manage, and sync credentials across devices. All encryption happens client-side — credentials are never stored or transmitted in plaintext.

## Tech Stack

| Category | Technology |
|---|---|
| Backend | Spring Boot 4.0.5 (Java 21) |
| Frontend | React 18 + TypeScript (Vite) |
| Mobile | Android (Kotlin) |
| Database | PostgreSQL (via Supabase) |
| Authentication | Supabase Auth (JWT / ES256) |
| ORM | Spring Data JPA (Hibernate) |
| Security | Spring Security OAuth2 Resource Server |
| Package Manager | pnpm |
| Build Tool (Backend) | Maven (mvnw) |

## Setup & Run Instructions

Get up and running in a few steps:

```bash
# 1. Clone the repo
git clone https://github.com/jace54321/arca.git
cd arca
```

### Backend

```bash
# 2. Set environment variables (or create a .env at the project root)
# Required:
#   SUPABASE_DB_URL=jdbc:postgresql://<host>:<port>/postgres?user=<user>&password=<password>
#   SUPABASE_PROJECT_URL=https://<project-ref>.supabase.co

# 3. Run the Spring Boot backend
cd arca-backend
./mvnw spring-boot:run
```

Backend runs on: `http://localhost:8080`

### Frontend

```bash
# 4. Install dependencies
cd frontend
pnpm install

# 5. Create a .env file in /frontend
# VITE_SUPABASE_URL=https://<project-ref>.supabase.co
# VITE_SUPABASE_ANON_KEY=<your-legacy-anon-jwt>
# VITE_API_URL=http://localhost:8080/api

# 6. Start the dev server
pnpm run dev
```

Frontend runs on: `http://localhost:5173`

## Project Structure

```
arca/
├── arca-backend/           # Spring Boot API
│   └── src/main/java/
│       └── com/arca/arca_backend/
│           ├── config/     # Security, CORS, JWT config
│           ├── controller/ # REST endpoints (auth, vault, sync, user)
│           ├── entity/     # JPA entities (User, Credential)
│           ├── repository/ # Spring Data repositories
│           └── service/    # Business logic
├── frontend/               # React + TypeScript (Vite)
│   └── src/
│       └── app/
│           ├── components/ # Reusable UI components
│           ├── context/    # ArcaContext (global state)
│           ├── pages/      # Login, Unlock, Vault, Settings, etc.
│           └── services/   # apiClient.ts (backend communication)
└── arca-android/           # Android mobile client (Kotlin)
```

## Developer

| Name | Role | GitHub |
|---|---|---|
| Joseph Cris Arpon | Solo Developer | [@jace54321](https://github.com/jace54321) |

## Troubleshooting

- **401 Unauthorized** — Make sure `VITE_SUPABASE_ANON_KEY` is the **legacy anon JWT** (starts with `eyJ...`), not the publishable API key.
- **Backend won't start** — Verify `SUPABASE_DB_URL` is set correctly as a `jdbc:postgresql://...` connection string.
- **CORS errors** — The backend allows `localhost:5173` and `localhost:5174` by default. Update `SecurityConfig.java` if your frontend runs on a different port.
- **Vault unlock fails** — Ensure the backend is running and the user is registered in the backend DB (call `POST /api/auth/register` on first login if needed).

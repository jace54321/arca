# Arca — Figma Make Prompt
### Complete UI Generation Brief for the Arca Hybrid Password Manager

---

## 1. Project Identity

**Product Name:** Arca
**Tagline:** *Your vault. Your key. No one else's.*
**Etymology:** From Latin — *arca* means "chest" or "strongbox." This is a deliberate name: the app is a sealed container that only the owner can open.
**Product Type:** Hybrid, zero-knowledge password manager. Offline-first Android app + React web dashboard + Spring Boot backend.
**Core Promise:** Passwords are encrypted on the user's device before they ever leave it. The server only ever sees a locked box — never the contents.

---

## 2. Design Philosophy

Arca must feel like it was designed by someone who cares deeply about both **security and craft**. It should communicate trust, precision, and calm confidence — not paranoia, not aggressive "hacker" aesthetics. Think a well-made physical safe: solid, quiet, reliable. Not a neon cyberpunk tool.

**Emotional target:** The user should feel *in control* and *protected* the moment they see the app. Nothing should feel cluttered, confusing, or anxious. Every screen should reinforce the feeling that "my passwords are safe here."

**Aesthetic direction:** Dark, minimal, precise. High contrast. Intentional use of the brand red as a single accent — never overused. Generous whitespace. Typography that communicates seriousness without being cold. Subtle depth through layering, not drop shadows.

**The one thing users will remember:** The near-black background with a single glowing red accent on every key interactive element — like a warning light in the dark. A locked chest, sealed in black, with one red signal that says: this is yours.

---

## 3. Design System

Apply these tokens consistently across every frame. Do not introduce colors, fonts, or spacing values outside of this system.

### 3.1 Color Palette

| Token | Hex | Usage |
|---|---|---|
| `--color-bg` | `#14181E` | Base background for all screens — near-black |
| `--color-surface` | `#1F2329` | Card surfaces, modals, input fields, sidebars — lifted slightly from base |
| `--color-surface-raised` | `#272C33` | Hover states, elevated cards, dropdown menus |
| `--color-border` | `#363C45` | Subtle borders on cards, dividers, input outlines |
| `--color-border-active` | `#F90000` | Focused input borders, selected states |
| `--color-primary` | `#F90000` | Primary CTA buttons, active icons, brand accents, sync indicators, the "Arca" wordmark highlight |
| `--color-primary-dim` | `#B30000` | Pressed/active state for primary buttons |
| `--color-primary-glow` | `rgba(249,0,0,0.12)` | Subtle background tint on active elements, hover glow |
| `--color-text-primary` | `#F1F5F9` | Headings, primary body text |
| `--color-text-secondary` | `#94A3B8` | Labels, captions, helper text, placeholder text |
| `--color-text-muted` | `#475569` | Disabled states, timestamps, very low emphasis text |
| `--color-success` | `#10B981` | Sync success badge, password strength (strong), "Synced" status |
| `--color-warning` | `#FF4500` | Sync pending badge, password strength (fair), offline banner |
| `--color-error` | `#EF4444` | Error states, password strength (weak), failed sync |
| `--color-info` | `#3B82F6` | Syncing-in-progress indicator (animated) |

### 3.2 Typography

**Primary Font:** Ubuntu (Google Fonts — weight 300, 400, 500, 700)
- Used for all UI text: headings, body, labels, buttons
- Chosen for its humanist warmth that softens the dark theme

**Monospace Font:** JetBrains Mono (Google Fonts — weight 400, 500)
- Used exclusively for: displayed passwords, usernames, vault entry field values, API/technical metadata in sync logs
- Never use for UI labels or headings

| Style | Font | Weight | Size | Line Height | Letter Spacing | Usage |
|---|---|---|---|---|---|---|
| `display-xl` | Ubuntu | 700 | 32px | 1.2 | -0.02em | Screen titles (rare) |
| `display-lg` | Ubuntu | 700 | 24px | 1.3 | -0.01em | Page headings |
| `display-md` | Ubuntu | 500 | 20px | 1.4 | 0 | Section headings, modal titles |
| `body-lg` | Ubuntu | 400 | 16px | 1.6 | 0 | Primary body copy |
| `body-md` | Ubuntu | 400 | 14px | 1.5 | 0 | Default UI text, card content |
| `body-sm` | Ubuntu | 400 | 12px | 1.4 | 0.01em | Captions, timestamps, helper text |
| `label` | Ubuntu | 500 | 13px | 1.3 | 0.04em | Input labels, table headers (uppercase) |
| `mono-md` | JetBrains Mono | 400 | 14px | 1.5 | 0 | Displayed passwords, usernames |
| `mono-sm` | JetBrains Mono | 400 | 12px | 1.4 | 0 | Sync log metadata, version numbers |

### 3.3 Spacing

8px base grid. All spacing values are multiples of 8.

| Token | Value | Usage |
|---|---|---|
| `--space-1` | 4px | Micro gaps (icon to label, badge padding) |
| `--space-2` | 8px | Tight internal padding (chips, badges) |
| `--space-3` | 12px | Input internal padding (vertical) |
| `--space-4` | 16px | Input internal padding (horizontal), card internal padding |
| `--space-5` | 20px | Card gap, section gap |
| `--space-6` | 24px | Section padding, modal padding |
| `--space-8` | 32px | Page section separation |
| `--space-10` | 40px | Major section breaks |
| `--space-12` | 48px | Hero area padding |

### 3.4 Border Radius

| Token | Value | Usage |
|---|---|---|
| `--radius-sm` | 4px | Badges, chips, small inline elements |
| `--radius-md` | 6px | Input fields, buttons |
| `--radius-lg` | 10px | Cards, modals, dropdowns |
| `--radius-xl` | 16px | Bottom sheets (mobile), floating panels |
| `--radius-full` | 9999px | Avatar circles, toggle switches, pill labels |

### 3.5 Elevation / Depth

Use background layering instead of drop shadows. Each layer is a slightly lighter shade of near-black charcoal.

| Level | Background | Usage |
|---|---|---|
| 0 — Base | `#14181E` | Page background |
| 1 — Surface | `#1F2329` | Cards, sidebars |
| 2 — Raised | `#272C33` | Modals, dropdowns, hover states |
| 3 — Overlay | `#2F353D` | Tooltips, context menus |

**Accent glow effect:** On focused inputs, selected cards, and primary CTAs — apply a 0px 0px 0px 3px `rgba(249,0,0,0.20)` ring. This is the "red pulse" that signals interaction.

### 3.6 Iconography

Use **Phosphor Icons** (regular weight, 20px default). Stroke-based, not filled. Use filled variants only for active/selected states to signal selection.

Key icons used across the app:
- Vault / Safe: `vault` or `lock-simple`
- Password hidden: `eye-slash`
- Password visible: `eye`
- Copy to clipboard: `copy`
- Add entry: `plus`
- Edit: `pencil-simple`
- Delete: `trash`
- Sync: `arrows-clockwise`
- Cloud synced: `cloud-check`
- Cloud pending: `cloud-arrow-up`
- Cloud offline: `cloud-slash`
- Settings: `gear`
- Logout: `sign-out`
- Search: `magnifying-glass`
- Category/folder: `folder-simple`
- Strength indicator: `shield-check` / `shield-warning` / `shield-slash`
- Recovery: `key`
- Device: `device-mobile` / `desktop`

---

## 4. Component Library

Define these as reusable components before building any screen.

### 4.1 Button

**Primary Button**
- Background: `--color-primary` → `--color-primary-dim` on hover
- Text: `#14181E` (dark on orange, never white)
- Font: Ubuntu 500, 14px, letter-spacing 0.02em, uppercase
- Padding: 12px 24px
- Radius: `--radius-md`
- Focus ring: red glow 3px
- Disabled: opacity 0.35, cursor not-allowed
- Loading state: spinner replaces label, maintains size

**Secondary Button**
- Background: transparent
- Border: 1px solid `--color-border`
- Text: `--color-text-primary`
- Hover: background `--color-surface-raised`, border `--color-border-active`

**Ghost Button / Icon Button**
- No background, no border
- Icon color: `--color-text-secondary` → `--color-primary` on hover
- Used for eye toggle, copy, edit, delete in-row actions

**Danger Button**
- Background: transparent, border `--color-error`
- Text: `--color-error`
- Hover: background `rgba(239,68,68,0.10)`

### 4.2 Input Field

- Background: `--color-surface`
- Border: 1px solid `--color-border`
- Border (focused): 1px solid `--color-primary` + red glow ring
- Border (error): 1px solid `--color-error`
- Text: `--color-text-primary`, Ubuntu Regular 14px
- Placeholder: `--color-text-muted`
- Label: above field, Ubuntu 500 13px uppercase, `--color-text-secondary`, letter-spacing 0.04em
- Helper text: below field, 12px, `--color-text-secondary`
- Error text: below field, 12px, `--color-error`
- Height: 44px (standard), 52px (large/prominent, e.g., Master Password field)
- Padding: 12px 16px
- Radius: `--radius-md`

**Password field variant:**
- Includes eye-toggle icon button on the right (20px Phosphor icon)
- When hidden: dots or asterisks in JetBrains Mono
- When revealed: plaintext in JetBrains Mono, background flashes subtly red for 200ms on reveal to signal the sensitive action

### 4.3 Credential Card

The core repeating element. Used in both the Android vault list and the web dashboard grid.

**Structure (top to bottom):**
1. Top row: Site favicon (24px circle, fallback to first letter in `--color-primary` background) + Site name (body-md, text-primary) + overflow menu icon (right-aligned, ghost)
2. Middle row: Username/email in monospace, text-secondary — partially masked (show first 3 chars + ••••••)
3. Bottom row: Password field — always hidden by default, shown as •••••••••• — with copy icon and eye icon on the right
4. Bottom strip (very subtle): sync badge on far right — "Synced ✓" in green, or "Pending ↑" in red

**States:**
- Default: surface background, border `--color-border`
- Hover: surface-raised background, border `--color-border-active`, red glow ring
- Selected: border `--color-primary`, left accent bar 3px red
- Offline-modified: subtle red left border, tooltip "Not yet synced"

**Dimensions (web):** Min-height 96px, padding 16px, radius `--radius-lg`
**Dimensions (mobile):** Full-width, min-height 80px, padding 16px, radius `--radius-lg`

### 4.4 Password Strength Meter

A horizontal bar below the Master Password and new-password fields.

- 4-segment bar (weak / fair / strong / very strong)
- Inactive segments: `--color-border`
- Active fill: red → red → green → bright green (as segments fill)
- Label to the right: "Weak" / "Fair" / "Strong" / "Very Strong" in matching color
- Animates smoothly as user types

### 4.5 Sync Status Badge

Used in the app bar and on individual credential cards.

| State | Icon | Color | Label |
|---|---|---|---|
| Synced | cloud-check (filled) | `--color-success` | "Synced" |
| Syncing | arrows-clockwise (spinning) | `--color-info` | "Syncing…" |
| Pending | cloud-arrow-up | `--color-warning` | "Pending" |
| Offline | cloud-slash | `--color-text-muted` | "Offline" |
| Error | cloud-x | `--color-error` | "Sync Failed" |

Shape: pill badge, 12px font, 4px 10px padding, `--radius-full`

### 4.6 Offline Banner

Persistent, non-dismissible red strip at the top of the screen when the device is offline.

- Background: `rgba(245,158,11,0.12)`
- Left border: 3px solid `--color-warning`
- Icon: `wifi-slash` (Phosphor, 16px, red)
- Text: "You are offline — [N] unsynced change(s). Will sync on reconnect." — Ubuntu 13px, `--color-warning`
- Padding: 10px 16px

### 4.7 Toast / Notification

Appears at bottom-center (web) or bottom of screen (mobile). Auto-dismisses after 4 seconds.

- Background: `--color-surface-raised`
- Border-left: 4px solid (green for success, red for error, red for warning)
- Icon + message text side by side
- Subtle fade-in-up + fade-out animation

---

## 5. Screen Specifications

### Screen 1: Authentication — Login / Register (Web)

**Purpose:** The first screen any user sees. Must immediately communicate security, trust, and polish. Sets the tone for the entire product.

**Layout:** Centered card on the full `--color-bg` background. The background is not flat — it has a very subtle radial gradient from `#14181E` at the edges to `#14181E` at the center, and an extremely faint grid pattern (1px lines, `rgba(255,255,255,0.02)`) to give depth without distraction.

**Left side of the card (if split layout on desktop):**
- Large "Arca" wordmark in Ubuntu 700, 40px, `--color-text-primary`, with the "A" or the dot on the "i" highlighted in `--color-primary`
- Tagline: *"Your vault. Your key. No one else's."* — Ubuntu 300, 18px, `--color-text-secondary`
- Below the tagline: 3 small trust pillars as icon + text rows:
  - 🔐 "Zero-knowledge architecture"
  - 📱 "Works offline, syncs silently"
  - 🔑 "Only you hold the key"
- Each pillar: Phosphor icon in `--color-primary`, 16px, followed by Ubuntu 14px text in `--color-text-secondary`

**Right side (or full card on mobile):**
- Tab switcher: "Log In" | "Create Account" — active tab has `--color-primary` underline, inactive is `--color-text-muted`
- **Log In tab fields:**
  - Email address input
  - Master Password input (large variant, 52px height, eye toggle, JetBrains Mono when revealed)
  - "Log In" primary button (full width)
  - Below button: subtle text "Forgot your Master Password? Unfortunately, we can't help — it's never sent to us." in 12px `--color-text-muted`. This is intentional messaging, not a bug.
- **Create Account tab fields:**
  - Email address input
  - Master Password input + password strength meter below it
  - Confirm Master Password input
  - Checkbox: "I understand my Master Password cannot be recovered. I will keep it safe."
  - "Create Account" primary button (full width, disabled until checkbox is checked)

**Micro-interactions:**
- When the Master Password field is focused, the card's left border glows red (0px 0px 0px 2px `--color-primary-glow`)
- On successful login, the card fades out and the vault slides in from the right
- On failed login, the card shakes horizontally (3px, 200ms ease-in-out, 3 times)

---

### Screen 2: Decryption Prompt Overlay (Web)

**Purpose:** Shown immediately after login before the vault is accessible. The user has authenticated (who they are) but must now unlock the vault (what they know). This is the "key in the lock" moment.

**Layout:** Full-screen overlay (not a modal — it covers everything). Background: `--color-bg` with a blurred, dark version of the vault dashboard barely visible behind a `backdrop-filter: blur(12px) brightness(0.4)` layer.

**Center panel:**
- Arca logo mark at top (small, 32px)
- Heading: "Unlock Your Vault" — display-lg, text-primary
- Subheading: "Enter your Master Password to decrypt your credentials on this device." — body-md, text-secondary, max-width 360px, centered
- **Visual separator:** a subtle key icon (Phosphor `key`, 48px, `--color-primary`, dim glow behind it)
- Master Password input field (large variant)
- "Unlock Vault" primary button (full width)
- Below: "This is the only way to unlock your vault. It is never sent anywhere." — 12px, muted

**Important UX note to preserve:**
- There is NO "Forgot Password" link. The absence of this is intentional and must be visually acknowledged. The helper text below the button IS the explanation.
- On wrong password: the key icon briefly rotates back (as if failing to turn), the input border goes red, helper text changes to "Incorrect password. Try again." — 12px, `--color-error`
- On correct password: the key icon rotates forward smoothly, a brief green flash, then the overlay dissolves upward revealing the vault

---

### Screen 3: Vault Dashboard (Web)

**Purpose:** The main screen. Users spend most of their time here. Browsing, searching, copying passwords.

**Layout:** Two-column: narrow sidebar (240px, fixed) + main content area (fluid).

**Sidebar:**
- Top: Arca wordmark + sync status badge (inline, right-aligned to the wordmark)
- Navigation items (icon + label, active has red left bar and `--color-primary-glow` background):
  - All Entries (active by default)
  - Categories (Work / Personal / Social — indented, 12px)
  - Sync Logs
  - Settings
- Bottom of sidebar: logged-in email in 12px muted text + Logout ghost button

**Main content — top bar:**
- Search bar (full width minus padding): placeholder "Search by name, username, or URL…" — Phosphor magnifying-glass icon inside on the left
- Right of search: "Add Entry" primary button with `plus` icon

**Main content — credential grid:**
- 2-column grid on desktop (1140px+), 1-column on tablet, 1-column on mobile
- Each cell is a Credential Card component (see Section 4.3)
- Cards animate in on load with a subtle stagger (each card fades in 40ms after the previous)
- Empty state: centered illustration (simple, SVG-based — a closed chest/box outline in `--color-border`, 80px) + "Your vault is empty. Add your first password." + "Add Entry" button

**Search behavior:**
- Real-time filtering as user types — no API call, purely in-memory
- Unmatched cards fade to 30% opacity rather than disappearing entirely, matched card(s) maintain full opacity and float to the top
- No results state: "No entries match '[query]'" in muted text

---

### Screen 4: Add / Edit Entry (Web)

**Purpose:** Creating or modifying a credential. Must be fast, clear, and not feel like a form.

**Layout:** Right-side slide-in panel (not a full modal). 420px wide, slides in from the right edge, main content dims to 60% and blurs slightly behind it.

**Panel content:**
- Close button (X) top right
- Heading: "New Entry" or "Edit Entry" — display-md
- Fields (in order):
  1. **Site Name** — text input, placeholder "e.g. GitHub, Google, Netflix"
  2. **URL** — text input, placeholder "https://…" (optional)
  3. **Username / Email** — text input
  4. **Password** — password input with eye toggle + a "Generate" icon button (dice icon, Phosphor `dice-five`) that generates a strong random password and fills the field with a brief red flash
  5. **Password Strength Meter** — appears below the password field, always visible
  6. **Category** — segmented control: Work | Personal | Social | Other (optional)
  7. **Notes** — textarea, 3 rows, placeholder "Optional notes…"

**Bottom action bar (sticky at panel bottom):**
- Left: "Delete Entry" danger ghost button (only shown in Edit mode)
- Right: "Cancel" secondary button + "Save Entry" primary button

**Micro-interactions:**
- Saving: button shows spinner + "Saving…" text, then brief "Saved ✓" with green check, then panel closes
- If offline: Save button still works. Button label becomes "Save Locally" and after saving, a small red toast appears: "Saved locally. Will sync when back online."

---

### Screen 5: Sync Logs / Security Audit (Web)

**Purpose:** Lets users see the history of every sync event — which device, when, success or failure. Builds trust by being transparent about what the system is doing.

**Layout:** Full main content area. No slide panel.

**Page heading:** "Sync History" — display-lg
**Subheading:** "A complete record of every time your vault was synchronized." — body-md, text-secondary

**Filter bar:** Dropdown to filter by status (All / Success / Conflict / Error) + date range picker

**Table:**
Each row is one sync event:
| Column | Content |
|---|---|
| Device | Device icon (mobile/desktop Phosphor) + device name in body-md + device type in 12px muted |
| Timestamp | "Feb 7, 2026 at 09:30 AM" in body-sm, muted. Hover reveals ISO timestamp in tooltip |
| Status | Sync Status Badge component |
| Version | "v12 → v13" in JetBrains Mono, 12px, muted |
| Message | Short description: "Vault updated successfully" or error message in `--color-error` |

Row hover: surface-raised background

**Empty state:** "No sync events yet. Your vault has never been synchronized." — centered, muted

---

### Screen 6: Authentication — Login / Register (Android Mobile)

**Purpose:** Same as Screen 1 but adapted for a 390×844 mobile viewport (iPhone 14 / Pixel 7 reference).

**Layout:** Full-screen, no split. Scrollable if needed.

**Top section:**
- Status bar area: dark, icons in light color
- Arca logo mark (48px SVG) + "Arca" wordmark — centered, top quarter of the screen
- Tagline: *"Your vault. Your key."* — shorter version, centered, body-md, text-secondary

**Middle section:**
- Tab switcher: "Log In" | "Create Account" — pill-style switcher, full-width, `--color-surface` background, active tab gets `--color-primary` fill with dark text
- Form fields below (same as web but full-width, 52px height inputs for touch targets)
- Password strength meter below password field (Create Account only)

**Bottom section:**
- Primary action button — full width, 56px height (larger touch target), 16px bottom margin from edge
- Same intentional "no forgot password" helper text below the button

**Keyboard behavior:**
- When keyboard appears, the Arca logo shrinks or hides (only form is visible), inputs scroll into view
- Done/Next keyboard action moves focus between fields

---

### Screen 7: Vault Dashboard (Android Mobile)

**Purpose:** The main screen on mobile. Must be thumb-friendly, fast to scan, and work flawlessly offline.

**Layout:** Standard Android screen. Top app bar + scrollable list + floating action button.

**Top app bar:**
- Left: "Arca" wordmark (Ubuntu 700, 20px)
- Right: Sync status badge (compact, icon only on small screens) + overflow menu (three-dot, Phosphor `dots-three-vertical`)

**Offline banner** (when offline): red strip below the app bar (see Component 4.6)

**Search bar:**
- Persistent, below the app bar (not hidden behind a search icon)
- Full width, 44px height, `--color-surface` background, rounded, with Phosphor magnifying-glass icon

**Credential list:**
- Vertical scrolling list of Credential Card components (full-width, mobile variant)
- Cards have a subtle left accent strip in `--color-primary` for entries modified while offline (pending sync)
- Swipe-left gesture on a card reveals: red "Delete" action button
- Swipe-right gesture: red "Copy Password" shortcut action

**Floating Action Button (FAB):**
- Position: bottom-right, 20px from edge
- Size: 56×56px
- Background: `--color-primary`
- Icon: `plus` (Phosphor, 28px, dark color)
- On press: brief scale animation (0.9 → 1.0, 120ms ease-out), then Add/Edit Entry bottom sheet opens

---

### Screen 8: Decryption Prompt (Android Mobile)

**Purpose:** Master Password unlock screen. Shown after app launch or after session timeout.

**Layout:** Full screen. No navigation, no escape. This IS the app until unlocked.

**Visual design:**
- Full `--color-bg` background
- Center: large lock icon (Phosphor `lock-simple`, 72px, `--color-primary`, with a very subtle red radial glow behind it — `radial-gradient(circle, rgba(249,0,0,0.15) 0%, transparent 70%)`)
- Below icon: "Arca" wordmark in display-lg
- Below wordmark: "Enter your Master Password to unlock." — body-md, text-secondary
- Large Master Password input (52px, full width, eye toggle, JetBrains Mono on reveal)
- "Unlock" primary button below — full width, 56px

**States:**
- Wrong password: lock icon shakes, border goes red, helper text "Incorrect password." appears below the field
- Correct password: lock icon animates open (rotation + unlock), brief success flash, dashboard fades in

---

### Screen 9: Add / Edit Entry (Android Mobile)

**Purpose:** Creating or editing a credential on mobile.

**Layout:** Bottom sheet (slides up from the bottom). Cover 80% of the screen height. `--radius-xl` on top corners. Drag handle at the top (32×4px pill, `--color-border`).

**Content (scrollable within the sheet):**
- Sheet handle + "New Entry" / "Edit Entry" heading + X button (right)
- Same fields as web version (Screen 4), but adapted for mobile:
  - Full-width inputs, 52px height for comfortable touch
  - "Generate Password" is a dedicated button below the password field (not an icon in the field) to prevent accidental taps
  - Category is a horizontal scrolling row of chips instead of a segmented control
- Sticky bottom: "Save Entry" primary button (full width, 56px) above the system gesture bar

---

### Screen 10: Sync Logs (Android Mobile)

**Purpose:** Viewing sync history on mobile.

**Layout:** Standard screen, top app bar ("Sync History" title, back arrow) + scrollable card list.

**Each log entry as a card:**
- Top row: Device name + sync status badge
- Middle: Timestamp + version change ("v12 → v13" in monospace)
- Bottom: Status message text (success in muted, error in red)

Cards are ordered newest-first. A `--color-primary` dot marks any entry from the current device.

---

## 6. Motion & Animation Principles

- **Timing:** 200ms for micro-interactions (hover, focus, button press), 300ms for transitions (panel slide, modal), 500ms for page transitions
- **Easing:** `cubic-bezier(0.4, 0, 0.2, 1)` (Material standard ease) for most transitions. `cubic-bezier(0.34, 1.56, 0.64, 1)` (spring) for FAB press and success confirmations
- **Stagger:** Credential cards on dashboard load stagger at 40ms per card, max 5 cards (cards beyond 5 don't stagger)
- **The lock animation:** On vault unlock (both platforms), the lock icon rotates its shackle open — this is the single most important animation in the product. It should feel satisfying and deliberate, not instant.
- **Sync spinner:** The `arrows-clockwise` icon rotates at 1 full revolution per 1.2 seconds, smooth linear, infinite while syncing
- **No bounce:** Avoid playful bounce animations — the product is a security tool. Subtle spring is fine, big bounces are not.

---

## 7. Screens to Generate (Summary)

| # | Screen | Platform | Priority |
|---|---|---|---|
| 1 | Login / Register | Web | P0 |
| 2 | Decryption Prompt Overlay | Web | P0 |
| 3 | Vault Dashboard | Web | P0 |
| 4 | Add / Edit Entry (slide panel) | Web | P0 |
| 5 | Sync Logs / Audit | Web | P1 |
| 6 | Login / Register | Android | P0 |
| 7 | Vault Dashboard | Android | P0 |
| 8 | Decryption Prompt (unlock screen) | Android | P0 |
| 9 | Add / Edit Entry (bottom sheet) | Android | P0 |
| 10 | Sync Logs | Android | P1 |

Generate P0 screens first. All 10 screens should be delivered in a single Figma file with:
- A **Web** page and a **Mobile** page as separate Figma pages
- All components defined in a **Components** page
- All design tokens defined as Figma Variables
- Auto Layout applied to all frames
- Responsive constraints set on all elements

---

## 8. What NOT to Design

- No biometric unlock UI (fingerprint / FaceID) — excluded from MVP
- No browser extension UI
- No "Login with Google/Facebook" buttons
- No server-side decryption prompts — the server never decrypts anything
- No bright/white backgrounds — the entire product lives in the dark theme
- No purple gradients, no glassmorphism, no neon — this is not a Web3 product
- No Inter or Roboto — Ubuntu only

---

## 9. Reference Phrase

If you need a single sentence to guide every design decision:

> *"A sealed black chest — cold, precise, uncompromising — with a single red signal that only ignites for the person who holds the key."*
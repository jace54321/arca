/**
 * Zero-knowledge cryptography for Arca.
 *
 * Key derivation:
 *   masterPassword + email
 *     → PBKDF2(600 000 iterations, SHA-256, salt = UTF-8(email))
 *     → 512 raw bits (64 bytes)
 *   bytes[0..31]  → AES-256-GCM vault key  (non-extractable, never leaves device)
 *   bytes[32..63] → auth key hex           (bcrypt-hashed copy stored on server)
 *
 * Encryption:
 *   AES-256-GCM with a fresh cryptographically-random 12-byte IV per field.
 */

export const CRYPTO_VERSION = 1;

const PBKDF2_ITERATIONS = 600_000;

// ── Key derivation ─────────────────────────────────────────────────────────────

/**
 * Derive both the vault key and the auth key from the user's master password.
 *
 * @param masterPassword  The password the user types in.
 * @param email           Used as PBKDF2 salt — must match the value used at registration.
 * @returns
 *   vaultKey   — AES-256-GCM CryptoKey (non-extractable). Use for encrypt/decrypt.
 *   authKeyHex — 32-byte hex string.  BCrypt this server-side to verify identity.
 */
export async function deriveKeys(
  masterPassword: string,
  email: string,
): Promise<{ vaultKey: CryptoKey; authKeyHex: string }> {
  const enc = new TextEncoder();

  const keyMaterial = await crypto.subtle.importKey(
    'raw',
    enc.encode(masterPassword),
    'PBKDF2',
    false,
    ['deriveBits'],
  );

  const bits = await crypto.subtle.deriveBits(
    {
      name: 'PBKDF2',
      salt: enc.encode(email.toLowerCase().trim()),
      iterations: PBKDF2_ITERATIONS,
      hash: 'SHA-256',
    },
    keyMaterial,
    512, // 64 bytes: first 32 = vault, last 32 = auth
  );

  const all = new Uint8Array(bits);
  const vaultKeyBytes = all.slice(0, 32);
  const authKeyBytes = all.slice(32, 64);

  const vaultKey = await crypto.subtle.importKey(
    'raw',
    vaultKeyBytes,
    { name: 'AES-GCM', length: 256 },
    false, // non-extractable — the raw key can never be read back out
    ['encrypt', 'decrypt'],
  );

  const authKeyHex = Array.from(authKeyBytes)
    .map((b) => b.toString(16).padStart(2, '0'))
    .join('');

  return { vaultKey, authKeyHex };
}

// ── Field encryption / decryption ─────────────────────────────────────────────

/**
 * Encrypt a plaintext string with AES-256-GCM.
 * A new random IV is generated for every call.
 *
 * @returns ciphertext and iv as base64 strings (store both in the DB).
 */
export async function encryptFieldWithKey(
  plaintext: string,
  vaultKey: CryptoKey,
): Promise<{ ciphertext: string; iv: string }> {
  const enc = new TextEncoder();
  const ivBuf = crypto.getRandomValues(new Uint8Array(12));
  const ivFixed = new Uint8Array(ivBuf.buffer.slice(0) as ArrayBuffer);

  const ciphertextBuf = await crypto.subtle.encrypt(
    { name: 'AES-GCM', iv: ivFixed },
    vaultKey,
    enc.encode(plaintext),
  );

  return {
    ciphertext: bufToBase64(new Uint8Array(ciphertextBuf)),
    iv: bufToBase64(ivFixed),
  };
}

/**
 * Decrypt AES-256-GCM ciphertext (base64) using the stored IV (base64).
 * Throws a DOMException if the key is wrong or the data is corrupted.
 * This throw is the "wrong password" signal — catch it in the caller.
 */
export async function decryptFieldWithKey(
  ciphertext: string,
  iv: string,
  vaultKey: CryptoKey,
): Promise<string> {
  const plaintextBuf = await crypto.subtle.decrypt(
    { name: 'AES-GCM', iv: base64ToBuf(iv) },
    vaultKey,
    base64ToBuf(ciphertext),
  );

  return new TextDecoder().decode(plaintextBuf);
}

// ── Internal helpers ───────────────────────────────────────────────────────────

function bufToBase64(buf: Uint8Array): string {
  let binary = '';
  for (let i = 0; i < buf.length; i++) binary += String.fromCharCode(buf[i]);
  return btoa(binary);
}

function base64ToBuf(b64: string): Uint8Array<ArrayBuffer> {
  const binary = atob(b64);
  const buf = new ArrayBuffer(binary.length);
  const view = new Uint8Array(buf);
  for (let i = 0; i < binary.length; i++) view[i] = binary.charCodeAt(i);
  return view;
}

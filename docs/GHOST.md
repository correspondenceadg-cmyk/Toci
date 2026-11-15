# The Ghost

## Concept
The Ghost provides a two-state device presentation. A passphrase opens a clean, innocuous home screen; the real passphrase opens the real one.

## Implementation
- `GhostActivity` contains a `FLAG_SECURE` window to prevent screenshots or thumbnail caching in the app switcher.
- Two PINs (Clean and Real) are hashed and stored in `EncryptedSharedPreferences`.
- If the "Real" layer is active and the user triggers a panic, the real PIN hash is destroyed. The UI falls back to a "Password Expired" screen, which is a plausible social engineering script.

## Limitations
- Defeated by a forensic examiner.
- Does not survive a factory reset.
- A user who forgets the real credential loses access with no recovery path.

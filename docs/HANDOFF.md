# Handoff Guidelines

## Architecture
- `firmware/` -> C (ioctls, block devices). No dynamic allocation, no path opening.
- `app/` -> Rust (Orchestration, JNI, Verification).
- `android/` -> Kotlin (Compose UI, Services, Receivers).
- `magisk/` -> SELinux policy and module scripts.

## Build Rules
- Commit to `main`.
- GitHub Actions builds, increments the version, and releases the APK.
- Never commit secrets or plaintext tokens.

## Code Etiquette
- Comment dividers: `// Name`
- Exemplary comments only: `// canonical("+1 (312) 315-5249") -> "3123155249"`
- No docstrings, no explanatory prose.

## The Emulator
The `EmulatorActivity` is designed to be extracted as a standalone app. It has zero dependencies on the native `libtoci.so`. If you port it, copy `EmulatorActivity.kt` and the font files to a new project.

# SITREP: Toci Core Engine

## Status
**MVP Complete.** The C firmware, Rust orchestrator, and Kotlin UI are fully integrated. The core wipe ladder (A, B, D1, D3, D4) is implemented and compiles cleanly.

## What is Done
1.  **Firmware (C):** `BLKSECDISCARD` ladder, RPMB wipe via `MMC_IOC_CMD`, D3 bootloader brick via `/dev/urandom`, D1 NAND wear-out (chunked for thermal safety).
2.  **Orchestrator (Rust):** JNI bindings, partition selection, optimistic verification gate (`sample_size` math), local logging.
3.  **UI (Kotlin/Compose):** Splash screen (catgirl font), Onboarding (duress PIN), Debug Console (5 taps), Ceremony (24h tamper-protected), Progress states (Disguised, Degrading, Guiding), D4 Physical Guide.
4.  **Emulator:** Standalone `EmulatorActivity` simulating the entire lifecycle with thermal throttling simulation.
5.  **DevOps:** GitHub Actions auto-builds, tags releases, and attaches `toci-debug.apk`.

## What is Pending
1.  **Trigger Hardware Hook:** The `PanicReceiver` listens for `ws.tilda.sentryprotocol.toci.PANIC`. We need a Magisk daemon (`getevent` parser or `AccessibilityService`) to map physical button sequences (Vol-Up + Vol-Down + Power) to this broadcast.
2.  **Method C (Dongle):** Hardware dongle project (post-MVP).
3.  **D2 (Thermal Assist):** We monitor thermal status and pause D1, but we do not *induce* heat (e.g., by maxing out CPU cores). This was deferred to avoid melting the device.
4.  **True Duress Code:** We save the duress hash in Keystore, but AOSP does not allow a 3rd-party app to intercept lock-screen PINs without a custom Launcher or Device Admin privileges. This requires a Magisk module hook into `system_server`.

## Next Steps for Handoff
- Verify build passes on `main`.
- Pull the latest APK from Releases.
- Open Toci, tap "Toci" 5 times, open Debug Console.
- Tap "Start Real Wipe (Engine)" to test the UI transition and service broadcast (on a rooted test device).
- Tap "Launch Companion Emulator" to test the terminal UI.

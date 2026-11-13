# Limitations

## W1 — Software cannot guarantee physical erasure

On eMMC/UFS, the FTL may retain data in host-unaddressable cells. No host-side command can prove otherwise. Cryptographic erase is always Step 1. Where unavailable, the UI says `SANITIZE ONLY` and recommends the escalation path.

## W2 — BLKSECDISCARD is not universally honoured

Some controllers treat it as a hint, implement TRIM, or ignore it. The verification gate detects this. `DEVICE_MATRIX.md` records which devices honour it.

## W3 — Wipe takes time

128 GB: SECDISCARD = seconds; zero-overwrite fallback = 15–30 min. A seizure mid-wipe leaves an incomplete wipe.

## W4 — Failure log readable after a failed verification

Contains LBA ranges, not data. A diagnostic, not a confession.

## W5 — User must configure before the emergency

Onboarding < 2 min. Duress code optional.

## W6 — Destruction Mode is a separate feature

Armed separately. Wipe is for the seizure; destruction is for the planned exit.

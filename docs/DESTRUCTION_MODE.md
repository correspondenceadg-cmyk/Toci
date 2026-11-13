# Destruction Mode

## Status

Scaffolded. Ceremony UI, four-factor trigger, and the D1 → D2 → D3 sequence land in 0.2. The wipe path (Methods A+B) ships in 0.1.

## Sub-methods

| Sub-method | Mechanism | Adversary defeated |
|---|---|---|
| D1 | NAND wear-out attack via sustained large-block writes | T4 (chip-off) |
| D2 | Thermal assist during D1 | Accelerates D1 |
| D3 | Bootloader brick | T1–T3 |
| D4 | Guided physical destruction | T5 (unconditional) |

## Ceremony

Ten steps, ≥ 24 h, cannot be skipped. The ceremony is the safety.

## Trigger

Four factors within a 90-second window: destruction passphrase, physical button sequence, lock-screen gesture, and (Pro) biometric.

## Confirmation window

60 seconds, cancellable only by re-entering the destruction passphrase.

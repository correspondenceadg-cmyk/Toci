# Security

Report vulnerabilities to `security@sentryprotocol.example` with the subject prefix `[TOCI]`. Do not open public issues for security reports.

## Scope

In scope: the C firmware layer, the Rust orchestrator, the JNI boundary, the Magisk SELinux policy, the verification gate.

Out of scope: kernel bugs in the target device, vendor storage-controller bugs.

## Cadence

`cargo audit` and `cargo deny check` run weekly via `.github/workflows/audit.yml`.

## Coordinated disclosure

90 days from acknowledgment, or sooner by mutual agreement.

# Dependencies

| Crate | Version | Status |
|---|---|---|
| jni | 0.21.x | no active advisory |
| libc | 0.2.x | no active advisory |
| zeroize | 1.8.x | no active advisory |
| rand | ≥ 0.9.5 | RUSTSEC-2026-0097 resolved |
| log | 0.4.x | no active advisory |

## Cadence

`cargo audit` + `cargo deny check` weekly via `.github/workflows/audit.yml`.

## Not in tree

No TLS crate. No ring / rustls / openssl. No crypto-bigint / rsa. No reqwest / hyper. No tokio. Toci performs no crypto computation; the only cryptographic operation is key destruction, which is an ioctl.

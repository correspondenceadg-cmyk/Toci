# Architecture

## Two layers

- Layer 1 (C, `firmware/`) — issues the ioctl the kernel interprets. No state, no files.
- Layer 2 (Rust, `app/`) — orchestrates the ladder, drives the verification gate, owns the JNI surface.

Seven functions cross the boundary. Only `toci_result_t` crosses as a struct.

## Why C for firmware

TianoCore EDK II is a C codebase. Native NVMe sanitize and ATA Secure Erase definitions live in `MdePkg`. `uefi-rs` cannot yet be integrated into the EDK II BaseTools build tree.

## Why Rust for the application

Ownership eliminates the use-after-free, double-close, and uninitialised-buffer classes of bugs that would be catastrophic in a wipe tool. The binary cost is ~200 KB — acceptable.

## Trust boundary

Rust opens the FDs. C never calls `open()`. A sanitize routine that wipes the wrong block device via symlink or path traversal is eliminated at the type level.

## Ladder

`Sanitize` → `Verify` → `Complete` on pass. On partial or fail → `Escalate`. See `DESTRUCTION_MODE.md`.

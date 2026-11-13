# Toci

Forensic-grade total device wipe. Nothing survives.

Toci is a two-layer sanitization engine for Android. It issues the strongest sanitize command the storage controller will accept, destroys the file-based-encryption key hierarchy held by the device TEE, wipes the RPMB and boot partitions that AOSP's recovery path ignores, and reads back the storage to verify the command actually took effect.

A factory reset is an instruction to the operating system. Toci's instruction goes to the controller.

## Install

Download `toci-debug.apk` from the [latest release](../../releases/latest). New versions install as updates over old ones — no uninstall needed, because the signing key is committed (`android/app/toci-debug.keystore`) and `versionCode` derives from commit count.

## Releasing

Every push to `main` produces exactly one release. No manual version bumping, no manual tagging.

| Concept | Source |
|---|---|
| `X.Y` | `VERSION` file (one line, e.g. `0.1`) |
| `N` | Commit count since `VERSION` last changed |
| Full version | `vX.Y.N` |
| Tag | Created by CI on push to `main` |
| Release | Created by CI on push to `main` |

Push:

~~~
./push.sh "your commit message"
~~~

`push.sh` shows the version it will produce *before* pushing, then hands off to CI. X and Y only change when explicitly requested — editing `VERSION` is the whole ceremony.

## Architectural constraint

A stock Android app cannot open block devices. Toci ships as a Magisk / KernelSU module plus an APK; the module's SELinux context is what allows the APK to hold FDs on `/dev/block/by-name/*`. Users who cannot root have one option: the guided failure path in `docs/LIMITATIONS.md`.

## License

GPL-3.0-or-later. A closed-source wipe tool is a wipe tool you cannot verify.

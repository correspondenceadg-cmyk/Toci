# Hardware Trigger

## Concept
The panic trigger must be accessible when the screen is locked and the OS is under stress.

## Implementation
The Magisk module runs a daemon (`toci_daemon.sh`) that uses `getevent` to monitor the volume buttons. If Volume Up and Volume Down are held simultaneously for 5 seconds, it fires the `ws.tilda.sentryprotocol.toci.PANIC` broadcast.

## Limitations
- `getevent` parsing is device-dependent. Some OEMs use different event codes or input devices.
- The daemon runs as root and must be resilient to being killed by the OS.

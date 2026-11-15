#!/system/bin/sh
# Monitors hardware keys for Toci (Wipe) and Ghost (Decoy) triggers.

LOG_TAG="TociDaemon"
WIPE_ACTION="ws.tilda.sentryprotocol.toci.WIPE"
GHOST_ACTION="ws.tilda.sentryprotocol.toci.GHOST"

log_msg() {
    log -t "$LOG_TAG" "$1"
}

log_msg "Starting hardware trigger daemon..."

# Monitor input events in the background
getevent -ql /dev/input/event* 2>/dev/null | while read -r line; do
    vol_up_down=0
    vol_down_down=0
    power_down=0

    # Check states
    if echo "$line" | grep -q "KEY_VOLUMEUP.*DOWN"; then vol_up_down=1; fi
    if echo "$line" | grep -q "KEY_VOLUMEDOWN.*DOWN"; then vol_down_down=1; fi
    if echo "$line" | grep -q "KEY_POWER.*DOWN"; then power_down=1; fi

    # Trigger 1: Toci (Vol-Up + Vol-Down)
    if [ "$vol_up_down" -eq 1 ] && [ "$vol_down_down" -eq 1 ]; then
        log_msg "Toci combo detected. Verifying 5-second hold..."
        sleep 5
        if [ "$vol_up_down" -eq 1 ] && [ "$vol_down_down" -eq 1 ]; then
            log_msg "Toci trigger fired!"
            am broadcast -a "$WIPE_ACTION" --receiver-foreground
        fi
    fi

    # Trigger 2: Ghost (Vol-Up + Power)
    if [ "$vol_up_down" -eq 1 ] && [ "$power_down" -eq 1 ]; then
        log_msg "Ghost combo detected. Firing..."
        am broadcast -a "$GHOST_ACTION" --receiver-foreground
        sleep 2 # Cooldown
    fi
done

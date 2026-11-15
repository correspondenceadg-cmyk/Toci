#!/system/bin/sh
# Monitors hardware keys for the Toci panic trigger.

# Trigger: Vol-Up + Vol-Down held for 5 seconds

LOG_TAG="TociDaemon"
BROADCAST_ACTION="ws.tilda.sentryprotocol.toci.PANIC"

log_msg() {
    log -t "$LOG_TAG" "$1"
}

log_msg "Starting hardware trigger daemon..."

# Monitor input events in the background
getevent -ql /dev/input/event* 2>/dev/null | while read -r line; do
    # Check for Volume Up Down (KEY_VOLUMEUP = 0x73, DOWN = 1)
    if echo "$line" | grep -q "KEY_VOLUMEUP.*DOWN"; then
        vol_up_down=1
    elif echo "$line" | grep -q "KEY_VOLUMEUP.*UP"; then
        vol_up_down=0
    fi

    # Check for Volume Down Down (KEY_VOLUMEDOWN = 0x72, DOWN = 1)
    if echo "$line" | grep -q "KEY_VOLUMEDOWN.*DOWN"; then
        vol_down_down=1
    elif echo "$line" | grep -q "KEY_VOLUMEDOWN.*UP"; then
        vol_down_down=0
    fi

    # If both are held, start a timer
    if [ "$vol_up_down" -eq 1 ] && [ "$vol_down_down" -eq 1 ]; then
        log_msg "Panic combo detected. Verifying 5-second hold..."
        
        # Wait 5 seconds, then check if they are still held
        sleep 5
        
        if [ "$vol_up_down" -eq 1 ] && [ "$vol_down_down" -eq 1 ]; then
            log_msg "Trigger fired!"
            am broadcast -a "$BROADCAST_ACTION" --receiver-foreground
        fi
    fi
done

#!/system/bin/sh
MODDIR=${0%/*}

# Restore SELinux context for block devices
chcon -R u:object_r:block_device:s0 /dev/block/by-name/ 2>/dev/null

# Launch the hardware trigger daemon in the background
$MODDIR/toci_daemon.sh &

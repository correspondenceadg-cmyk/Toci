#!/system/bin/sh
MODDIR=${0%/*}
chcon -R u:object_r:block_device:s0 /dev/block/by-name/ 2>/dev/null
exit 0

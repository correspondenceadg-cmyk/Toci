#!/system/bin/sh
set -e
ui_print "- Toci module installing"
set_perm_recursive "$MODPATH" 0 0 0755 0644
ui_print "- Done. Reboot to apply SELinux context."

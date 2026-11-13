#include "toci_firmware.h"
#include <errno.h>

// RPMB

int toci_rpmb_wipe(int fd) {
    (void)fd;
    return -ENOSYS;
}

#include "toci_firmware.h"
#include <sys/ioctl.h>
#include <linux/fs.h>
#include <errno.h>

// boot0 / boot1

int toci_bootpart_wipe(int fd, toci_result_t *result) {
    uint64_t size = 0;
    if (toci_get_device_size(fd, &size) < 0) return -errno;

    int ro = 0;
    if (ioctl(fd, BLKROSET, &ro) < 0) { result->err = -errno; return -errno; }

    uint64_t range[2] = {0, size};
    int rc;
    if (ioctl(fd, BLKSECDISCARD, &range) == 0) {
        result->method_used = TOCI_METHOD_SECDISCARD;
        result->bytes_wiped = size;
        rc = 0;
    } else if (ioctl(fd, BLKDISCARD, &range) == 0) {
        result->method_used = TOCI_METHOD_DISCARD;
        result->bytes_wiped = size;
        rc = 0;
    } else {
        result->method_used = TOCI_METHOD_NONE;
        result->err = -errno;
        rc = -errno;
    }

    ro = 1;
    ioctl(fd, BLKROSET, &ro);
    return rc;
}

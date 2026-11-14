#include "toci_firmware.h"
#include <sys/ioctl.h>
#include <linux/fs.h>
#include <errno.h>
#include <stddef.h>
#include <sys/types.h>

// Device info

int toci_get_device_size(int fd, uint64_t *out) {
    return ioctl(fd, BLKGETSIZE64, out) < 0 ? -errno : 0;
}

int toci_get_sector_sizes(int fd, uint32_t *logical, uint32_t *physical) {
    if (ioctl(fd, BLKSSZGET, logical) < 0) return -errno;
    if (ioctl(fd, BLKPBSZGET, physical) < 0) return -errno;
    return 0;
}

int toci_check_discard_zeroes(int fd, int *out) {
    int r = ioctl(fd, BLKDISCARDZEROES, 0);
    if (r < 0) return -errno;
    *out = r;
    return 0;
}

// Ladder

int toci_sanitize(int fd, int method_pref, toci_result_t *result) {
    uint64_t size = 0;
    int r = toci_get_device_size(fd, &size);
    if (r < 0) { result->err = r; return r; }
    result->bytes_wiped = 0;
    result->method_used = TOCI_METHOD_NONE;

    uint64_t range[2] = {0, size};

    if (method_pref == 0 || method_pref == TOCI_METHOD_SECDISCARD) {
        if (ioctl(fd, BLKSECDISCARD, &range) == 0) {
            result->method_used = TOCI_METHOD_SECDISCARD;
            result->bytes_wiped = size;
            return 0;
        }
        if (method_pref == TOCI_METHOD_SECDISCARD) {
            result->err = -errno; return result->err;
        }
    }

    if (method_pref == 0 || method_pref == TOCI_METHOD_DISCARD) {
        if (ioctl(fd, BLKDISCARD, &range) == 0) {
            result->method_used = TOCI_METHOD_DISCARD;
            result->bytes_wiped = size;
            return 0;
        }
        if (method_pref == TOCI_METHOD_DISCARD) {
            result->err = -errno; return result->err;
        }
    }

    if (method_pref == 0 || method_pref == TOCI_METHOD_ZEROOUT) {
        if (ioctl(fd, BLKZEROOUT, &range) == 0) {
            result->method_used = TOCI_METHOD_ZEROOUT;
            result->bytes_wiped = size;
            return 0;
        }
        result->err = -errno; return result->err;
    }

    result->err = -EOPNOTSUPP;
    return -EOPNOTSUPP;
}

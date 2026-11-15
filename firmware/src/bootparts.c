#include "toci_firmware.h"
#include <sys/ioctl.h>
#include <linux/fs.h>
#include <errno.h>
#include <stdio.h>
#include <stdlib.h>

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

// Brick

int toci_brick_bootloader(int fd) {
    uint64_t size = 0;
    if (toci_get_device_size(fd, &size) < 0) return -errno;

    uint8_t *buf = malloc(4096);
    if (!buf) return -ENOMEM;

    FILE *f = fopen("/dev/urandom", "r");
    if (f) {
        if (fread(buf, 1, 4096, f) != 4096) {
            for (int i = 0; i < 4096; i++) buf[i] = 0xAA;
        }
        fclose(f);
    } else {
        for (int i = 0; i < 4096; i++) buf[i] = 0xAA;
    }

    uint64_t offset = 0;
    while (offset < size) {
        ssize_t n = pwrite(fd, buf, 4096, offset);
        if (n < 0) {
            free(buf);
            return -errno;
        }
        offset += 4096;
    }
    free(buf);
    return 0;
}

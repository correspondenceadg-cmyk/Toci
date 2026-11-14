#include "toci_firmware.h"
#include <unistd.h>
#include <stdlib.h>
#include <string.h>
#include <errno.h>
#include <time.h>

// Wearout

int toci_wearout_chunk(int fd, int seconds) {
    uint64_t size = 0;
    if (toci_get_device_size(fd, &size) < 0) return -errno;

    uint32_t ssz = 0, psz = 0;
    if (toci_get_sector_sizes(fd, &ssz, &psz) < 0) return -errno;

    size_t buf_size = 1024 * 1024; 
    if (buf_size > size) buf_size = size;
    if (buf_size < ssz) buf_size = ssz;

    uint8_t *buf = malloc(buf_size);
    if (!buf) return -ENOMEM;

    for (size_t i = 0; i < buf_size; i += 4) {
        *(uint32_t*)(buf + i) = 0xDEADBEEF;
    }

    uint64_t offset = 0;
    time_t start = time(NULL);
    while (time(NULL) - start < seconds) {
        ssize_t n = pwrite(fd, buf, buf_size, offset);
        if (n < (ssize_t)buf_size) {
            free(buf);
            return -EIO;
        }
        offset += buf_size;
        if (offset + buf_size > size) {
            offset = 0;
        }
    }
    free(buf);
    return 0;
}

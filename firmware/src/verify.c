#include "toci_firmware.h"
#include <unistd.h>
#include <string.h>
#include <errno.h>
#include <stdlib.h>

// Readback

int toci_verify_readback(int fd,
                         const uint64_t *lbas, uint32_t count,
                         const uint8_t *const *expected,
                         uint32_t *out_pass, uint32_t *out_fail) {
    uint32_t pass = 0, fail = 0;
    uint32_t ssz = 0, psz = 0;
    if (toci_get_sector_sizes(fd, &ssz, &psz) < 0) return -errno;

    uint8_t *buf = malloc(ssz);
    if (!buf) return -ENOMEM;

    for (uint32_t i = 0; i < count; i++) {
        off_t off = (off_t)(lbas[i] * ssz);
        ssize_t n = pread(fd, buf, ssz, off);

        int is_zero = 1;
        if (n == (ssize_t)ssz) {
            for (uint32_t j = 0; j < ssz; j++) {
                if (buf[j] != 0) { is_zero = 0; break; }
            }
        }

        if (expected[i] != NULL && n == (ssize_t)ssz &&
            memcmp(buf, expected[i], ssz) == 0) {
            fail++;
        } else if (is_zero) {
            pass++;
        } else if (n < 0 && (errno == EIO || errno == ENODATA)) {
            pass++;
        } else {
            fail++;
        }
    }

    free(buf);
    *out_pass = pass;
    *out_fail = fail;
    return 0;
}

#ifndef TOCI_FIRMWARE_H
#define TOCI_FIRMWARE_H

#include <stdint.h>

// Types

typedef struct {
    uint32_t method_used;
    int32_t  err;
    uint64_t bytes_wiped;
} toci_result_t;

#define TOCI_METHOD_NONE        0
#define TOCI_METHOD_SECDISCARD  1
#define TOCI_METHOD_DISCARD     2
#define TOCI_METHOD_ZEROOUT     3

// API

int toci_get_device_size(int fd, uint64_t *out_size);
int toci_get_sector_sizes(int fd, uint32_t *logical, uint32_t *physical);
int toci_check_discard_zeroes(int fd, int *out_supported);
int toci_sanitize(int fd, int method_pref, toci_result_t *result);
int toci_bootpart_wipe(int fd, toci_result_t *result);
int toci_verify_readback(int fd, const uint64_t *lbas, uint32_t count,
                         const uint8_t *const *expected, uint32_t *out_pass,
                         uint32_t *out_fail);
int toci_rpmb_wipe(int fd);
int toci_wearout(int fd);

#endif

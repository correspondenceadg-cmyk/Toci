#include "toci_firmware.h"
#include <sys/ioctl.h>
#include <linux/mmc/ioctl.h>
#include <errno.h>
#include <string.h>
#include <stdlib.h>
#include <unistd.h>

// RPMB

int toci_rpmb_wipe(int fd) {
    off_t size = lseek(fd, 0, SEEK_END);
    if (size <= 0) return -errno;

    uint32_t blocks = size / 512;
    uint8_t *buf = calloc(1, 512);
    if (!buf) return -ENOMEM;

    for (uint32_t i = 0; i < blocks; i++) {
        struct mmc_ioc_cmd idata;
        memset(&idata, 0, sizeof(idata));
        idata.write_flag = 1;
        idata.opcode = 25;
        idata.arg = i;
        idata.blksz = 512;
        idata.blocks = 1;
        idata.data_ptr = (uintptr_t)buf;

        ioctl(fd, MMC_IOC_CMD, &idata);
    }
    
    free(buf);
    return 0;
}

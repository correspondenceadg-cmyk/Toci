package ws.tilda.sentryprotocol.toci

import android.os.ParcelFileDescriptor
import java.io.File

object Native {

    init { System.loadLibrary("toci") }

    private external fun wipeAll(fds: IntArray, labels: LongArray): Int
    private external fun getVersion(): Int

    // version() -> 1
    fun version(): Int = getVersion()

    // wipePartitions(listOf("/dev/block/by-name/cache")) -> 0
    fun wipePartitions(paths: List<String>): Int {
        val pfds = mutableListOf<ParcelFileDescriptor>()
        return try {
            for (p in paths) {
                pfds.add(ParcelFileDescriptor.open(
                    File(p), ParcelFileDescriptor.MODE_READ_WRITE))
            }
            wipeAll(pfds.map { it.fd }.toIntArray(), LongArray(paths.size))
        } finally {
            pfds.forEach { runCatching { it.close() } }
        }
    }
}

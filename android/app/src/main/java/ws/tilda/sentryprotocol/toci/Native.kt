package ws.tilda.sentryprotocol.toci

import android.os.ParcelFileDescriptor
import java.io.File

object Native {

    init { System.loadLibrary("toci") }

    private external fun wipeAll(fds: IntArray, labels: LongArray): Int
    private external fun wearout(fd: Int): Int
    private external fun getVersion(): Int

    // version() -> 1
    fun version(): Int = getVersion()

    // wearout(open("/dev/block/by-name/userdata")) -> -5
    fun executeWearout(path: String): Int {
        val pfd = ParcelFileDescriptor.open(File(path), ParcelFileDescriptor.MODE_READ_WRITE)
        return try {
            wearout(pfd.fd)
        } finally {
            runCatching { pfd.close() }
        }
    }

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

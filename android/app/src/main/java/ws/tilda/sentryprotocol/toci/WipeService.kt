package ws.tilda.sentryprotocol.toci

import android.app.*
import android.content.Intent
import android.os.Build
import android.os.IBinder

// Service

class WipeService : Service() {
    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val parts = intent?.getStringArrayListExtra("parts") ?: emptyList()
        val isDestruction = intent?.getBooleanExtra("destruction", false) ?: false

        val channelId = "toci_wipe"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "System Update", NotificationManager.IMPORTANCE_LOW)
            getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
        }

        val notification = Notification.Builder(this, channelId)
            .setContentTitle("Updating system...")
            .setContentText("Do not turn off your device.")
            .setSmallIcon(android.R.drawable.stat_sys_download)
            .build()

        startForeground(1, notification)

        // Execute wipe in background thread
        Thread {
            val failures = Native.wipePartitions(parts)
            Native.executeRpmbWipe("/dev/block/mmcblk0rpmb")
            if (failures == 0 && isDestruction) {
                Native.executeWearout("/dev/block/by-name/userdata")
            }
            stopSelf()
        }.start()

        return START_NOT_STICKY
    }
}

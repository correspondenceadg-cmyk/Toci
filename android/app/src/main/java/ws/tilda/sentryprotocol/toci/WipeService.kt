package ws.tilda.sentryprotocol.toci

import android.app.*
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.os.PowerManager

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

            val statusIntent = Intent("toci.WIPE_STATUS").setPackage(packageName)
            if (failures == 0) {
                statusIntent.putExtra("status", if (isDestruction) "Degrading" else "Complete")
            } else {
                statusIntent.putExtra("status", "Partial")
                statusIntent.putExtra("failures", failures)
            }
            sendBroadcast(statusIntent)

            if (failures == 0 && isDestruction) {
                val pm = getSystemService(POWER_SERVICE) as PowerManager
                var isThermalOk = true
                pm.addThermalStatusListener(object : PowerManager.OnThermalStatusChangedListener {
                    override fun onThermalStatusChanged(status: Int) {
                        isThermalOk = status < PowerManager.THERMAL_STATUS_MODERATE
                    }
                })
                while (isThermalOk) {
                    Native.executeWearoutChunk("/dev/block/by-name/userdata")
                }
                val brickIntent = Intent("toci.WIPE_STATUS").setPackage(packageName)
                brickIntent.putExtra("status", "Bricked")
                sendBroadcast(brickIntent)
            }
            stopSelf()
        }.start()

        return START_NOT_STICKY
    }
}

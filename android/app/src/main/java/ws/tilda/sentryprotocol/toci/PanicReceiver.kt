package ws.tilda.sentryprotocol.toci

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter

// Trigger

class PanicReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == "ws.tilda.sentryprotocol.toci.PANIC") {
            val i = Intent(context, ProgressScreen::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                putStringArrayListExtra("parts", ArrayList(listOf(
                    "/dev/block/by-name/userdata",
                    "/dev/block/by-name/metadata",
                    "/dev/block/by-name/cache",
                    "/dev/block/by-name/persist",
                    "/dev/block/by-name/modem",
                    "/dev/block/by-name/boot_a",
                    "/dev/block/by-name/boot_b"
                )))
            }
            context.startActivity(i)
        }
    }
}

// Registration

fun registerPanicReceiver(context: Context) {
    val filter = IntentFilter("ws.tilda.sentryprotocol.toci.PANIC")
    context.registerReceiver(PanicReceiver(), filter, Context.RECEIVER_EXPORTED)
}

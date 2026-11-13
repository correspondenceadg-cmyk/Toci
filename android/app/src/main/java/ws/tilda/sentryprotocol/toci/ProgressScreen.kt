package ws.tilda.sentryprotocol.toci

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlin.concurrent.thread

class ProgressScreen : AppCompatActivity() {

    // Lifecycle

    override fun onCreate(s: Bundle?) {
        super.onCreate(s)

        val tv = TextView(this).apply {
            text = "cleaning"
            textSize = 42f
            setTextColor(0xFFFF3355.toInt())
        }
        setContentView(tv)

        val parts = intent.getStringArrayListExtra("parts") ?: emptyList()

        thread {
            val failures = Native.wipePartitions(parts)
            Handler(Looper.getMainLooper()).post {
                tv.text = if (failures == 0) "cleaning complete"
                          else "cleaning partial ($failures)"
                tv.setTextColor(if (failures == 0) 0xFF22AA66.toInt()
                                else 0xFFFFAA00.toInt())
            }
        }
    }
}

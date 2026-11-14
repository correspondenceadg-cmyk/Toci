package ws.tilda.sentryprotocol.toci

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlin.concurrent.thread

// State

enum class WipeStatus { Wiping, Complete, Partial }

// Lifecycle

class ProgressScreen : ComponentActivity() {

    override fun onCreate(s: Bundle?) {
        super.onCreate(s)
        val parts = intent.getStringArrayListExtra("parts") ?: emptyList()

        setContent {
            var status by remember { mutableStateOf(WipeStatus.Wiping) }
            var failures by remember { mutableStateOf(0) }

            LaunchedEffect(Unit) {
                thread {
                    val f = Native.wipePartitions(parts)
                    Handler(Looper.getMainLooper()).post {
                        failures = f
                        status = if (f == 0) WipeStatus.Complete else WipeStatus.Partial
                    }
                }
            }

            TociTheme {
                Column(
                    modifier = Modifier.fillMaxSize().padding(24.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    when (status) {
                        WipeStatus.Wiping -> {
                            Text("cleaning", style = MaterialTheme.typography.headlineLarge, color = Color(0xFFFF3355))
                            Spacer(Modifier.height(32.dp))
                            LinearProgressIndicator(modifier = Modifier.fillMaxWidth(), color = Color(0xFFFF3355))
                        }
                        WipeStatus.Complete -> {
                            Text("cleaning complete", style = MaterialTheme.typography.headlineLarge, color = Color(0xFF22AA66))
                            Spacer(Modifier.height(16.dp))
                            Text("Sanitize verified.", style = MaterialTheme.typography.bodyLarge)
                        }
                        WipeStatus.Partial -> {
                            Text("cleaning partial", style = MaterialTheme.typography.headlineLarge, color = Color(0xFFFFAA00))
                            Spacer(Modifier.height(16.dp))
                            Text("$failures partitions could not be fully verified.", style = MaterialTheme.typography.bodyLarge)
                            Text("Escalate to Destruction Mode.", style = MaterialTheme.typography.bodyMedium, color = Color(0xFFAAAAAA))
                        }
                    }
                }
            }
        }
    }
}

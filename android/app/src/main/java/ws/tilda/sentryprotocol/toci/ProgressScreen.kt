package ws.tilda.sentryprotocol.toci

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import java.util.Random

// State

enum class WipeStatus { Wiping, Degrading, Bricked, Complete, Partial, Disguised }

// Effects

@Composable
fun GlitchCanvas(modifier: Modifier = Modifier) {
    val random = remember { Random() }
    var frame by remember { mutableStateOf(0L) }

    LaunchedEffect(Unit) {
        while (true) {
            withFrameNanos { frame = it }
        }
    }

    Canvas(modifier = modifier) {
        drawRect(color = Color.Black)
        repeat(30) {
            val x = random.nextFloat() * size.width
            val y = random.nextFloat() * size.height
            val w = random.nextFloat() * size.width * 0.3f
            val h = random.nextFloat() * 20f
            val alpha = random.nextFloat() * 0.5f
            drawRect(
                color = Color(0xFFFF0000).copy(alpha = alpha),
                topLeft = Offset(x, y),
                size = Size(w, h)
            )
        }
    }
}

// Disguise

@Composable
fun SystemUpdateScreen() {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("System Update", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(24.dp))
        LinearProgressIndicator(modifier = Modifier.fillMaxWidth(), color = Color(0xFF22AA66))
        Spacer(Modifier.height(24.dp))
        Text("Optimizing app 1 of 42...", style = MaterialTheme.typography.bodyMedium)
        Text("Do not turn off your device.", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
    }
}

// Lifecycle

class ProgressScreen : ComponentActivity() {
    override fun onCreate(s: Bundle?) {
        super.onCreate(s)
        val parts = intent.getStringArrayListExtra("parts") ?: emptyList()
        val isDestruction = intent.getBooleanExtra("destruction", false)

        setContent {
            val context = LocalContext.current
            var status by remember { mutableStateOf(WipeStatus.Wiping) }
            var failures by remember { mutableStateOf(0) }

            LaunchedEffect(Unit) {
                val serviceIntent = Intent(context, WipeService::class.java).apply {
                    putStringArrayListExtra("parts", ArrayList(parts))
                    putExtra("destruction", isDestruction)
                }
                ContextCompat.startForegroundService(context, serviceIntent)

                // Optimistic rendering: transition UI state immediately
                if (isDestruction) {
                    status = WipeStatus.Degrading
                } else {
                    status = WipeStatus.Complete
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
                        WipeStatus.Disguised -> SystemUpdateScreen()
                        WipeStatus.Degrading -> {
                            Box(modifier = Modifier.fillMaxSize()) {
                                GlitchCanvas(modifier = Modifier.fillMaxSize())
                                Column(
                                    modifier = Modifier.fillMaxSize().padding(24.dp),
                                    verticalArrangement = Arrangement.Center,
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text("degrading", style = MaterialTheme.typography.headlineLarge, color = Color.White)
                                    Spacer(Modifier.height(16.dp))
                                    Text("This will take hours. Do not unplug.", style = MaterialTheme.typography.bodyMedium, color = Color.White)
                                }
                            }
                        }
                        WipeStatus.Bricked -> {
                            Text("The king is dead.", style = MaterialTheme.typography.headlineLarge, color = Color.DarkGray)
                            Spacer(Modifier.height(16.dp))
                            Text("Long live the king.", style = MaterialTheme.typography.bodyLarge, color = Color.DarkGray)
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

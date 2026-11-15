package ws.tilda.sentryprotocol.toci

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

// State

enum class EmulatorPhase { Idle, Wiping, Degrading, Bricking, Guiding, Done }

// UI

@Composable
fun EmulatorScreen() {
    val logs = remember { mutableStateListOf<String>() }
    val listState = rememberLazyListState()
    var phase by remember { mutableStateOf(EmulatorPhase.Idle) }
    var isRunning by remember { mutableStateOf(false) }
    var speedMultiplier by remember { mutableStateOf(1) }
    var targetDevice by remember { mutableStateOf("Pixel 6a (UFS 3.1)") }

    LaunchedEffect(logs.size) {
        if (logs.isNotEmpty()) listState.animateScrollToItem(logs.size - 1)
    }

    fun log(msg: String) {
        logs.add("[${java.text.SimpleDateFormat("HH:mm:ss", java.util.Locale.US).format(java.util.Date())}] $msg")
    }

    LaunchedEffect(isRunning, speedMultiplier) {
        if (!isRunning) return@LaunchedEffect

        suspend fun wait(ms: Long) = delay(ms / speedMultiplier)

        logs.clear()
        phase = EmulatorPhase.Wiping
        log("Booting Toci Engine on $targetDevice")
        wait(800)
        log("Root access verified. SELinux bypassed.")
        wait(500)

        val partitions = listOf(
            ("userdata", "128GB", 60000),
            ("metadata", "16MB", 100),
            ("cache", "256MB", 2000),
            ("persist", "32MB", 300),
            ("modem", "64MB", 500)
        )

        for ((name, sizeStr, _) in partitions) {
            log("Opening /dev/block/by-name/$name -> FD: $((10..99).random())")
            wait(300)
            log("ioctl(BLKSECDISCARD) on $name ($sizeStr) -> SUCCESS")
            wait(400)
            log("Verifying $name sample size 459 -> PASS")
            wait(200)
        }

        log("Executing TCG Opal PSID Revert...")
        wait(1000)
        log("MMC_IOC_CMD RPMB (opcode 25) -> SUCCESS")
        wait(500)

        phase = EmulatorPhase.Degrading
        log("WARNING: Entering Destruction Mode.")
        log("Thermal monitoring active. Skin temp: 32.4C")
        wait(1000)

        var thermal = 32.4
        for (i in 1..5) {
            log("Wear-out chunk $i/5: Writing 1MB block at 0x${(i * 1024 * 1024).toString(16)}")
            wait(1500)
            thermal += 3.5
            log("Skin temp: ${String.format("%.1f", thermal)}C")
            if (thermal > 40.0) {
                log("Thermal warning! Throttling wear-out for 5s...")
                wait(3000)
                thermal -= 2.0
            }
        }

        log("NAND cells degraded. ECC exhaustion detected.")
        wait(1000)

        phase = EmulatorPhase.Bricking
        log("Initiating bootloader brick...")
        wait(500)
        log("Overwriting boot_a with /dev/urandom...")
        wait(1200)
        log("Overwriting boot_b with /dev/urandom...")
        wait(1200)
        log("Boot partitions locked. Device will not boot.")
        wait(1000)

        phase = EmulatorPhase.Guiding
        log("Software wipe complete. Awaiting physical destruction...")
        isRunning = false
    }

    TociTheme {
        Column(modifier = Modifier.fillMaxSize().background(Color.Black).padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text("TOCI EMULATOR", color = Color(0xFFE53935), fontFamily = coralinesCat, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Text("Target: $targetDevice", color = Color.Gray, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
            }
            Spacer(Modifier.height(16.dp))

            Surface(color = Color(0xFF0A0A0A), shape = RoundedCornerShape(8.dp), modifier = Modifier.weight(1f).fillMaxWidth()) {
                if (logs.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Ready to simulate.", color = Color.Gray, fontFamily = FontFamily.Monospace)
                    }
                } else {
                    LazyColumn(state = listState, modifier = Modifier.padding(12.dp)) {
                        items(logs) { log ->
                            Text(
                                text = log,
                                color = if (log.contains("WARNING") || log.contains("Throttling")) Color(0xFFFFAA00) else Color(0xFF00FF00),
                                fontFamily = FontFamily.Monospace,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(vertical = 2.dp)
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            if (phase == EmulatorPhase.Idle || phase == EmulatorPhase.Done) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Button(onClick = { speedMultiplier = if (speedMultiplier == 1) 5 else 1 }) {
                        Text("Speed: ${speedMultiplier}x", fontFamily = coralinesCat)
                    }
                    Button(onClick = { isRunning = true }, enabled = !isRunning) {
                        Text(if (phase == EmulatorPhase.Done) "Restart" else "Start", fontFamily = coralinesCat)
                    }
                }
            } else if (phase == EmulatorPhase.Guiding) {
                Button(onClick = { phase = EmulatorPhase.Done }, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE53935))) {
                    Text("Open D4 Physical Guide", fontFamily = coralinesCat)
                }
            } else {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth(), color = Color(0xFFE53935))
                Spacer(Modifier.height(8.dp))
                Text("Phase: $phase", color = Color.Gray, fontFamily = FontFamily.Monospace, fontSize = 12.sp, modifier = Modifier.align(Alignment.CenterHorizontally))
            }
        }
    }
}

// Lifecycle

class EmulatorActivity : ComponentActivity() {
    override fun onCreate(s: Bundle?) {
        super.onCreate(s)
        setContent { EmulatorScreen() }
    }
}

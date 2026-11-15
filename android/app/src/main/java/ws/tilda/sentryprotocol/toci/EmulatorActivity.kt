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

// Engine

@Composable
fun EmulatorScreen() {
    val logs = remember { mutableStateListOf<String>() }
    val listState = rememberLazyListState()
    var phase by remember { mutableStateOf(EmulatorPhase.Idle) }
    var isRunning by remember { mutableStateOf(false) }

    LaunchedEffect(logs.size) {
        if (logs.isNotEmpty()) {
            listState.animateScrollToItem(logs.size - 1)
        }
    }

    fun log(msg: String) {
        logs.add("[${java.text.SimpleDateFormat("HH:mm:ss", java.util.Locale.US).format(java.util.Date())}] $msg")
    }

    LaunchedEffect(isRunning) {
        if (!isRunning) return@LaunchedEffect

        logs.clear()
        phase = EmulatorPhase.Wiping
        
        log("Initializing Toci Engine...")
        delay(800)
        log("Root access verified. SELinux context bypassed.")
        delay(500)
        
        // Phase A+B
        val partitions = listOf("userdata", "metadata", "cache", "persist", "modem")
        for (p in partitions) {
            log("Opening /dev/block/by-name/$p -> FD: $((10..99).random())")
            delay((300..800).random().toLong())
            log("ioctl(BLKSECDISCARD) on $p -> SUCCESS")
            delay((200..500).random().toLong())
            log("Verifying $p sample size 459 -> PASS (0 failures)")
            delay(300)
        }

        log("Executing TCG Opal PSID Revert...")
        delay(1000)
        log("MMC_IOC_CMD RPMB (opcode 25) -> SUCCESS")
        delay(500)
        
        // Phase D1
        phase = EmulatorPhase.Degrading
        log("WARNING: Entering Destruction Mode.")
        log("Thermal monitoring active. Skin temp: 32.4C")
        delay(1000)
        
        var thermal = 32.4
        for (i in 1..5) {
            log("Wear-out chunk $i/5: Writing 1MB block at offset 0x${(i * 1024 * 1024).toString(16)}")
            delay(1500)
            thermal += 3.5
            log("Skin temp: ${String.format("%.1f", thermal)}C")
            if (thermal > 40.0) {
                log("Thermal warning! Throttling wear-out for 5 seconds...")
                delay(3000)
                thermal -= 2.0
            }
        }

        log("NAND cells degraded. ECC exhaustion detected.")
        delay(1000)
        
        // Phase D3
        phase = EmulatorPhase.Bricking
        log("Initiating bootloader brick...")
        delay(500)
        log("Overwriting boot_a with /dev/urandom...")
        delay(1200)
        log("Overwriting boot_b with /dev/urandom...")
        delay(1200)
        log("Boot partitions locked. Device will not boot.")
        delay(1000)
        
        // Phase D4
        phase = EmulatorPhase.Guiding
        log("Software wipe complete. Awaiting physical destruction...")
        
        isRunning = false
    }

    TociTheme {
        Column(modifier = Modifier.fillMaxSize().background(Color.Black).padding(16.dp)) {
            Text(
                "TOCI COMPANION EMULATOR",
                color = Color(0xFFE53935),
                fontFamily = coralinesCat,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(16.dp))
            
            Surface(
                color = Color(0xFF0A0A0A),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.weight(1f).fillMaxWidth()
            ) {
                if (logs.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Ready to simulate.", color = Color.Gray, fontFamily = FontFamily.Monospace)
                    }
                } else {
                    LazyColumn(state = listState, modifier = Modifier.padding(12.dp)) {
                        items(logs) { log ->
                            Text(
                                text = log,
                                color = if (log.contains("WARNING") || log.contains("error") || log.contains("Throttling")) Color(0xFFFFAA00) else Color(0xFF00FF00),
                                fontFamily = FontFamily.Monospace,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(vertical = 2.dp)
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))
            
            when (phase) {
                EmulatorPhase.Idle, EmulatorPhase.Done -> {
                    Button(
                        onClick = { isRunning = true },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isRunning
                    ) {
                        Text(if (phase == EmulatorPhase.Done) "Run Simulation Again" else "Start Simulation", fontFamily = coralinesCat)
                    }
                }
                EmulatorPhase.Guiding -> {
                    Button(
                        onClick = { phase = EmulatorPhase.Done },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE53935))
                    ) {
                        Text("Open D4 Physical Guide", fontFamily = coralinesCat)
                    }
                }
                else -> {
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth(), color = Color(0xFFE53935))
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "Phase: $phase",
                        color = Color.Gray,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 12.sp,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }
            }
        }
    }
}

// Lifecycle

class EmulatorActivity : ComponentActivity() {
    override fun onCreate(s: Bundle?) {
        super.onCreate(s)
        setContent {
            EmulatorScreen()
        }
    }
}

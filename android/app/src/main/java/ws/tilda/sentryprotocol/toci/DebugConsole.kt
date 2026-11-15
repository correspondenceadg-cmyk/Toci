package ws.tilda.sentryprotocol.toci

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll

// Menu

@Composable
fun DebugConsole(onClose: () -> Unit, onSetStatus: (WipeStatus) -> Unit) {
    var showEmulator by remember { mutableStateOf(false) }

    if (showEmulator) {
        EmulatorTicker { showEmulator = false }
        return
    }
    AlertDialog(
        onDismissRequest = onClose,
        title = { Text("Debug Console") },
        text = {
            Column {
                Text("Wipe States", style = MaterialTheme.typography.titleMedium)
                Button(
                    onClick = { 
                        val context = androidx.compose.ui.platform.LocalContext.current
                        context.startActivity(android.content.Intent(context, EmulatorActivity::class.java))
                        onClose()
                    },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                ) { Text("Launch Companion Emulator") }
                Spacer(Modifier.height(8.dp))
                Button(
                    onClick = { 
                        val context = androidx.compose.ui.platform.LocalContext.current
                        context.sendBroadcast(android.content.Intent("ws.tilda.sentryprotocol.toci.PANIC").setPackage(context.packageName))
                        onClose()
                    },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE53935))
                ) { Text("Fire Panic Broadcast") }

                Button(
                    onClick = { 
                        val context = androidx.compose.ui.platform.LocalContext.current
                        val intent = android.content.Intent(context, ProgressScreen::class.java).apply {
                            putStringArrayListExtra("parts", ArrayList(listOf(
                                "/dev/block/by-name/userdata",
                                "/dev/block/by-name/cache"
                            )))
                        }
                        context.startActivity(intent)
                        onClose()
                    },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF22AA66))
                ) { Text("Start Real Wipe (Engine)") }

                Spacer(Modifier.height(8.dp))
                Text("UI States", style = MaterialTheme.typography.titleMedium)

                WipeStatus.values().forEach { status ->
                    Button(
                        onClick = { onSetStatus(status); onClose() },
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                    ) { Text(status.name) }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onClose) { Text("Close") }
        }
    )
}

package ws.tilda.sentryprotocol.toci

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
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

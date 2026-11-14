package ws.tilda.sentryprotocol.toci

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

// Menu

@Composable
fun DebugConsole(onClose: () -> Unit, onSetStatus: (WipeStatus) -> Unit) {
    AlertDialog(
        onDismissRequest = onClose,
        title = { Text("Debug Console") },
        text = {
            Column {
                Text("Wipe States", style = MaterialTheme.typography.titleMedium)
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

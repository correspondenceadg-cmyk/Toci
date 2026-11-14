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
@Composable
fun EmulatorTicker(onClose: () -> Unit) {
    val logs = remember { mutableStateListOf<String>() }
    val scrollState = rememberScrollState()

    LaunchedEffect(Unit) {
        val parts = listOf("userdata", "metadata", "cache", "persist", "modem")
        logs.add("Initializing Toci Engine...")
        delay(1000)
        for (p in parts) {
            logs.add("Opening /dev/block/by-name/$p...")
            delay(500)
            logs.add("Issuing BLKSECDISCARD on $p...")
            delay(800)
            logs.add("Verifying $p... PASS")
        }
        logs.add("RPMB Wipe... PASS")
        logs.add("Wipe complete. 0 failures.")
    }

    LaunchedEffect(logs.size) {
        scrollState.animateScrollTo(scrollState.maxValue)
    }

    AlertDialog(
        onDismissRequest = onClose,
        title = { Text("Level 2 Emulator") },
        text = {
            Column(modifier = Modifier.verticalScroll(scrollState)) {
                logs.forEach { Text(it, style = MaterialTheme.typography.bodySmall) }
            }
        },
        confirmButton = {
            TextButton(onClick = onClose) { Text("Close") }
        }
    )
}

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
                    onClick = { showEmulator = true },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                ) { Text("Run Level 2 Emulator") }
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

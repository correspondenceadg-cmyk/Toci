package ws.tilda.sentryprotocol.toci

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.runtime.DisposableEffect
import kotlinx.coroutines.delay

// State

enum class CeremonyStep { Warning, Passphrase, Waiting, Armed }

// Ceremony

@Composable
fun CeremonyFlow(onArmed: () -> Unit) {
    var step by remember { mutableStateOf(CeremonyStep.Warning) }
    var passphrase by remember { mutableStateOf("") }
    var hoursRemaining by remember { mutableStateOf(24) }
    val lifecycleOwner = LocalLifecycleOwner.current
    var backgroundTime by remember { mutableStateOf(0L) }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_STOP) {
                backgroundTime = System.currentTimeMillis()
            } else if (event == Lifecycle.Event.ON_START) {
                if (backgroundTime > 0 && System.currentTimeMillis() - backgroundTime > 30000) {
                    step = CeremonyStep.Warning
                    passphrase = ""
                    hoursRemaining = 24
                }
                backgroundTime = 0
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    TociTheme {
        Column(
            modifier = Modifier.fillMaxSize().padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            when (step) {
                CeremonyStep.Warning -> {
                    Text("DESTRUCTION MODE", style = MaterialTheme.typography.headlineLarge, color = MaterialTheme.colorScheme.primary)
                    Spacer(Modifier.height(16.dp))
                    Text("This will permanently degrade the device hardware and brick the bootloader. It cannot be undone.", style = MaterialTheme.typography.bodyLarge)
                    Spacer(Modifier.height(32.dp))
                    Button(onClick = { step = CeremonyStep.Passphrase }) { Text("I Understand") }
                }
                CeremonyStep.Passphrase -> {
                    Text("Set Destruction Passphrase", style = MaterialTheme.typography.headlineMedium)
                    Spacer(Modifier.height(16.dp))
                    OutlinedTextField(
                        value = passphrase,
                        onValueChange = { passphrase = it },
                        label = { Text("8-Word Passphrase") },
                        visualTransformation = PasswordVisualTransformation()
                    )
                    Spacer(Modifier.height(32.dp))
                    Button(
                        onClick = { step = CeremonyStep.Waiting },
                        enabled = passphrase.length >= 8
                    ) { Text("Continue") }
                }
                CeremonyStep.Waiting -> {
                    Text("Ceremony Cooldown", style = MaterialTheme.typography.headlineMedium)
                    Text("Destruction Mode will arm in $hoursRemaining hours.", style = MaterialTheme.typography.bodyLarge)
                    Spacer(Modifier.height(16.dp))
                    Text("You must leave the app open and the phone on a charger.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary)
                    Spacer(Modifier.height(32.dp))
                    if (hoursRemaining == 24) {
                        Button(onClick = { hoursRemaining = 0 }) { Text("[Dev Skip 24h]") }
                    }
                    Button(
                        onClick = {
                            onArmed()
                            step = CeremonyStep.Armed
                        },
                        enabled = hoursRemaining == 0
                    ) { Text("Arm Destruction") }
                }
                CeremonyStep.Armed -> {
                    Text("ARMED", style = MaterialTheme.typography.headlineLarge, color = MaterialTheme.colorScheme.primary)
                    Text("Destruction Mode is live. Trigger the four-factor sequence to initiate.", style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}

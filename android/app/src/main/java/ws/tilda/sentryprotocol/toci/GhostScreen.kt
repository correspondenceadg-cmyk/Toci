package ws.tilda.sentryprotocol.toci

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext

// State

enum class GhostLayer { Clean, Real }

// Setup

@Composable
fun GhostSetup(onComplete: () -> Unit) {
    var cleanPin by remember { mutableStateOf("") }
    var realPin by remember { mutableStateOf("") }
    val context = LocalContext.current

    TociTheme {
        Column(modifier = Modifier.fillMaxSize().padding(24.dp), verticalArrangement = Arrangement.Center) {
            Text("The Ghost Setup", style = MaterialTheme.typography.headlineMedium, color = Color.White)
            Spacer(Modifier.height(16.dp))
            Text("The clean layer is a plausible decoy. The real layer contains your sensitive data.", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
            Spacer(Modifier.height(24.dp))
            OutlinedTextField(
                value = cleanPin, onValueChange = { cleanPin = it },
                label = { Text("Clean Layer PIN") },
                visualTransformation = PasswordVisualTransformation()
            )
            Spacer(Modifier.height(16.dp))
            OutlinedTextField(
                value = realPin, onValueChange = { realPin = it },
                label = { Text("Real Layer PIN") },
                visualTransformation = PasswordVisualTransformation()
            )
            Spacer(Modifier.height(32.dp))
            Button(
                onClick = {
                    GhostPrefs.setCredentials(context, cleanPin, realPin)
                    GhostPrefs.seedPlausibleHistory(context)
                    onComplete()
                },
                enabled = cleanPin.length >= 4 && realPin.length >= 4 && cleanPin != realPin
            ) { Text("Initialize Ghost", fontFamily = coralinesCat) }
        }
    }
}

// Unlock

@Composable
fun GhostUnlock(onUnlock: (GhostLayer) -> Unit) {
    var pin by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current

    TociTheme {
        Column(modifier = Modifier.fillMaxSize().padding(24.dp), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Enter Passcode", style = MaterialTheme.typography.headlineMedium, color = Color.White)
            Spacer(Modifier.height(16.dp))
            OutlinedTextField(
                value = pin, onValueChange = { pin = it; error = null },
                label = { Text("Passcode") },
                visualTransformation = PasswordVisualTransformation()
            )
            if (error != null) {
                Spacer(Modifier.height(8.dp))
                Text(error!!, color = Color.Red, style = MaterialTheme.typography.bodySmall)
            }
            Spacer(Modifier.height(32.dp))
            Button(
                onClick = {
                    val layer = GhostPrefs.checkCredential(context, pin)
                    if (layer == "clean") onUnlock(GhostLayer.Clean)
                    else if (layer == "real") onUnlock(GhostLayer.Real)
                    else error = "Incorrect passcode"
                },
                enabled = pin.isNotEmpty()
            ) { Text("Unlock", fontFamily = coralinesCat) }
        }
    }
}

// Home

@Composable
fun GhostHome(layer: GhostLayer, onPanic: () -> Unit) {
    TociTheme {
        Column(modifier = Modifier.fillMaxSize().padding(24.dp), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
            when (layer) {
                GhostLayer.Clean -> {
                    Text("Weather", style = MaterialTheme.typography.headlineLarge, color = Color.White)
                    Spacer(Modifier.height(16.dp))
                    Text("72°F and Sunny", style = MaterialTheme.typography.bodyLarge, color = Color.White)
                    Spacer(Modifier.height(32.dp))
                    Text("Notes", style = MaterialTheme.typography.headlineSmall, color = Color.White)
                    Text("Buy milk, eggs, and bread.", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
                    Text("Dentist appointment Thursday at 2 PM.", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
                }
                GhostLayer.Real -> {
                    Text("Real Layer", style = MaterialTheme.typography.headlineLarge, color = Color(0xFFE53935))
                    Spacer(Modifier.height(16.dp))
                    Text("Access to Toci and sensitive apps.", style = MaterialTheme.typography.bodyLarge, color = Color.White)
                    Spacer(Modifier.height(32.dp))
                    Button(
                        onClick = onPanic,
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                    ) { Text("Trigger Ghost Panic", fontFamily = coralinesCat) }
                }
            }
        }
    }
}

package ws.tilda.sentryprotocol.toci

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp

// State

enum class OnboardingStep { Welcome, Duress, Trigger, Confirm }

// Theme

@Composable
fun TociTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = darkColorScheme(
            primary = Color(0xFFE53935),
            background = Color.Black,
            surface = Color(0xFF121212)
        ),
        content = content
    )
}

// Onboarding

@Composable
fun OnboardingFlow(onComplete: (String) -> Unit) {
    var step by remember { mutableStateOf(OnboardingStep.Welcome) }
    var duressPin by remember { mutableStateOf("") }
    var triggerConfirmed by remember { mutableStateOf(false) }

    Surface(color = MaterialTheme.colorScheme.background, modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.padding(24.dp).fillMaxSize()) {
            when (step) {
                OnboardingStep.Welcome -> {
                    Text("Toci", style = MaterialTheme.typography.headlineLarge, color = MaterialTheme.colorScheme.primary)
                    Spacer(Modifier.height(16.dp))
                    Text("Forensic-grade total device wipe.", style = MaterialTheme.typography.bodyLarge)
                    Spacer(Modifier.height(32.dp))
                    Button(onClick = { step = OnboardingStep.Duress }) { Text("Begin Setup") }
                }
                OnboardingStep.Duress -> {
                    Text("Duress Code", style = MaterialTheme.typography.headlineMedium)
                    Text("Enter a PIN. This triggers the wipe if you are forced to unlock your phone under duress.", style = MaterialTheme.typography.bodyMedium)
                    Spacer(Modifier.height(16.dp))
                    OutlinedTextField(
                        value = duressPin,
                        onValueChange = { duressPin = it },
                        label = { Text("Duress PIN") },
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword)
                    )
                    Spacer(Modifier.height(32.dp))
                    Button(
                        onClick = { step = OnboardingStep.Trigger },
                        enabled = duressPin.length >= 4
                    ) { Text("Next") }
                }
                OnboardingStep.Trigger -> {
                    Text("Trigger Configuration", style = MaterialTheme.typography.headlineMedium)
                    Text("By default, holding Vol-Up + Vol-Down + Power for 5 seconds triggers the wipe.", style = MaterialTheme.typography.bodyMedium)
                    Spacer(Modifier.height(32.dp))
                    Button(onClick = { step = OnboardingStep.Confirm }) { Text("Understood") }
                }
                OnboardingStep.Confirm -> {
                    Text("Confirm", style = MaterialTheme.typography.headlineMedium)
                    Spacer(Modifier.height(16.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = triggerConfirmed,
                            onCheckedChange = { triggerConfirmed = it }
                        )
                        Text("I understand this wipes all data immediately and cannot be undone.")
                    }
                    Spacer(Modifier.height(32.dp))
                    Button(
                        onClick = { onComplete(duressPin) },
                        enabled = triggerConfirmed
                    ) { Text("Arm Toci") }
                }
            }
        }
    }
}

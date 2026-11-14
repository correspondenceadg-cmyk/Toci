package ws.tilda.sentryprotocol.toci

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

// State

enum class OnboardingStep { Welcome, Duress, Trigger, Confirm }

// Fonts

val coralinesCat = FontFamily(Font(R.font.coralines_cat))
val misterBrush = FontFamily(Font(R.font.misterbrush))
val catgirlSystem = FontFamily(Font(R.font.catgirl_system))

// Theme

@Composable
fun TociTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = darkColorScheme(
            primary = Color(0xFFE53935),
            background = Color.Black,
            surface = Color(0xFF121212)
        ),
        typography = Typography(
            headlineLarge = TextStyle(fontFamily = coralinesCat),
            headlineMedium = TextStyle(fontFamily = coralinesCat),
            headlineSmall = TextStyle(fontFamily = coralinesCat),
            bodyLarge = TextStyle(fontFamily = misterBrush),
            bodyMedium = TextStyle(fontFamily = misterBrush),
            bodySmall = TextStyle(fontFamily = misterBrush)
        ),
        content = content
    )
}

// Splash

@Composable
fun SplashScreen(onTimeout: () -> Unit) {
    var visible by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        visible = true
        delay(2000)
        visible = false
        delay(500)
        onTimeout()
    }

    val alphaValue by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(durationMillis = 1000),
        label = "splash_alpha"
    )

    Box(
        modifier = Modifier.fillMaxSize().background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "T",
            style = TextStyle(
                fontFamily = catgirlSystem,
                fontSize = 120.sp,
                color = Color(0xFFE53935)
            ),
            modifier = Modifier.alpha(alphaValue)
        )
    }
}

// Onboarding

@Composable
fun OnboardingFlow(onComplete: (String) -> Unit) {
    var step by remember { mutableStateOf(OnboardingStep.Welcome) }
    var duressPin by remember { mutableStateOf("") }
    var triggerConfirmed by remember { mutableStateOf(false) }
    var tapCount by remember { mutableStateOf(0) }
    var showDebug by remember { mutableStateOf(false) }
    var showSplash by remember { mutableStateOf(true) }

    if (showSplash) {
        SplashScreen { showSplash = false }
        return
    }

    Surface(color = MaterialTheme.colorScheme.background, modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.padding(24.dp).fillMaxSize()) {
            when (step) {
                OnboardingStep.Welcome -> {
                    Text(
                        "Toci",
                        style = MaterialTheme.typography.headlineLarge,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.pointerInput(Unit) {
                            detectTapGestures(onTap = {
                                tapCount++
                                if (tapCount >= 5) { showDebug = true; tapCount = 0 }
                            })
                        }
                    )
                    Spacer(Modifier.height(16.dp))
                    Text("Forensic-grade total device wipe.", style = MaterialTheme.typography.bodyLarge)
                    Spacer(Modifier.height(32.dp))
                    Button(onClick = { step = OnboardingStep.Duress }) { Text("Begin Setup", fontFamily = coralinesCat) }
                }
                OnboardingStep.Duress -> {
                    Text("Duress Code", style = MaterialTheme.typography.headlineMedium)
                    Text("Enter a PIN. This triggers the wipe if you are forced to unlock your phone under duress.", style = MaterialTheme.typography.bodyMedium)
                    Spacer(Modifier.height(16.dp))
                    OutlinedTextField(
                        value = duressPin,
                        onValueChange = { duressPin = it },
                        label = { Text("Duress PIN") },
                        textStyle = TextStyle(fontFamily = coralinesCat),
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword)
                    )
                    Spacer(Modifier.height(32.dp))
                    Button(
                        onClick = { step = OnboardingStep.Trigger },
                        enabled = duressPin.length >= 4
                    ) { Text("Next", fontFamily = coralinesCat) }
                }
                OnboardingStep.Trigger -> {
                    Text("Trigger Configuration", style = MaterialTheme.typography.headlineMedium)
                    Text("By default, holding Vol-Up + Vol-Down + Power for 5 seconds triggers the wipe.", style = MaterialTheme.typography.bodyMedium)
                    Spacer(Modifier.height(32.dp))
                    Button(onClick = { step = OnboardingStep.Confirm }) { Text("Understood", fontFamily = coralinesCat) }
                }
                OnboardingStep.Confirm -> {
                    Text("Confirm", style = MaterialTheme.typography.headlineMedium)
                    Spacer(Modifier.height(16.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = triggerConfirmed,
                            onCheckedChange = { triggerConfirmed = it }
                        )
                        Text("I understand this wipes all data immediately and cannot be undone.", style = MaterialTheme.typography.bodyMedium)
                    }
                    Spacer(Modifier.height(32.dp))
                    Button(
                        onClick = { onComplete(duressPin) },
                        enabled = triggerConfirmed
                    ) { Text("Arm Toci", fontFamily = coralinesCat) }
                }
            }
            if (showDebug) {
                DebugConsole(onClose = { showDebug = false }, onSetStatus = { /* Route to ProgressScreen */ })
            }
        }
    }
}

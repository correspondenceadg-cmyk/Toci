package ws.tilda.sentryprotocol.toci

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*

// Lifecycle

class GhostActivity : ComponentActivity() {
    override fun onCreate(s: Bundle?) {
        super.onCreate(s)
        // App Switcher Hygiene: Prevents screenshot/thumbnail in recent apps
        window.addFlags(WindowManager.LayoutParams.FLAG_SECURE)

        setContent {
            var configured by remember { mutableStateOf(GhostPrefs.isConfigured(this)) }
            var unlocked by remember { mutableStateOf(false) }
            var layer by remember { mutableStateOf(GhostLayer.Clean) }
            var panicFallback by remember { mutableStateOf(false) }

            if (!configured) {
                GhostSetup { configured = true }
            } else if (!unlocked) {
                GhostUnlock { 
                    layer = it
                    unlocked = true 
                }
            } else {
                if (panicFallback) {
                    TociTheme {
                        Column(
                            modifier = androidx.compose.ui.Modifier.fillMaxSize().padding(24.dp),
                            verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center,
                            horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
                        ) {
                            androidx.compose.material3.Text(
                                "Password Expired", 
                                color = androidx.compose.ui.graphics.Color.Red, 
                                style = androidx.compose.material3.MaterialTheme.typography.headlineMedium
                            )
                            androidx.compose.foundation.layout.Spacer(modifier = androidx.compose.ui.Modifier.height(16.dp))
                            androidx.compose.material3.Text(
                                "Your session has expired or the password was changed. Please unlock using your current PIN.", 
                                color = androidx.compose.ui.graphics.Color.Gray,
                                style = androidx.compose.material3.MaterialTheme.typography.bodyMedium
                            )
                            androidx.compose.foundation.layout.Spacer(modifier = androidx.compose.ui.Modifier.height(32.dp))
                            androidx.compose.material3.Button(
                                onClick = { 
                                    unlocked = false 
                                    panicFallback = false 
                                }
                            ) {
                                androidx.compose.material3.Text("Try Again", fontFamily = coralinesCat)
                            }
                        }
                    }
                } else {
                    GhostHome(layer = layer, onPanic = {
                        GhostPrefs.wipeRealLayer(this)
                        panicFallback = true
                    })
                }
            }
        }
    }
}

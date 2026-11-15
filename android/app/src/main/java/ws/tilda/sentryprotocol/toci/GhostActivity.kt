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
                        Column(modifier = androidx.compose.ui.Modifier.fillMaxSize().padding(24.dp), verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center) {
                            androidx.compose.material3.Text("Error", color = androidx.compose.ui.graphics.Color.Red, style = androidx.compose.material3.MaterialTheme.typography.headlineMedium)
                            androidx.compose.material3.Text("The real layer has been locked or cleared. Please use the clean layer.", color = androidx.compose.ui.graphics.Color.White)
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

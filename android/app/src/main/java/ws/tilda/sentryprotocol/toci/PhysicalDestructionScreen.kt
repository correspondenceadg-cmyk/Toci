package ws.tilda.sentryprotocol.toci

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

// D4 Guide

@Composable
fun PhysicalDestructionScreen() {
    var step by remember { mutableStateOf(1) }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("FINAL STEP", style = MaterialTheme.typography.headlineLarge, color = Color(0xFFE53935))
        Spacer(Modifier.height(16.dp))
        
        when (step) {
            1 -> {
                Text("The software has done all it can. The device is degraded and bricked. For absolute, unconditional erasure, physical destruction is required.", style = MaterialTheme.typography.bodyLarge)
                Spacer(Modifier.height(32.dp))
                Button(onClick = { step = 2 }) { Text("I have a drill", fontFamily = coralinesCat) }
            }
            2 -> {
                Text("Step 1: Power off the device entirely.", style = MaterialTheme.typography.bodyMedium)
                Spacer(Modifier.height(32.dp))
                Button(onClick = { step = 3 }) { Text("Done", fontFamily = coralinesCat) }
            }
            3 -> {
                Text("Step 2: Locate the storage IC on the motherboard. It is usually a small square chip near the CPU.", style = MaterialTheme.typography.bodyMedium)
                Spacer(Modifier.height(32.dp))
                Button(onClick = { step = 4 }) { Text("Done", fontFamily = coralinesCat) }
            }
            4 -> {
                Text("Step 3: Drill through the die of the storage IC. Use a 3-4mm bit. Two holes, offset.", style = MaterialTheme.typography.bodyMedium)
                Spacer(Modifier.height(32.dp))
                Button(onClick = { step = 5 }) { Text("Done", fontFamily = coralinesCat) }
            }
            5 -> {
                Text("The king is dead. Long live the king.", style = MaterialTheme.typography.headlineLarge, color = Color.DarkGray)
            }
        }
    }
}

package ws.tilda.sentryprotocol.toci

import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager

// Component

object GhostManager {
    // toggleTociIcon(context, false) -> Toci launcher icon hidden
    fun toggleTociIcon(context: Context, visible: Boolean) {
        val component = ComponentName(context, MainActivity::class.java)
        val newState = if (visible) {
            PackageManager.COMPONENT_ENABLED_STATE_ENABLED
        } else {
            PackageManager.COMPONENT_ENABLED_STATE_DISABLED
        }
        context.packageManager.setComponentEnabledSetting(
            component,
            newState,
            PackageManager.DONT_KILL_APP
        )
    }
}

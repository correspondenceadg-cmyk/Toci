package ws.tilda.sentryprotocol.toci

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import java.security.MessageDigest

// Persistence

object GhostPrefs {
    private fun getPrefs(context: Context): SharedPreferences {
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()
        return EncryptedSharedPreferences.create(
            context,
            "toci_ghost_prefs",
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    private fun hash(pin: String): String {
        return MessageDigest.getInstance("SHA-256")
            .digest(pin.toByteArray())
            .joinToString("") { "%02x".format(it) }
    }

    fun isConfigured(context: Context): Boolean {
        return getPrefs(context).getBoolean("configured", false)
    }

    fun setCredentials(context: Context, cleanPin: String, realPin: String) {
        getPrefs(context).edit()
            .putString("clean_hash", hash(cleanPin))
            .putString("real_hash", hash(realPin))
            .putBoolean("configured", true)
            .apply()
    }

    fun checkCredential(context: Context, pin: String): String? {
        val h = hash(pin)
        val prefs = getPrefs(context)
        return when (h) {
            prefs.getString("clean_hash", "") -> "clean"
            prefs.getString("real_hash", "") -> "real"
            else -> null
        }
    }

    fun wipeRealLayer(context: Context) {
        getPrefs(context).edit().remove("real_hash").apply()
    }

    fun seedPlausibleHistory(context: Context) {
        getPrefs(context).edit()
            .putString("clean_note_1", "Buy milk, eggs, and bread.")
            .putString("clean_note_2", "Dentist appointment Thursday at 2 PM.")
            .putString("clean_weather", "72°F and Sunny")
            .apply()
    }
}

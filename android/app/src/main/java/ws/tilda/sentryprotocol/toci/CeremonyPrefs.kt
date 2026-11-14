package ws.tilda.sentryprotocol.toci

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

// Persistence

object CeremonyPrefs {
    private fun getPrefs(context: Context): SharedPreferences {
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()
        return EncryptedSharedPreferences.create(
            context,
            "toci_ceremony_prefs",
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    fun isArmed(context: Context): Boolean {
        return getPrefs(context).getBoolean("armed", false)
    }

    fun getStartTime(context: Context): Long {
        return getPrefs(context).getLong("start_time", 0L)
    }

    fun setStartTime(context: Context, time: Long) {
        getPrefs(context).edit().putLong("start_time", time).apply()
    }

    fun arm(context: Context) {
        getPrefs(context).edit().putBoolean("armed", true).apply()
    }

    fun reset(context: Context) {
        getPrefs(context).edit().clear().apply()
    }
}

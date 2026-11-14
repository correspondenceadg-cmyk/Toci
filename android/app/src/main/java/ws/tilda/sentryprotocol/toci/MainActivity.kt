package ws.tilda.sentryprotocol.toci

import android.content.Intent
import android.os.Bundle
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.platform.ComposeView
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import java.security.KeyStore
import java.security.MessageDigest
import javax.crypto.KeyGenerator

class MainActivity : AppCompatActivity() {

    private val parts = listOf(
        "/dev/block/by-name/userdata",
        "/dev/block/by-name/metadata",
        "/dev/block/by-name/cache",
        "/dev/block/by-name/persist",
        "/dev/block/by-name/modem",
        "/dev/block/by-name/boot_a",
        "/dev/block/by-name/boot_b",
    )

    // Lifecycle

    override fun onCreate(s: Bundle?) {
        super.onCreate(s)
        ensureDuressKey()
        registerPanicReceiver(this)

        if (intent.getBooleanExtra("fire", false)) {
            fireWipe()
            return
        }

        if (intent.getBooleanExtra("ceremony", false)) {
            val view = ComposeView(this).apply {
                setContent {
                    CeremonyFlow { /* Handle armed state persistence here */ }
                }
            }
            setContentView(view)
            return
        }

        val prefs = getEncryptedPrefs()
        if (prefs.contains("duress_hash")) {
            // Already onboarded, skip to trigger for testing
            fireWipe()
            return
        }

        val view = ComposeView(this).apply {
            setContent {
                TociTheme {
                    OnboardingFlow { duressPin ->
                        saveDuressHash(duressPin)
                        fireWipe()
                    }
                }
            }
        }
        setContentView(view)
    }

    // Keys

    private fun ensureDuressKey() {
        val ks = KeyStore.getInstance("AndroidKeyStore").apply { load(null) }
        if (ks.containsAlias("toci_duress_key")) return
        KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore")
            .apply {
                init(KeyGenParameterSpec.Builder(
                    "toci_duress_key",
                    KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                    .setUserAuthenticationRequired(false)
                    .build())
            }
            .generateKey()
    }

    private fun getEncryptedPrefs(): android.content.SharedPreferences {
        val masterKey = MasterKey.Builder(this)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()
        return EncryptedSharedPreferences.create(
            this,
            "toci_secure_prefs",
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    // State

    private fun saveDuressHash(pin: String) {
        val hash = MessageDigest.getInstance("SHA-256")
            .digest(pin.toByteArray())
            .joinToString("") { "%02x".format(it) }
        getEncryptedPrefs().edit().putString("duress_hash", hash).apply()
    }

    // Trigger

    private fun fireWipe() {
        startActivity(
            Intent(this, ProgressScreen::class.java)
                .putStringArrayListExtra("parts", ArrayList(parts))
        )
        finish()
    }
}

package ws.tilda.sentryprotocol.toci

import android.content.Intent
import android.os.Bundle
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import androidx.appcompat.app.AppCompatActivity
import java.security.KeyStore
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
        if (intent.getBooleanExtra("fire", false)) fireWipe()
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

    // Trigger

    private fun fireWipe() {
        startActivity(
            Intent(this, ProgressScreen::class.java)
                .putStringArrayListExtra("parts", ArrayList(parts))
        )
        finish()
    }
}

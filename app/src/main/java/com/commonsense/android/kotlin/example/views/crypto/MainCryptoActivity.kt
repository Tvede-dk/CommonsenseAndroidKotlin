package com.commonsense.android.kotlin.example.views.crypto

import com.commonsense.android.kotlin.example.databinding.CryptoActivityBinding
import com.commonsense.android.kotlin.system.crypto.Crypto
import com.commonsense.android.kotlin.views.databinding.activities.BaseDatabindingActivity
import com.commonsense.android.kotlin.views.databinding.activities.InflaterFunctionFull
import com.commonsense.android.kotlin.views.extensions.setOnclickAsync

/**
 * Created by Kasper Tvede on 10-04-2018.
 * Purpose:
 *
 */
class MainCryptoActivity : BaseDatabindingActivity<CryptoActivityBinding>() {
    override fun createBinding(): InflaterFunctionFull<CryptoActivityBinding> =
            CryptoActivityBinding::inflate


    override fun useBinding() {
        binding.cryptoActivityTryit.setOnclickAsync {
            val input = binding.cryptoActivityInput.text.toString()

            val crypto = Crypto.SymmetricWithAuthentication.createMostSecure()
            if (crypto == null) {

                binding.cryptoActivityEncrypted.text = "could not create cipher."
                return@setOnclickAsync
            }

            val encrypted = crypto.encrypt(input.toByteArray(Charsets.UTF_8))
            binding.cryptoActivityEncrypted.text = encrypted?.cipherText?.toHexString() ?: "no encryption happened"
            if (encrypted != null) {
                val decrypted = crypto.decrypt(encrypted)
                binding.cryptoActivityDecrypted.text = decrypted?.toString(Charsets.UTF_8) ?: "no decryption worked"

            } else {
                binding.cryptoActivityDecrypted.text = "encrypted was null"
            }
        }

    }

}


private fun ByteArray.toHexString(): String {
    val result = StringBuilder(this.size * 2)
    for (b in this) {
        result.append(String.format("%02X", b))
    }
    return result.toString()
}
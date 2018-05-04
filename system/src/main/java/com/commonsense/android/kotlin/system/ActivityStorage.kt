package com.commonsense.android.kotlin.system

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.support.annotation.VisibleForTesting
import android.util.Base64
import com.commonsense.android.kotlin.base.FunctionResult
import com.commonsense.android.kotlin.base.FunctionUnit
import java.io.*
import javax.crypto.KeyGenerator

/**
 * Created by Kasper Tvede on 23-03-2018.
 * Purpose:
 * handles the storing of runtime callbacks and other things, such that onStop saves / persits the callbacks.
 * see the andorid documentation on "onStop" and the storing of information in both bundles ( dicouraged) and
 * empty processes on android.
 */
class ActivityStorage<T : Activity>(context: Context,
                                    private val storage: DeviceSettings) {

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    internal var secretKey: String? = null

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    internal val keySizeInBits: Int = 256

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    internal val secretKeyIndex: String = "private-random-key"


    fun <U> storeCallback(callback: FunctionResult<T, U>, name: String) {
//        val codeInBase64 = callback.serialize()
//        storage.saveSetting(name, codeInBase64)
    }

    fun storeEmptyCallback(callback: FunctionUnit<T>, name: String) {
        val codeInBase64 = callback.serializeEmptyLambda()
        storage.saveSetting(codeInBase64, name)
    }

    fun remove(name: String) {

    }

    fun <U> restoreCallback(name: String): FunctionResult<T, U>? {
        return null
    }

    fun restoreEmptyCallback(name: String): FunctionUnit<T>? {
        val loaded = storage.loadSetting(name, null) ?: return null
        return deserializeEmptyLambda(loaded)
    }

    /**
     * This needs to be called, to store the private key.
     */
    fun onSaveInstanceState(bundle: Bundle) {
        bundle.putString(secretKeyIndex, secretKey)
    }

    fun onCreate(savedInstanceState: Bundle?) {
        secretKey = savedInstanceState?.getString(secretKeyIndex)
                ?: createKey() //to make sure that we initilize secretkey even iff the bundle is null at the key.
    }


    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    @Throws
    internal fun createKey(): String {
        val keyGen = KeyGenerator.getInstance("AES")
        keyGen.init(keySizeInBits)
        return Base64.encodeToString(keyGen.generateKey().encoded, 0)
    }

    fun onStop(isFinishing: Boolean) {
        if (isFinishing) {

        } else {

        }
    }

    fun loadDataAndDelete(index: String): String? {
        val data = storage.loadSetting(index, null)
        storage.removeSetting(index)
        return data
    }


    fun saveData(index: String, data: String) {
        storage.saveSetting(index, data)
    }

    internal fun decryptData(encoded: String): String? {
        return ""
    }

    internal fun encryptData(data: String) : ByteArray {
        return ByteArray(0)
    }


}

private fun Any.serializeEmptyLambda(): String {
    val out = ByteArrayOutputStream()
    val outputStream = ObjectOutputStream(out)
    outputStream.writeObject(this)
    outputStream.close()
    return Base64.encode(out.toByteArray(), 0).toString(Charsets.UTF_8)
}

private fun <T> deserializeEmptyLambda(base64String: String): T? {
    return ObjectInputStream(BufferedInputStream(ByteArrayInputStream(Base64.decode(base64String, 0)))).use {
        @Suppress("UNCHECKED_CAST")
        it.readObject() as? T
    }
}
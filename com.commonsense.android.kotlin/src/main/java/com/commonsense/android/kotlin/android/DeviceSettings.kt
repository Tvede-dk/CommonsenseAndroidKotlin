package com.commonsense.android.kotlin.android

import android.content.Context
import android.content.SharedPreferences
import com.commonsense.android.kotlin.android.extensions.editWith

/**
 * created by Kasper Tvede on 29-10-2016.
 */


fun Context.createDeviceSettings(): DeviceSettings {
    return DeviceSettings(this)
}


class DeviceSettings(val context: Context) {

    val sharedPrefs: SharedPreferences by lazy {
        context.getSharedPreferences("DeviceSettings", Context.MODE_PRIVATE)
    }

    //<editor-fold desc="save functions">
    fun saveSetting(value: String, key: String) = sharedPrefs.editWith { putString(key, value) }

    fun saveSetting(value: Int, key: String) = sharedPrefs.editWith { putInt(key, value) }

    fun saveSetting(value: Float, key: String) = sharedPrefs.editWith { putFloat(key, value) }

    fun saveSetting(value: Boolean, key: String) = sharedPrefs.editWith { putBoolean(key, value) }

    fun saveSetting(value: Long, key: String) = sharedPrefs.editWith { putLong(key, value) }


    inline fun <U> saveSetting(item: U, crossinline converter: (U) -> String?, key: String) = sharedPrefs.editWith { putString(key, converter(item)) }
    //</editor-fold>

    //<editor-fold desc="load functions">
    fun loadSetting(key: String, default: String? = null): String? = sharedPrefs.getString(key, default)

    fun loadSetting(key: String, default: Int = 0) = sharedPrefs.getInt(key, default)

    fun loadSetting(key: String, default: Float = 0f) = sharedPrefs.getFloat(key, default)

    fun loadSetting(key: String, default: Boolean = false) = sharedPrefs.getBoolean(key, default)

    fun loadSetting(key: String, default: Long = 0) = sharedPrefs.getLong(key, default)

    inline fun <U> loadSetting(key: String, crossinline converter: (String?) -> U?, default: U?) = sharedPrefs.getString(key, null)?.let { converter(it) } ?: default

    fun haveSetting(key: String): Boolean = sharedPrefs.contains(key)

    fun removeSetting(key: String) = sharedPrefs.editWith { remove(key) }
    //</editor-fold>


}

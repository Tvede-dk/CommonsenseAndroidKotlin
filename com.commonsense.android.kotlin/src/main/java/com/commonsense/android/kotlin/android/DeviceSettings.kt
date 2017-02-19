package com.commonsense.android.kotlin.android

import android.content.Context
import android.content.SharedPreferences

/**
 * Created by admin on 29-10-2016.
 */


fun Context.createDeviceSettings(): DeviceSettings {
    return DeviceSettings(this)
}


class DeviceSettings(val context: Context) {

    val sharedPrefs: SharedPreferences by lazy {
        context.getSharedPreferences("DeviceSettings", Context.MODE_PRIVATE)
    }

    //<editor-fold desc="save functions">
    fun saveDeviceSetting(value: String, key: String) = sharedPrefs.edit().putString(key, value).apply()

    fun saveDeviceSetting(value: Int, key: String) = sharedPrefs.edit().putInt(key, value).apply()

    fun saveDeviceSetting(value: Float, key: String) = sharedPrefs.edit().putFloat(key, value).apply()

    fun saveDeviceSetting(value: Boolean, key: String) = sharedPrefs.edit().putBoolean(key, value).apply()

    fun saveDeviceSetting(value: Long, key: String) = sharedPrefs.edit().putLong(key, value).apply()


    inline fun <U> saveDeviceSetting(item: U,crossinline converter: (U) -> String?, key: String) = sharedPrefs.edit().putString(key, converter(item)).apply()
    //</editor-fold>

    //<editor-fold desc="load functions">
    fun loadDeviceSetting(key: String, default: String? = null): String? = sharedPrefs.getString(key, default)

    fun loadDeviceSetting(key: String, default: Int = 0) = sharedPrefs.getInt(key, default)

    fun loadDeviceSetting(key: String, default: Float = 0f) = sharedPrefs.getFloat(key, default)

    fun loadDeviceSetting(key: String, default: Boolean = false) = sharedPrefs.getBoolean(key, default)

    fun loadDeviceSetting(key: String, default: Long = 0) = sharedPrefs.getLong(key, default)

    inline fun <U> loadDeviceSetting(key: String,crossinline converter: (String?) -> U?, default: U?) = sharedPrefs.getString(key, null)?.let { converter(it) } ?: default

    fun haveSetting(key: String): Boolean = sharedPrefs.contains(key)
    //</editor-fold>


}

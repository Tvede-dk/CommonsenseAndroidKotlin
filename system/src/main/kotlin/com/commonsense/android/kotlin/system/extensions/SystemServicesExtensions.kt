package com.commonsense.android.kotlin.system.extensions

import android.app.*
import android.app.job.*
import android.app.usage.*
import android.content.Context
import android.location.*
import android.net.*
import android.net.wifi.*
import android.net.wifi.aware.*
import android.net.wifi.p2p.*
import android.os.*
import android.os.Build.*
import android.support.annotation.*
import android.view.*
import android.view.inputmethod.InputMethodManager
import com.commonsense.android.kotlin.base.extensions.collections.*

/**
 * Created by Kasper Tvede
 *
 * see
 * https://developer.android.com/reference/android/content/Context#getSystemService(java.lang.Class%3CT%3E)
 * for list of services exposed as properties
 *
 */

/**
 * The top-level window manager in which you can place custom windows. The returned object is a WindowManager.
 */
inline val Context.windowManager: WindowManager?
    get() = getService(Context.WINDOW_SERVICE)

/**
 * A LayoutInflater for inflating layout resources in this context.
 */
inline val Context.layoutInflater: LayoutInflater?
    get() = getService(Context.LAYOUT_INFLATER_SERVICE)

/**
 * An ActivityManager for interacting with the global activity state of the system.
 */
inline val Context.activityManager: ActivityManager?
    get() = getService(Context.ACTIVITY_SERVICE)

/**
 * A PowerManager for controlling power management.
 */
inline val Context.powerManager: PowerManager?
    get() = getService(Context.POWER_SERVICE)

/**
 * A AlarmManager for receiving intents at the time of your choosing.
 */
inline val Context.alarmManager: AlarmManager?
    get() = getService(Context.ALARM_SERVICE)

/**
 * A NotificationManager for informing the user of background events.
 */
inline val Context.notificationManager: NotificationManager?
    get() = getService(Context.NOTIFICATION_SERVICE)


/**
 * A KeyguardManager for controlling keyguard.
 */
inline val Context.keyguardManager: KeyguardManager?
    get() = getService(Context.KEYGUARD_SERVICE)

/**
 * A LocationManager for controlling location (e.g., GPS) updates.
 */
inline val Context.locationManager: LocationManager?
    get() = getService(Context.LOCATION_SERVICE)

/**
 * A SearchManager for handling search.
 */
inline val Context.searchManager: SearchManager?
    get() = getService(Context.SEARCH_SERVICE)

/**
 * A Vibrator for interacting with the vibrator hardware.
 */
inline val Context.vibrator: Vibrator?
    get() = getService(Context.VIBRATOR_SERVICE)
/**
 * A ConnectivityManager for handling management of network connections.
 */
inline val Context.connectivityManager: ConnectivityManager?
    get() = getService(Context.CONNECTIVITY_SERVICE)

/**
 * A IpSecManager for managing IPSec on sockets and networks.
 * api 28 / P
 */
inline val Context.ipSecManager: IpSecManager?
    @RequiresApi(28)
    get() = getService(Context.IPSEC_SERVICE)

/**
 * A WifiManager for management of Wi-Fi connectivity.
 * (this is leak safe , since it handles the pre N case).
 * note regarding the issue with pre N:
 *  - in layman terms api level below N, should call it on the application context, otherwise the context.
 *  - ^ to avoid memory leaks.
 */
inline val Context.wifiManager: WifiManager?
    get() = isApiOverOrEqualTo(24)
            //if api >= 24, use the given context, otherwise use the applicationContext
            .map(this, applicationContext)
            ?.getService(Context.WIFI_SERVICE)

/**
 * A WifiAwareManager for management of Wi-Fi Aware discovery and connectivity.
 */
inline val Context.wifiAware: WifiAwareManager?
    @RequiresApi(26)
    get() = getService(Context.WIFI_AWARE_SERVICE)


/**
 * A WifiP2pManager for management of Wi-Fi Direct connectivity.
 */
inline val Context.wifiP2P: WifiP2pManager?
    get() = getService(Context.WIFI_P2P_SERVICE)

/**
 * An InputMethodManager for management of input methods.
 */
inline val Context.inputMethodManager: InputMethodManager?
    get() = getService(Context.INPUT_METHOD_SERVICE)

/**
 * An UiModeManager for controlling UI modes.
 * This class provides access to the system uimode services.
 * These services allow applications to control UI modes of the device.
 * It provides functionality to disable the car mode and it gives access to the night mode settings.
 */
inline val Context.uiModeManager: UiModeManager?
    get() = getService(Context.UI_MODE_SERVICE)


/**
 * A DownloadManager for requesting HTTP downloads
 */
inline val Context.downloadManager: DownloadManager?
    get() = getService(Context.DOWNLOAD_SERVICE)

/**
 * A BatteryManager for managing battery state
 */
inline val Context.batteryManager: BatteryManager?
    @RequiresApi(21)
    get() = getService(Context.BATTERY_SERVICE)

/**
 * A JobScheduler for managing scheduled tasks
 */
inline val Context.jobScheduler: JobScheduler?
    @RequiresApi(VERSION_CODES.LOLLIPOP)
    get() = getService(Context.JOB_SCHEDULER_SERVICE)


/**
 * A NetworkStatsManager for querying network usage statistics.
 */
inline val Context.networkStatsManager: NetworkStatsManager?
    @RequiresApi(VERSION_CODES.M)
    get() = getService(Context.NETWORK_STATS_SERVICE)


/**
 * A NetworkStatsManager for querying network usage statistics.
 */
inline val Context.hardwarePropertiesManager: HardwarePropertiesManager?
    @RequiresApi(VERSION_CODES.N)
    get() = getService(Context.HARDWARE_PROPERTIES_SERVICE)

//TODO inline class. its is more of a "simple" wrapper type to make it possible to write
// [in a context type] services.window

val Context.services: ContextServices
    get() = ContextServices(this)

class ContextServices(val context: Context) {
    /**
     * The top-level window manager in which you can place custom windows.
     */
    inline val window: WindowManager?
        get() = context.windowManager

    /**
     * A LayoutInflater for inflating layout resources in this context.
     */
    inline val inflater: LayoutInflater?
        get() = context.layoutInflater

    /**
     * A ActivityManager for interacting with the global activity state of the system.
     */
    inline val activity: ActivityManager?
        get() = context.activityManager

    /**
     * A PowerManager for controlling power management.
     */
    inline val power: PowerManager?
        get () = context.powerManager

    /**
     * A AlarmManager for receiving intents at the time of your choosing.
     */
    inline val alarm: AlarmManager?
        get() = context.alarmManager

    /**
     * A NotificationManager for informing the user of background events.
     */
    inline val notification: NotificationManager?
        get() = context.notificationManager

    /**
     * A KeyguardManager for controlling keyguard.
     */
    inline val keyguard: KeyguardManager?
        get() = context.keyguardManager

    /**
     * A LocationManager for controlling location (e.g., GPS) updates.
     */
    inline val location: LocationManager?
        get() = context.locationManager


    /**
     * A SearchManager for handling search.
     */
    inline val search: SearchManager?
        get() = context.searchManager

    /**
     * A Vibrator for interacting with the vibrator hardware.
     */
    inline val vibrator: Vibrator?
        get() = context.vibrator


    /**
     * A ConnectivityManager for handling management of network connections.
     */
    inline val connectivity: ConnectivityManager?
        get() = context.connectivityManager

    /**
     * A IpSecManager for managing IPSec on sockets and networks.
     */
    inline val ipSec: IpSecManager?
        @RequiresApi(28)
        get() = context.ipSecManager

    /**
     * A WifiManager for management of Wi-Fi connectivity.
     */
    inline val wifi: WifiManager?
        get() = context.wifiManager


    /**
     * A WifiAwareManager for management of Wi-Fi Aware discovery and connectivity.
     */
    inline val wifiAware: WifiAwareManager?
        @RequiresApi(26)
        get() = context.wifiAware

    /**
     * A WifiP2pManager for management of Wi-Fi Direct connectivity.
     */
    inline val wifiP2P: WifiP2pManager?
        get() = context.wifiP2P

    /**
     * An InputMethodManager for management of input methods.
     */
    inline val inputMethod: InputMethodManager?
        get() = context.inputMethodManager

    /**
     * An UiModeManager for controlling UI modes.
     * This class provides access to the system uimode services.
     * These services allow applications to control UI modes of the device.
     * It provides functionality to disable the car mode and it gives access to the night mode settings.
     */
    inline val uiMode: UiModeManager?
        get() = context.uiModeManager

    /**
     * A DownloadManager for requesting HTTP downloads
     */
    inline val download: DownloadManager?
        get() = context.downloadManager

    /**
     * A BatteryManager for managing battery state
     */
    inline val battery: BatteryManager?
        @RequiresApi(21)
        get() = context.batteryManager

    /**
     * A JobScheduler for managing scheduled tasks
     */
    inline val jobScheduler: JobScheduler?
        @RequiresApi(VERSION_CODES.LOLLIPOP)
        get() = context.jobScheduler

    /**
     * A NetworkStatsManager for querying network usage statistics.
     */
    inline val networkStats: NetworkStatsManager?
        @RequiresApi(VERSION_CODES.M)
        get() = context.networkStatsManager


    /**
     * A NetworkStatsManager for querying network usage statistics.
     */
    inline val hardwareProperties: HardwarePropertiesManager?
        @RequiresApi(VERSION_CODES.N)
        get() = context.hardwarePropertiesManager

}


inline val Context.defaultDisplay: Display?
    get() = windowManager?.defaultDisplay


@Suppress("NOTHING_TO_INLINE")
inline fun <reified T> Context.getService(name: String): T? {
    return if (isApiOverOrEqualTo(23)) {
        getSystemService(T::class.java)
    } else {
        getSystemService(name) as? T
    }
}

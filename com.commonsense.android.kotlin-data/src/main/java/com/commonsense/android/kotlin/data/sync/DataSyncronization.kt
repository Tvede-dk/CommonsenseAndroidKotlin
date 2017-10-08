package com.commonsense.android.kotlin.data.sync

/**
 * Created by Kasper Tvede on 08-10-2017.
 */


interface DataSyncable {
    fun getId(): String
    fun isLocallyModified(): Boolean

}

package com.commonsense.android.kotlin.android.dataMovement

import android.os.Bundle
import android.os.Parcelable
import onTrue

/**
 * Created by Kasper Tvede on 11-01-2017.
 */

const val INTENT_DATA_INDEX = "INTENT_DATA_INDEX"

inline fun <T : Parcelable> Bundle.UseIntentDataIfExists(crossinline action: (item: T) -> Unit) {
    containsKey(INTENT_DATA_INDEX).onTrue {
        getParcelable<T>(INTENT_DATA_INDEX)?.let { action(it) }
    }
}
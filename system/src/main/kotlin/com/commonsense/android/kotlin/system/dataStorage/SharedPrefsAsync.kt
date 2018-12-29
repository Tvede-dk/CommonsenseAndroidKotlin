package com.commonsense.android.kotlin.system.dataStorage

import android.content.*
import android.content.Context.*
import com.commonsense.android.kotlin.base.extensions.*
import com.commonsense.android.kotlin.base.extensions.coroutines.*
import kotlinx.coroutines.*
import kotlin.reflect.*


/**
 * A coroutine like wrapper over the standard shared preferences.
 * in future this will be a part of a larger setup in mpp and csense kotlin
 */
class SharedPrefsAsync(name: String, context: Context, val scope: CoroutineScope) {

}


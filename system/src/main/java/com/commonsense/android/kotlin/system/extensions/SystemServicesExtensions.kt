package com.commonsense.android.kotlin.system.extensions

import android.content.Context
import android.view.Display
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager

/**
 * Created by Kasper Tvede on 21-05-2017.
 */

inline val Context.inputMethodManager: InputMethodManager?
    get() {
        return if (isApiOverOrEqualTo(23)) {
            getSystemService(InputMethodManager::class.java)
        } else {
            getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        }
    }


inline val Context.windowManager: WindowManager?
    get() = getSystemService(Context.WINDOW_SERVICE) as? WindowManager


inline val Context.defaultDisplay: Display?
    get() = windowManager?.defaultDisplay

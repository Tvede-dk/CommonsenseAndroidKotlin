package com.commonsense.android.kotlin.views.extensions

import android.graphics.Canvas
import android.graphics.drawable.Drawable

/**
 * Created by Kasper Tvede on 26-08-2017.
 */
val Canvas.centerX: Float
    get() = width / 2f


val Canvas.centerY: Float
    get() = height / 2f


/**
 *  Does modify the drawable!!! (setBounds)
 */
fun Canvas.draw(drawable: Drawable, left: Int, top: Int, right: Int, bottom: Int) {
    drawable.setBounds(left, top, right, bottom)
    drawable.draw(this)
}

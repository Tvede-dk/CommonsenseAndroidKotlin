package com.commonsense.android.kotlin.views.extensions

import android.graphics.Canvas
import android.graphics.drawable.Drawable

/**
 * Calculates the center X value of this canvas
 */
val Canvas.centerX: Float
    get() = width / 2f

/**
 * Calculates the center Y value of this canvas
 */
val Canvas.centerY: Float
    get() = height / 2f



/**
 * Does modify the drawable! (setBounds)
 * @receiver Canvas the canvas to draw this drawable to
 * @param drawable Drawable the drawable to draw onto this canvas
 * @param left Int
 * @param top Int
 * @param right Int
 * @param bottom Int
 */
fun Canvas.draw(drawable: Drawable, left: Int, top: Int, right: Int, bottom: Int) {
    drawable.setBounds(left, top, right, bottom)
    drawable.draw(this)
}

package com.commonsense.android.kotlin.base.datastructures

import android.support.annotation.IntRange
import java.util.concurrent.atomic.AtomicInteger

/**
 * A ticket system
 * each category requests a ticket.
 * and if there has been assigned a number to a category, it will always get the same number back.
 */
class Ticketer(@IntRange(from = 0) initialValue: Int) {

    private val currentDrawNumber = AtomicInteger(initialValue)

    private val categoryStore = mutableMapOf<String, Int>()

    /**
     * Draws a number
     */
    @IntRange(from = 0)
    fun getIdForCategory(category: String): Int {
        if (!categoryStore.containsKey(category)) {
            synchronized(this) {
                if (!categoryStore.containsKey(category)) {
                    val number = drawNextNumber()
                    categoryStore[category] = number
                }
            }
        }
        return categoryStore[category] ?: 0
    }

    @IntRange(from = 0)
    private fun drawNextNumber(): Int {
        return currentDrawNumber.getAndIncrement()
    }
}

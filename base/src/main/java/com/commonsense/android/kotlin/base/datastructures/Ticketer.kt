package com.commonsense.android.kotlin.base.datastructures

import java.util.concurrent.atomic.AtomicInteger

/**
 * A ticket system
 * each category requests a ticket.
 * and if there has been assigned a number to a category, it will always get the same number back.
 */
class Ticketer(initalValue: Int) {
    private var currentDrawNumber = AtomicInteger(initalValue)
    private val categoryStore = mutableMapOf<String, Int>()
    /**
     * Draws a number
     */
    fun getIdForCategory(category: String): Int {
        if (!categoryStore.containsKey(category)) {
            synchronized(this) {
                val number = drawNextNumber()
                categoryStore.put(category, number)
            }
        }
        return categoryStore[category] ?: 0
    }

    private fun drawNextNumber(): Int {
        return currentDrawNumber.getAndIncrement()
    }
}

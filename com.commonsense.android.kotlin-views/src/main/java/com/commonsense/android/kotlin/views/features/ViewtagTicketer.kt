package com.commonsense.android.kotlin.views.features

import android.view.View
import com.commonsense.android.kotlin.base.FunctionUnit
import java.util.concurrent.atomic.AtomicInteger
import kotlin.reflect.KClass


/**
 * Created by Kasper Tvede on 08-08-2017.
 */
object ViewtagTicketer {
    private var currentDrawNumber = AtomicInteger(0x0200_0000) // all 24 bits set, such that ushr 24 > 2
    private val categoryStore = mutableMapOf<String, Int>()
    /**
     * Draws a number  (category uniqed) key for use with "setTag" and "getTag"  on a view.
     */
    fun getIdForCategory(category: String): Int {
        if (!categoryStore.containsKey(category)) {
            synchronized(ViewtagTicketer) {
                val number = drawNextNumber()
                categoryStore.put(category, number)
            }
        }
        return categoryStore.getOrDefault(category, 0)
    }

    private fun drawNextNumber(): Int {
        return currentDrawNumber.getAndIncrement()
    }


}

fun View.tagKeyFor(category: String): Int = ViewtagTicketer.getIdForCategory(category)

fun View.tagKeyFor(classAsCategory: KClass<*>): Int = tagKeyFor(classAsCategory.java.simpleName)

fun View.setTag(category: String, value: Any) {
    val index = ViewtagTicketer.getIdForCategory(category)
    setTag(index, value)
}

fun View.setTag(classAsCategory: KClass<*>, value: Any) =
        setTag(classAsCategory.java.simpleName, value)

inline fun <reified T : Any> View.getTag(forCategory: String): T? {
    val index = ViewtagTicketer.getIdForCategory(forCategory)
    return getTag(index) as? T
}

inline fun <reified T : Any> View.getTag(classAsCategory: KClass<*>): T? =
        getTag(classAsCategory.java.simpleName)


inline fun <reified T : Any> View.useTagOr(forCategory: String, action: FunctionUnit<T>, initialValue: () -> T) {
    val value = getTag<T>(forCategory)
    if (value == null) {
        val newValue = initialValue()
        setTag(forCategory, newValue)
        action(newValue)
    } else {
        action(value)
    }
}

inline fun <reified T : Any> View.useTagOr(classAsCategory: KClass<*>, action: FunctionUnit<T>, defaultToInsert: () -> T) =
        useTagOr(classAsCategory.java.simpleName, action, defaultToInsert)

fun View.clearTag(forCategory: String) {
    val index = ViewtagTicketer.getIdForCategory(forCategory)
    setTag(index, null)
}

fun View.clearTag(classAsCategory: KClass<*>) =
        clearTag(classAsCategory.java.simpleName)

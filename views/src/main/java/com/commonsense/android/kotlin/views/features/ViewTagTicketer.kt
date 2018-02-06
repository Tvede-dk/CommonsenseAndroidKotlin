package com.commonsense.android.kotlin.views.features

import android.view.View
import com.commonsense.android.kotlin.base.FunctionUnit
import com.commonsense.android.kotlin.base.datastructures.Ticketer
import kotlin.reflect.KClass


/**
 * A ticket system for view tag's , the point being that we are going to allow multiple
 * features working side by side, without ever overlapping by forcing them to draw a number to
 * use for their ViewTag indexes
 */
object ViewTagTicketer {
    /**
     * The ticket system behind the scenes.
     */
    private val ticketer: Ticketer = Ticketer(0x0200_0000)

    /**
     * Draws a number  (category unique) key for use with "setTag" and "getTag"  on a view.
     */
    fun getIdForCategory(category: String): Int = ticketer.getIdForCategory(category)
}

fun View.tagKeyFor(category: String): Int = ViewTagTicketer.getIdForCategory(category)

fun View.tagKeyFor(classAsCategory: KClass<*>): Int = tagKeyFor(classAsCategory.java.simpleName)

fun <T> View.setTag(category: String, value: T): T {
    val index = ViewTagTicketer.getIdForCategory(category)
    setTag(index, value)
    return value
}

fun View.setTag(classAsCategory: KClass<*>, value: Any) =
        setTag(classAsCategory.java.simpleName, value)

inline fun <reified T : Any> View.getTag(forCategory: String): T? {
    val index = ViewTagTicketer.getIdForCategory(forCategory)
    return getTag(index) as? T
}

inline fun <reified T : Any> View.getTag(classAsCategory: KClass<*>): T? =
        getTag(classAsCategory.java.simpleName)


inline fun <reified T : Any> View.useTagOr(forCategory: String, action: FunctionUnit<T>, initialValue: () -> T) {
    val value = getTag<T>(forCategory)
    action(value ?: setTag(forCategory, initialValue()))
}

inline fun <reified T : Any> View.getTagOr(forCategory: String, defaultValue: () -> T): T {
    return getTag(forCategory) ?: setTag(forCategory, defaultValue())
}

inline fun <reified T : Any> View.useTagOr(classAsCategory: KClass<*>, action: FunctionUnit<T>, defaultToInsert: () -> T) =
        useTagOr(classAsCategory.java.simpleName, action, defaultToInsert)

fun View.clearTag(forCategory: String) {
    val index = ViewTagTicketer.getIdForCategory(forCategory)
    setTag(index, null)
}

fun View.clearTag(classAsCategory: KClass<*>) =
        clearTag(classAsCategory.java.simpleName)

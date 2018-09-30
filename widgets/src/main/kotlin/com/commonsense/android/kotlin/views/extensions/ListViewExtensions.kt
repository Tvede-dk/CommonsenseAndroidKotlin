@file:Suppress("unused", "NOTHING_TO_INLINE", "MemberVisibilityCanBePrivate")

package com.commonsense.android.kotlin.views.extensions

import android.widget.*


/**
 * Scrolls this listView to the first visible item (the top).
 * animated / smoothly
 * @receiver ListView
 */
fun ListView.scrollToTop() {
    smoothScrollToPositionFromTop(firstVisiblePosition, 0)
}

/**
 * Scrolls this listView to the last visible position (item)
 * animated / smoothly
 * @receiver ListView
 */
fun ListView.scrollToBottom() {
    smoothScrollToPosition(lastVisiblePosition)
}
package com.commonsense.android.kotlin.views.extensions

import android.widget.ListView

/**
 * Created by Kasper Tvede on 1/27/2018.
 * Purpose:
 *
 */

/**
 * Scrolls this listView to the first visible item (the top).
 */
fun ListView.scrollToTop() {
    smoothScrollToPositionFromTop(firstVisiblePosition, 0)
}

/**
 * Scrolls this listView to the last visible position (item)
 */
fun ListView.scrollToBottom() {
    smoothScrollToPosition(lastVisiblePosition)
}
@file:Suppress("unused", "NOTHING_TO_INLINE", "MemberVisibilityCanBePrivate")

package com.commonsense.android.kotlin.views.widgets

import android.support.v7.widget.*

/**
 * Created by kasper on 29/05/2017.
 */
class RoundCornersImageView : AppCompatImageView {

    constructor(context: android.content.Context) : super(context)
    constructor(context: android.content.Context, attrs: android.util.AttributeSet) : super(context, attrs) {
        setupAttrs(attrs)
    }

    constructor(context: android.content.Context, attrs: android.util.AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        setupAttrs(attrs)
    }

    fun setupAttrs(@Suppress("UNUSED_PARAMETER") attrs: android.util.AttributeSet) {

    }


}

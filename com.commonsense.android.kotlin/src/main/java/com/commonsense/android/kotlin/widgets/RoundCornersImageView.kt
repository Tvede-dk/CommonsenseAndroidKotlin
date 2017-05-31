package com.commonsense.android.kotlin.widgets

import android.content.Context
import android.util.AttributeSet
import android.widget.ImageView

/**
 * Created by kasper on 29/05/2017.
 */
class RoundCornersImageView : ImageView {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        // setupAttrs(attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        //   setupAttrs(attrs)
    }
}
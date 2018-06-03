package com.commonsense.android.kotlin.views.helpers

import android.text.Editable
import android.text.TextWatcher

/**
 * Created by Kasper Tvede on 21-05-2017.
 */

class OnTextChangedWatcher(val onChanged: (sequence: CharSequence) -> Unit) : TextWatcher {


    override fun beforeTextChanged(changedText: CharSequence?, p1: Int, p2: Int, p3: Int) {

    }

    override fun onTextChanged(charSequence: CharSequence, p1: Int, p2: Int, p3: Int) {
        onChanged(charSequence)
    }

    override fun afterTextChanged(afterChanged: Editable?) {
    }

}


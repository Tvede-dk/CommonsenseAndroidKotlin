@file:Suppress("unused", "NOTHING_TO_INLINE", "MemberVisibilityCanBePrivate")

package com.commonsense.android.kotlin.views.features

import android.app.*
import android.content.*
import android.support.annotation.*
import android.support.annotation.IntRange
import android.widget.*
import java.util.*

/**
 * Created by kasper on 11/07/2017.
 */


fun Context.showDatePickerDialog(
        @IntRange(from = 0) startYear: Int,
        @IntRange(from = 0) monthOfYear: Int,
        @IntRange(from = 0) dayOfMonth: Int,
        minDate: Date?,
        maxDate: Date?,
        onDateSelected: (year: Int, month: Int, day: Int) -> Unit,
        @StringRes otherAction: Int,
        onOtherAction: () -> Unit) {

    val dialog = DatePickerDialog(this, { _: DatePicker, year: Int, month: Int, day: Int ->
        onDateSelected(year, month, day)
    }, startYear, monthOfYear, dayOfMonth)

    dialog.setButton(DatePickerDialog.BUTTON_NEUTRAL, this.getString(otherAction)) { _, _ ->
        onOtherAction()
    }

    minDate?.let {
        dialog.datePicker.minDate = it.time
    }

    maxDate?.let {
        dialog.datePicker.maxDate = it.time
    }

    dialog.show()
}
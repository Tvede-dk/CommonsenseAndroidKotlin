package com.commonsense.android.kotlin.helperClasses

import android.app.DatePickerDialog
import android.content.Context
import android.support.annotation.IntRange
import android.support.annotation.StringRes
import android.widget.DatePicker
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

    dialog.setButton(DatePickerDialog.BUTTON_NEUTRAL, this.getString(otherAction), { _, _ ->
        onOtherAction()
    })

    minDate?.let {
        dialog.datePicker.minDate = it.time
    }

    maxDate?.let {
        dialog.datePicker.maxDate = it.time
    }

    dialog.show()
}
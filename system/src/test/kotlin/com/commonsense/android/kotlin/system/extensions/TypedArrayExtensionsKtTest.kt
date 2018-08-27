package com.commonsense.android.kotlin.system.extensions

import android.content.res.*
import com.commonsense.android.kotlin.test.*
import org.junit.*
import org.mockito.ArgumentMatchers.eq
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock


/**
 * Created by Kasper Tvede on 30-05-2018.
 * Purpose:
 */
class TypedArrayExtensionsKtTest {
    @Ignore
    @Test
    fun getDrawableSafe() {
    }

    @Ignore
    @Test
    fun getTextSafe() {
    }

    @Ignore
    @Test
    fun getTextSafe1() {
    }

    @Test
    fun getColorSafe() {
        val typedArray = mock(TypedArray::class.java)
//        `when`(ColorStateList.valueOf(any())).thenReturn(ColorStateList(arrayOf(), intArrayOf()))
        `when`(typedArray.hasValue(eq(1))).thenReturn(true)
        `when`(typedArray.hasValue(eq(2))).thenReturn(true)
//        `when`(typedArray.getColorStateList(eq(1))).thenReturn(
//                ColorStateList.valueOf(Color.RED))
//
        typedArray.getColorSafe(0).assertNull("have nothing on 0")
        typedArray.getColorSafe(2).assertNull("have index 2 but not a color on it")
//        typedArray.getColorSafe(1).assertNotNullAndEquals(Color.RED, "should have red color as per mock")


    }

    @Ignore
    @Test
    fun getColorSafe1() {
    }

    @Test
    fun ifHaveOrNull() {
        val typedArray = mock(TypedArray::class.java)
        `when`(typedArray.hasValue(eq(0))).thenReturn(false)
        `when`(typedArray.hasValue(eq(1))).thenReturn(true)


        typedArray.ifHaveOrNull(0) { failTest("should not have 0 index as per mock") }
        var counter = 0
        typedArray.ifHaveOrNull(1) { counter += 1 }
        counter.assert(1, "should have runned action since style 1 is there.")

//        `when`(context.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
//                .thenReturn(layoutInflater)
//        `when`(typedArray.getInteger(R.styleable.MapView_overlayMode, OVERLAY_MODE_SDK))
//                .thenReturn(overlayMode)

    }
}
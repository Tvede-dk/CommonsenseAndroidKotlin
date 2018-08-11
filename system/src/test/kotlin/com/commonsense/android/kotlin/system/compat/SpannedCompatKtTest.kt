package com.commonsense.android.kotlin.system.compat

import com.commonsense.android.kotlin.test.*
import org.junit.*
import org.robolectric.annotation.*


/**
 * Created by Kasper Tvede on 22-05-2018.
 * Purpose:
 */
@Config(sdk = [18, 25])
class SpannedCompatKtTest : BaseRoboElectricTest() {

    @Test
    fun fromHtml() {
        val empty = "".fromHtml()
        empty.length.assert(0)
        val text = "test".fromHtml()
        text.length.assert(4)


        val invalidHtml = "<li><p></></p> </".fromHtml()
        invalidHtml.length.assert(0, "bad html should get \"discarded\"" +
                " thus its full of nothing (ignoring whitespaces)")
        val simpleText = "<p>test</p>".fromHtml()
        simpleText.length.assertLargerOrEqualTo(4, "test should be there, " +
                "plus some styling regarding the tags")


    }
}
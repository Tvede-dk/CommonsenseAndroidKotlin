package com.commonsense.android.kotlin.views.widgets

import android.widget.*
import com.commonsense.android.kotlin.test.*
import org.junit.*
import org.junit.runner.*
import org.robolectric.*
import org.robolectric.annotation.*

/**
 * Created by Kasper Tvede on 24-07-2017.
 */


@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
class ViewExtensionsTest : BaseRoboElectricTest() {

    @Test
    fun testViewTagging() {
        val view = ImageView(context)
        view.tag = "testTag"
        view.setTag(34000000, "valueGold")

        view.tag.assertAs("testTag")


    }

}
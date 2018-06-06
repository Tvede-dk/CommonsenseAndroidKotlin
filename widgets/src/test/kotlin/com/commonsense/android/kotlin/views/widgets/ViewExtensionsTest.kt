package com.commonsense.android.kotlin.views.widgets

import android.widget.ImageView
import com.commonsense.android.kotlin.test.BaseRoboElectricTest
import com.commonsense.android.kotlin.test.assertAs
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

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
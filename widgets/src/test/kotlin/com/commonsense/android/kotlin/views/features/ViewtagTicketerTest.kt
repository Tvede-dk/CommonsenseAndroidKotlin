package com.commonsense.android.kotlin.views.features

import com.commonsense.android.kotlin.test.*
import org.junit.*

/**
 * Created by Kasper Tvede on 08-08-2017.
 */


class ViewtagTicketerTest : BaseRoboElectricTest() {

    @Test
    fun testTicketSystem() {
        val first = ViewtagTicketer.getIdForCategory("test")
        val isOverFrameworkIndex = (first ushr 24) >= 2
        isOverFrameworkIndex.assert(true, "should always produce numbers above 24 bits set.")

        first.assert(ViewtagTicketer.getIdForCategory("test"), "should produce same index for same category")

        val second = ViewtagTicketer.getIdForCategory("test2")
        (second!= first).assert(true, "different categories should produce different indexes.")
    }
}
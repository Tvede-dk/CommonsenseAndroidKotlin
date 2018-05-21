package com.commonsense.android.kotlin.base.patterns

import com.commonsense.android.kotlin.test.BaseRoboElectricTest
import com.commonsense.android.kotlin.test.assert
import org.junit.Assert
import org.junit.jupiter.api.Test

/**
 * Created by Kasper Tvede on 23-07-2017.
 */
class ToggleBooleanTest : BaseRoboElectricTest() {

    @Test
    fun testToggling() {
        val toggler = ToggleBoolean(false)
        toggler.ifTrue {
            Assert.fail()
        }
        var didRun = false
        toggler.ifFalse {
            didRun = true
        }
        didRun.assert(true, "should do the false action on false value")

        toggler.ifFalse {
            Assert.fail("should not stay false.")
        }

        var didRunOnTrue = false
        toggler.ifTrue {
            didRunOnTrue = true
        }
        didRunOnTrue.assert(true, "should do the true action on true value")
        toggler.ifTrue {
            Assert.fail("should not stay true.")
        }

    }

}

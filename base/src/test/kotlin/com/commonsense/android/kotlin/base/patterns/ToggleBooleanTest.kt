package com.commonsense.android.kotlin.base.patterns

import com.commonsense.android.kotlin.test.*
import org.junit.*


/**
 * Created by Kasper Tvede on 23-07-2017.
 */
class ToggleBooleanTest {

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

    @Test
    fun ifTrue() {
        val startFalse = ToggleBoolean(false)
        startFalse.ifTrue { failTest() }
        var counter = 0
        startFalse.ifFalse { counter = 1 }
        counter.assert(1)

    }


    @Test
    fun ifFalse() {
        val startFalse = ToggleBoolean(true)
        startFalse.ifFalse { failTest() }
        var counter = 0
        startFalse.ifTrue { counter = 1 }
        counter.assert(1)

    }

}

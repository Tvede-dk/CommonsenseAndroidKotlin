package com.commonsense.android.base.patterns

import com.commonsense.android.kotlin.base.patterns.ObserverPattern
import com.commonsense.android.kotlin.test.assert
import org.junit.Test

/**
 * Created by Kasper Tvede on 23-07-2017.
 */
class ObserverPatternTest {


    @Test
    fun testAddRemoveFunctions() {
        val observer = ObserverPattern<Int>()
        var counter1 = 0
        var counter2 = 0
        var counter3 = 0

        val listener1 = { value: Int -> counter1 += value }
        val listener2 = { value: Int -> counter2 += value }
        val listener3 = { value: Int -> counter3 += value }

        observer.addListener(listener1)
        observer.addListener(listener2)
        observer.addListener(listener3)

        observer.notify(7)

        counter1.assert(7, "should be set though listener 1")
        counter2.assert(7, "should be set though listener 2")
        counter3.assert(7, "should be set though listener 3")

        observer.removeListener(listener1)



        counter2 = 0
        counter3 = 0
        observer.notify(18)

        counter1.assert(7, "should not changed when removed")
        counter2.assert(18, "should be set though listener 2")
        counter3.assert(18, "should be set though listener 3")

        observer.removeListener(listener3)

        counter2 = 0
        observer.notify(5)

        counter1.assert(7, "should not changed when removed")
        counter2.assert(5, "should be set though listener 2")
        counter3.assert(18, "should not changed when removed")

        observer.addListener(listener1)
        observer.addListener(listener2)
        observer.addListener(listener3)

        counter1 = 0
        counter2 = 0
        counter3 = 0
        //counter2  added twice.. very important

        observer.notify(42)
        counter1.assert(42, "should be set though listener 1")
        counter2.assert(84, "should be set though listener 2,2 times (42+42)")
        counter3.assert(42, "should be set though listener 3")

        observer.clearListeners()
        observer.notify(100)
        counter1.assert(42, "should not be called after clear listeners")
        counter2.assert(84, "should not be called after clear listeners")
        counter3.assert(42, "should not be called after clear listeners")

    }

}
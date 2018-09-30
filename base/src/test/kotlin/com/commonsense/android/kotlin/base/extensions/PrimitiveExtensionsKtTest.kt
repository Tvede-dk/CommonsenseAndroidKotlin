package com.commonsense.android.kotlin.base.extensions

import com.commonsense.android.kotlin.base.extensions.collections.*
import org.junit.*
import org.junit.jupiter.api.Test
import java.util.concurrent.*

/**
 * Created by Kasper Tvede on 27-05-2017.
 */
class PrimitiveExtensionsKtTest {

    @Test
    fun testOntrue() {
        val sem = Semaphore(0)
        true.onTrue {
            sem.release()
        }
        Assert.assertTrue(sem.tryAcquire(1, 1, TimeUnit.SECONDS))

        true.onFalse {
            sem.release(10)
        }
        Assert.assertFalse(sem.tryAcquire(10))  //should not get the 10
    }

    @Test
    fun testOnFalse() {
        val sem = Semaphore(0)
        false.onFalse {
            sem.release()
        }
        Assert.assertTrue(sem.tryAcquire(1, 1, TimeUnit.SECONDS))

        false.onTrue {
            sem.release(10)
        }
        Assert.assertFalse(sem.tryAcquire(10))  //should not get the 10
    }

}
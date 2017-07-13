package com.commonsense.android.kotlin.baseDataClasses

import kotlinx.coroutines.experimental.runBlocking
import org.junit.Test

/**
 * Created by Kasper Tvede on 13-07-2017.
 */
class DataComposer2Test {
    @Test
    fun thisIsfun() = runBlocking {

       /* val text = "8,8,98,456,123,456,7,4,5"

        var result = mutableListOf<Long>()
        val composer = DataComposer2({ text }, this@DataComposer2Test::convertRaw, this@DataComposer2Test::splitLogic, this@DataComposer2Test::processlogic, {
            result.addAll(it)
        })

        composer.startProcess().await()

        Assert.assertEquals(result.size, 9)
        Assert.assertEquals(result.first(), 8L)
        Assert.assertEquals(result.last(), 5L)*/
    }


    //TODO considerations for below functions.
    // async ??? if this is a LOOONG operation ??????
    //ERRORS!?!?!?!?

    private fun convertRaw(raw: String): String {
        return raw
    }

    private fun splitLogic(input: String): List<Int> {
        return input.split(",").map { it.trim() }.map { it.toInt() }
    }

    private fun processlogic(value: Int): Long {
        return value.toLong()
    }


}
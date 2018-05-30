package com.commonsense.android.kotlin.system.resourceHandling

import android.net.Uri
import android.os.Environment
import com.commonsense.android.kotlin.test.BaseRoboElectricTest
import com.commonsense.android.kotlin.test.assertNotNull
import com.commonsense.android.kotlin.test.assertTrue
import org.junit.Test

import org.junit.Assert.*
import org.robolectric.Robolectric
import org.robolectric.Shadows.shadowOf
import org.robolectric.shadows.ShadowContentResolver


/**
 * Created by Kasper Tvede on 28-05-2018.
 * Purpose:
 */
class FileHelpersKtTest : BaseRoboElectricTest() {

    @Test
    fun copyTo() {
//        val uri = Uri.parse("content://test")
//        val content = byteArrayOf(42)
//        val contentResolver = shadowOf(context.contentResolver)
//
//        contentResolver.registerInputStream(uri, content.inputStream())
//        uri.exists(context.contentResolver).assertTrue("written file should exists")
//        context.contentResolver.openInputStream(uri).assertNotNull()
    }

    @Test
    fun exists() {

    }
}
package com.commonsense.android.kotlin.system.imaging

import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import com.commonsense.android.kotlin.test.BaseRoboElectricTest
import com.commonsense.android.kotlin.test.assert
import com.commonsense.android.kotlin.test.assertNotNullApply
import kotlinx.coroutines.experimental.runBlocking
import org.junit.Test
import org.robolectric.Shadows
import org.robolectric.annotation.Config
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream


/**
 * Created by Kasper Tvede on 24-07-2017.
 */


@Config(manifest = Config.NONE)
class ImageHelpersTest : BaseRoboElectricTest() {


    @Test
    fun testLoadStoreBitmap() = runBlocking {
        val resolver = this@ImageHelpersTest.context.contentResolver
        val shadow = Shadows.shadowOf(resolver)
        val image = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)
        image.eraseColor(Color.RED)
        val uri = Uri.parse("file://test.png")
        image.saveTo(uri, resolver, 100, Bitmap.CompressFormat.PNG).await()
        //if not thrown or anything, then we are properly ok to try and use the result as the input to the load methods.

        val streamOut = ByteArrayOutputStream()
        image.outputTo(streamOut, 100, Bitmap.CompressFormat.PNG)
        shadow.registerInputStream(uri, ByteArrayInputStream(streamOut.toByteArray()))
        val readBack = uri.loadBitmapScaled(resolver, 100).await()
        readBack.assertNotNullApply {
            this.width.assert(image.width)
            this.height.assert(image.height)
            this.density.assert(image.density)
            this.byteCount.assert(image.byteCount)
        }

    }
}
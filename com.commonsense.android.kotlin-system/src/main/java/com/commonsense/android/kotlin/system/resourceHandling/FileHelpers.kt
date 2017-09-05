package com.commonsense.android.kotlin.system.resourceHandling

import android.content.ContentResolver
import android.net.Uri
import com.commonsense.android.kotlin.system.logging.tryAndLog
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.async
import java.io.BufferedInputStream
import java.io.BufferedOutputStream

/**
 * Created by Kasper Tvede on 31-07-2017.
 *
 */

fun Uri.copyTo(other: Uri, resolver: ContentResolver) = async(CommonPool) {
    val openIS = resolver.openInputStream(this@copyTo)
    val outIS = resolver.openOutputStream(other)
    tryAndLog("Uri.copyTo") {
        val bufferedIn = BufferedInputStream(openIS)
        val bufferedOut = BufferedOutputStream(outIS)
        val buff = ByteArray(32 * 1024) //32 kb buffer.
        var len: Int = bufferedIn.read(buff)
        while (len > 0) {
            bufferedOut.write(buff, 0, len)
            len = bufferedIn.read(buff)
        }
        bufferedIn.close()
        bufferedOut.close()
    }

}


fun Uri.exists(contentResolver: ContentResolver): Boolean {
    try {
        contentResolver.openAssetFileDescriptor(this, "r").use {
            return true
        }
    } catch (exception: Exception) {
        return false
    }
}
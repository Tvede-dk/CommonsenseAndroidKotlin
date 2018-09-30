@file:Suppress("unused", "NOTHING_TO_INLINE")

package com.commonsense.android.kotlin.system.resourceHandling

import android.content.*
import android.net.*
import com.commonsense.android.kotlin.system.logging.*
import kotlinx.coroutines.experimental.*
import java.io.*

/**
 *
 * @receiver Uri
 * @param other Uri
 * @param resolver ContentResolver
 * @return Deferred<Unit?>
 */
fun Uri.copyTo(other: Uri, resolver: ContentResolver) = GlobalScope.async {
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

/**
 * Tells if a given Uri exists;
 * this is for files on the device.
 * so Uri's that starts with "file://"
 * @receiver Uri
 * @param contentResolver ContentResolver
 * @return Boolean
 */
fun Uri.exists(contentResolver: ContentResolver): Boolean = try {
    contentResolver.openAssetFileDescriptor(this, "r").use {
        true
    }
} catch (exception: Exception) {
    false
}
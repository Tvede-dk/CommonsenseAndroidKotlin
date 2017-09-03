package com.commonsense.android.kotlin.system.resourceHandling

import android.content.ContentResolver
import android.net.Uri
import android.provider.MediaStore
import com.commonsense.android.kotlin.system.logging.tryAndLog
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.async
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.File

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
    val projection = arrayOf(MediaStore.MediaColumns.DATA)
    val query = contentResolver.query(this,
            projection, null, null, null)
    return query.use {
        return@use tryAndLog("Uri.exists") {
            return@tryAndLog if (query?.moveToFirst() == true) {
                val path = query.getString(0)
                File(path).exists()
            } else {
                false
            }
        } ?: true
    }
}
@file:Suppress("unused", "NOTHING_TO_INLINE", "MemberVisibilityCanBePrivate")

package com.commonsense.android.kotlin.system.imaging


import android.*
import android.app.*
import android.content.*
import android.graphics.*
import android.net.*
import android.provider.*
import androidx.annotation.*
import com.commonsense.android.kotlin.base.*
import com.commonsense.android.kotlin.system.base.*
import com.commonsense.android.kotlin.system.base.helpers.*
import com.commonsense.android.kotlin.system.permissions.*


/**
 * A Simple wrapper class that handles the retrieval of images(pictures) from either the gallery or the camera.
 *
 *
 */
//TODO better name: or at least spell it right :)
//-picture - taker, image retriver, image fetcher, cameraGalleryImageHandler ?
// -- hmm somewhat along those
class PictureRetriver(private val activity: BaseActivity,
                      private val callback: (path: Uri, fromCamera: Boolean) -> Unit,
                      private val optionalCancelCallback: EmptyFunction? = null,
                      private val requestCode: Int = 18877) {


    private var pictureUri: Uri? = null
    /**
     * First queries for the required permission before retrieving the image and returning the result.
     */
    fun useCamera() = activity.usePermissionEnums(listOf(PermissionEnum.WriteExternalStorage, PermissionEnum.Camera), usePermission = {
        //we also need camera.

        //lets use the background since the contentResolver is using disk access, which
        //violates the UI thread on file access principle; then only when we are ready,
        // use the main thread.
        activity.launchInBackground("useCamera") {
            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            takePictureIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)

            val values = ContentValues(1)
            values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpg")
            pictureUri = activity.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, pictureUri)
            if (takePictureIntent.resolveActivity(activity.packageManager) != null) {
                activity.launchInUi("useCamera") {
                    activity.startActivityForResult(takePictureIntent, null, requestCode, this::onActivityResult)
                }
            }
        }
    })

    fun useGallery() {
        val pickIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        pickIntent.type = "image/*"
        activity.startActivityForResult(pickIntent, null, requestCode, this::onActivityResult)
    }

    fun onActivityResult(resultCode: Int, data: Intent?) {
        if (resultCode != Activity.RESULT_OK) {
            optionalCancelCallback?.invoke()
            return
        }
        var isCamera = true
        data?.data?.let {
            //gallery
            pictureUri = it
            isCamera = false
        }

        val safePictureUri = pictureUri
        if (safePictureUri != null) {
            callback(safePictureUri, isCamera)
        } else {
            optionalCancelCallback?.invoke()
        }
        pictureUri = null
    }

    @RequiresPermission(allOf = [(Manifest.permission.WRITE_EXTERNAL_STORAGE), (Manifest.permission.READ_EXTERNAL_STORAGE)])
    fun getImage(fromCamera: Boolean) {
        if (fromCamera) {
            useCamera()
        } else {
            useGallery()
        }
    }
}

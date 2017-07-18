package com.commonsense.android.kotlin.system.imaging

import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import com.commonsense.android.kotlin.system.PermissionEnum
import com.commonsense.android.kotlin.system.askAndUsePermission
import com.commonsense.android.kotlin.system.base.ActivityResultCallbackOk
import com.commonsense.android.kotlin.system.base.BaseActivity


/**
 * Created by Kasper Tvede on 10-07-2017.
 */
//TODO better name:
//-picture - taker, image retriver, image fetcher, cameraGalleryImageHandler ?
// -- hmm somewhat along those
class PictureRetriver(private val activity: BaseActivity, private val callback: (Uri) -> Unit, private val requestCode: Int = 18877) : ActivityResultCallbackOk {

    init {
        activity.addActivityResultListenerOnlyOk(requestCode, this)
    }


    var thumbnail: Bitmap? = null

    private var pictureUri: Uri? = null

    fun useCamera() = activity.askAndUsePermission(PermissionEnum.WriteExternalStorage) {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        takePictureIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)

        val values = ContentValues(1)
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpg")
        pictureUri = activity.contentResolver
                .insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, pictureUri)
        if (takePictureIntent.resolveActivity(activity.packageManager) != null) {
            activity.startActivityForResult(takePictureIntent, requestCode)//For testing.
        }
    }

    fun useGallery() {
        val pickIntent = Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        pickIntent.type = "image/*"
        activity.startActivityForResult(pickIntent, requestCode)
    }

    override fun onActivityResult(data: Intent?) {
        data?.data?.let {
            pictureUri = it
        }

        val imageBitmap = data?.extras?.get("data") as? Bitmap
        imageBitmap?.let {
            thumbnail = it
        }
        //TODO , might wanna create a thumbnail in background before this point.

        activity.LaunchInUi(this::class.java.simpleName, {
            pictureUri?.let(callback)
        })

    }

    fun getImage(fromCamera: Boolean) {
        if (fromCamera) {
            useCamera()
        } else {
            useGallery()
        }
    }
}

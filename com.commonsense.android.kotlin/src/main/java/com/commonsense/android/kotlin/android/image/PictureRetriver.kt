package com.commonsense.android.kotlin.android.image

import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import com.commonsense.android.kotlin.baseClasses.ActivityResultCallbackOk
import com.commonsense.android.kotlin.baseClasses.BaseActivity
import java.io.File

/**
 * Created by Kasper Tvede on 10-07-2017.
 */
//TODO better name:
//-picture - taker, image retriver, image fetcher, cameraGalleryImageHandler ?
// -- hmm somewhat along those
class PictureRetriver(private val activity: BaseActivity, private val requestCode: Int = 8877) : ActivityResultCallbackOk {

    init {
        activity.addActivityResultListenerOnlyOk(requestCode, this)
    }

    fun openCamera() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val photo = File(Environment.getExternalStorageDirectory(), "Pic.jpg")
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                Uri.fromFile(photo))
        if (takePictureIntent.resolveActivity(activity.packageManager) != null) {
            activity.startActivityForResult(takePictureIntent, requestCode)//For testing.
        }
    }

    override fun onActivityResult(data: Intent?) {

    }

    fun getImage() {
        //present user with a choice of either camera or from gallery
        openCamera()
    }


}
package com.CommonSenseAndroidKotlin.example.fragments

import android.graphics.Bitmap
import android.net.Uri
import android.support.v7.widget.LinearLayoutManager
import com.CommonSenseAndroidKotlin.example.databinding.CameraFragmentDemoBinding
import com.CommonSenseAndroidKotlin.example.databinding.SimpleImageListItemBinding
import com.commonsense.android.kotlin.android.extensions.widets.setOnclickAsync
import com.commonsense.android.kotlin.android.image.PictureRetriver
import com.commonsense.android.kotlin.android.image.loadBitmapScaled
import com.commonsense.android.kotlin.baseClasses.BaseActivity
import com.commonsense.android.kotlin.baseClasses.databinding.*
import com.commonsense.android.kotlin.extensions.tryAndLog
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch

/**
 * Created by Kasper Tvede on 10-07-2017.
 */
class CameraFragment : BaseDatabindingFragment<CameraFragmentDemoBinding>() {
    override fun getInflater(): InflateBinding<CameraFragmentDemoBinding>
            = CameraFragmentDemoBinding::inflate

    private val imageHelper by lazy {
        PictureRetriver(activity as BaseActivity, this::onImageSelected)
    }

    private val imageAdapter by lazy {
        BaseDataBindingRecyclerAdapter(context)
    }

    override fun useBinding() {
        binding.cameraFragmentImagesTake.setOnclickAsync {
            imageHelper.getImage(fromCamera = true)
        }
        binding.cameraFragmentImagesChoose.setOnclickAsync {
            imageHelper.getImage(fromCamera = false)
        }

        binding.cameraFragmentImagesReset.setOnclickAsync {
            imageAdapter.clear()
        }

        binding.cameraFragmentImagesList.setupAsync(imageAdapter, LinearLayoutManager(context), {
            launch(UI) {

            }
        })
    }

    fun onImageSelected(imageUri: Uri) {
        tryAndLog("bitmap") {
            val toString = imageUri.toString()
            LaunchInBackground("scaleUri" + toString) {
                val bitmap = imageUri.loadBitmapScaled(context.contentResolver, 200)
                        ?: return@LaunchInBackground
                LaunchInUi("updateAdapter" + toString) {
                    imageAdapter.add(ImageViewItemRender(bitmap), 0)
                }
            }
//            val bitmap = MediaStore.Images.Media.getBitmap(context.contentResolver, imageUri)

        }
    }

}


class ImageViewItemRender(bitmap: Bitmap) : BaseRenderModel<Bitmap, SimpleImageListItemBinding>(bitmap,
        SimpleImageListItemBinding::class.java) {

    override fun getInflaterFunction(): ViewInflatingFunction<SimpleImageListItemBinding>
            = SimpleImageListItemBinding::inflate

    override fun renderFunction(view: SimpleImageListItemBinding,
                                model: Bitmap,
                                viewHolder: BaseViewHolderItem<SimpleImageListItemBinding>) {
        view.simpleImageListItemImage.setImageBitmap(model)
    }


}
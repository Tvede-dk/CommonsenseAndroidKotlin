package com.CommonSenseAndroidKotlin.example.fragments

import android.graphics.Bitmap
import android.net.Uri
import android.support.v7.widget.LinearLayoutManager
import com.CommonSenseAndroidKotlin.example.databinding.CameraFragmentDemoBinding
import com.CommonSenseAndroidKotlin.example.databinding.SimpleImageListItemBinding
import com.commonsense.android.kotlin.android.extensions.safeToast
import com.commonsense.android.kotlin.android.extensions.widets.setOnclickAsync
import com.commonsense.android.kotlin.android.image.PictureRetriver
import com.commonsense.android.kotlin.android.image.calculateOptimalThumbnailSize
import com.commonsense.android.kotlin.android.image.loadBitmapPreviews
import com.commonsense.android.kotlin.baseClasses.BaseActivity
import com.commonsense.android.kotlin.baseClasses.databinding.*
import com.commonsense.android.kotlin.extensions.collections.toIntArray
import com.commonsense.android.kotlin.extensions.tryAndLogSuspend
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import kotlin.system.measureTimeMillis

/**
 * Created by Kasper Tvede on 10-07-2017.
 */
class CameraFragment : BaseDatabindingFragment<CameraFragmentDemoBinding>() {
    override fun getInflater(): InflateBinding<CameraFragmentDemoBinding>
            = CameraFragmentDemoBinding::inflate

    private val imageHelper by lazy {
        PictureRetriver(activity as BaseActivity, { onImageSelected(it) })
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

    fun onImageSelected(imageUri: Uri) = LaunchInUi("bitmap") {
        tryAndLogSuspend("bitmap") {
            //            val bitmap = imageUri.loadBitmapScaled(context.contentResolver, 200).await() ?: return@tryAndLogSuspend
            var images: List<Bitmap>? = null
            val scales = (100 downTo 50) step 1
            val time = measureTimeMillis {

                images = imageUri.loadBitmapPreviews(scales.toIntArray(),
                        context.calculateOptimalThumbnailSize(), context.contentResolver)
                        .await() ?: return@tryAndLogSuspend
            }
            activity.safeToast("time is : $time ms")
            val safeImages = images ?: return@tryAndLogSuspend
            val mapped = safeImages.map { ImageViewItemRender(it) }
            imageAdapter.addAll(mapped, 0)
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
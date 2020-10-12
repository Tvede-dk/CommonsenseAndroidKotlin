package csense.android.exampleApp.fragments

import android.annotation.*
import android.graphics.*
import android.net.*
import androidx.recyclerview.widget.*
import com.commonsense.android.kotlin.base.extensions.collections.*
import com.commonsense.android.kotlin.system.base.*
import com.commonsense.android.kotlin.system.extensions.*
import com.commonsense.android.kotlin.system.imaging.*
import com.commonsense.android.kotlin.system.logging.*
import com.commonsense.android.kotlin.views.*
import com.commonsense.android.kotlin.views.databinding.adapters.*
import com.commonsense.android.kotlin.views.databinding.fragments.*
import com.commonsense.android.kotlin.views.extensions.*
import csense.android.exampleApp.databinding.*
import kotlinx.coroutines.*
import kotlin.system.*

/**
 * Created by Kasper Tvede on 10-07-2017.
 */
class CameraFragment : BaseDatabindingFragment<CameraFragmentDemoBinding>() {
    override fun getInflater(): InflateBinding<CameraFragmentDemoBinding> = CameraFragmentDemoBinding::inflate

    private val imageHelper by lazy {
        PictureRetriver(activity as BaseActivity, { path, _ -> onImageSelected(path) })
    }

    private val imageAdapter = BaseDataBindingRecyclerAdapter()


    @SuppressLint("MissingPermission")
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

        binding.cameraFragmentImagesList.setupAsync(imageAdapter, LinearLayoutManager(context)) {
            GlobalScope.launch(Dispatchers.Main) {
            }
        }
    }

    fun onImageSelected(imageUri: Uri) = launchInUi("bitmap") {
        val context = context ?: return@launchInUi
        val activity = activity ?: return@launchInUi
        val imageAdapter = imageAdapter

        tryAndLogSuspend("bitmap") {
            //            val bitmap = imageUri.loadBitmapScaled(context.contentResolver, 200).await() ?: return@tryAndLogSuspend
            var images: List<Bitmap>?
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

    override fun getInflaterFunction(): ViewInflatingFunction<SimpleImageListItemBinding> = SimpleImageListItemBinding::inflate

    override fun renderFunction(view: SimpleImageListItemBinding,
                                model: Bitmap,
                                viewHolder: BaseViewHolderItem<SimpleImageListItemBinding>) {
        view.simpleImageListItemImage.setImageBitmap(model)
    }


}
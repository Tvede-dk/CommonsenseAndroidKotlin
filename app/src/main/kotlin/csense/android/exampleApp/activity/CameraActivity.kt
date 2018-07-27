package csense.android.exampleApp.activity


import com.commonsense.android.kotlin.system.extensions.*
import com.commonsense.android.kotlin.views.databinding.activities.*
import csense.android.exampleApp.databinding.*
import csense.android.exampleApp.fragments.*

/**
 * Created by Kasper Tvede on 10-07-2017.
 */
class CameraActivity : BaseDatabindingActivity<CameraActivityBinding>() {
    override fun createBinding(): InflaterFunctionSimple<CameraActivityBinding> = CameraActivityBinding::inflate


    override fun useBinding() {
        replaceFragment(binding.cameraActivityContent.id, CameraFragment())
    }

}
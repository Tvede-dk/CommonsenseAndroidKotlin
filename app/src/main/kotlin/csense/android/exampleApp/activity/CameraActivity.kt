package csense.android.exampleApp.activity

import com.CommonSenseAndroidKotlin.example.databinding.CameraActivityBinding
import csense.android.exampleApp.fragments.CameraFragment
import com.commonsense.android.kotlin.system.extensions.replaceFragment
import com.commonsense.android.kotlin.views.databinding.activities.BaseDatabindingActivity
import com.commonsense.android.kotlin.views.databinding.activities.InflaterFunctionSimple

/**
 * Created by Kasper Tvede on 10-07-2017.
 */
class CameraActivity : BaseDatabindingActivity<CameraActivityBinding>() {
    override fun createBinding(): InflaterFunctionSimple<CameraActivityBinding>
            = CameraActivityBinding::inflate


    override fun useBinding() {
        replaceFragment(binding.cameraActivityContent.id, CameraFragment())
    }

}
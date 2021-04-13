package csense.android.exampleApp.views.searchable

import com.commonsense.android.kotlin.system.extensions.replaceFragment
import com.commonsense.android.kotlin.views.databinding.activities.BaseDatabindingActivity
import com.commonsense.android.kotlin.views.databinding.activities.InflaterFunctionSimple
import csense.android.exampleApp.databinding.FullscreenFragmentBinding
import csense.android.exampleApp.fragments.CameraFragment
import csense.android.exampleApp.fragments.SearchAbleRecyclerDemo

class SearchableRecyclerDemoActivity : BaseDatabindingActivity<FullscreenFragmentBinding>() {
    override fun useBinding() {
        replaceFragment(binding.fullFrame.id, SearchAbleRecyclerDemo())
    }

    override fun createBinding(): InflaterFunctionSimple<FullscreenFragmentBinding> =
            FullscreenFragmentBinding::inflate

}
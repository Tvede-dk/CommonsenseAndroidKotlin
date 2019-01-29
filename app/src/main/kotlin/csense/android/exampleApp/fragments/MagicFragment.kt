package csense.android.exampleApp.fragments

import android.os.*
import android.view.*
import com.commonsense.android.kotlin.system.extensions.*
import com.commonsense.android.kotlin.views.baseClasses.*
import com.commonsense.android.kotlin.views.databinding.fragments.*
import csense.android.exampleApp.*
import csense.android.exampleApp.databinding.*
import kotlinx.coroutines.*

class MagicFragment : BaseDatabindingFragment<MagicFragmentViewBinding>() {

    override fun getInflater(): InflateBinding<MagicFragmentViewBinding> =
            MagicFragmentViewBinding::inflate

    private val adapter by lazy {
        BaseFragmentPagerAdapter(childFragmentManager)
    }

    override fun useBinding() {
//        launchInUi("") {
//            delay(1000)
            binding.pager.adapter = adapter
            adapter.addFragment(CameraFragment(), "camera")
            adapter.addFragment(CameraFragment(), "camera2")
            adapter.addFragment(CameraFragment(), "camera3")
            adapter.addFragment(CameraFragment(), "camera4")
//            childFragmentManager.transactionCommit {
//                replace(R.id.test2, CameraFragment())
//            }
//        }
    }
}
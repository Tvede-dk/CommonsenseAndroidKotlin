package csense.android.exampleApp.views.widgets.pagerAdapter

import android.graphics.*
import android.os.Bundle
import com.commonsense.android.kotlin.views.databinding.fragments.*
import csense.android.exampleApp.R
import csense.android.exampleApp.databinding.*
import java.util.*

class WidgetsFragmentExample : BaseDatabindingFragment<WidgetsPagerAdapterFragmentBinding>() {

    override fun getInflater(): InflateBinding<WidgetsPagerAdapterFragmentBinding> =
            WidgetsPagerAdapterFragmentBinding::inflate

    override fun useBinding() {
        val random = Random().nextInt(4)
        val color = when (random) {
            0 -> Color.RED
            1 -> Color.BLUE
            2 -> Color.GREEN
            else -> Color.GRAY
        }
        binding.widgetsPagerExampleFragmentBackground.setBackgroundColor(color)
        binding.widgetsPagerExampleFragmentTextCenter.setText(R.string.app_name)
    }

//    override fun onSafeDataAndBinding() {
//        val random = Random().nextInt(4)
//        val color = when (random) {
//            0 -> Color.RED
//            1 -> Color.BLUE
//            2 -> Color.GREEN
//            else -> Color.GRAY
//        }
//        binding.widgetsPagerExampleFragmentBackground.setBackgroundColor(color)
//        binding.widgetsPagerExampleFragmentTextCenter.text = data
//    }

}
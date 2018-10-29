package csense.android.exampleApp.views.widgets.pagerAdapter

import android.graphics.*
import com.commonsense.android.kotlin.views.databinding.fragments.*
import csense.android.exampleApp.databinding.*
import java.util.*

class WidgetsFragmentExample : BaseDatabindingFragmentWithData<WidgetsPagerAdapterFragmentBinding, String>() {

    override fun getInflater(): InflateBinding<WidgetsPagerAdapterFragmentBinding> =
            WidgetsPagerAdapterFragmentBinding::inflate

    override fun onSafeDataAndBinding() {
        val random = Random().nextInt(4)
        val color = when (random) {
            0 -> Color.RED
            1 -> Color.BLUE
            2 -> Color.GREEN
            else -> Color.GRAY
        }
        binding.widgetsPagerExampleFragmentBackground.setBackgroundColor(color)
        binding.widgetsPagerExampleFragmentTextCenter.text = data
    }

}
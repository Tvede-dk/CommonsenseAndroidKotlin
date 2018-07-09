package csense.android.exampleApp.widgetsExamples

import com.CommonSenseAndroidKotlin.example.databinding.MainWidgetExampleActivityBinding
import com.commonsense.android.kotlin.system.extensions.safeToast
import com.commonsense.android.kotlin.views.databinding.activities.BaseDatabindingActivity
import com.commonsense.android.kotlin.views.databinding.activities.InflaterFunctionSimple
import com.commonsense.android.kotlin.views.input.selection.SingleSelectionHandler
import com.commonsense.android.kotlin.views.input.selection.asToggleable

/**
 * Created by Kasper Tvede on 07-09-2017.
 */
class MainWidgetExampleActivity : BaseDatabindingActivity<MainWidgetExampleActivityBinding>() {

    override fun createBinding(): InflaterFunctionSimple<MainWidgetExampleActivityBinding>
            = MainWidgetExampleActivityBinding::inflate

    val radioHandler = SingleSelectionHandler<String>()

    val buttonHandler = SingleSelectionHandler<String>()

    override fun useBinding() {
        radioHandler += binding.radio1.asToggleable("test")
        radioHandler += binding.radio2.asToggleable("test2")
        radioHandler += binding.radio3.asToggleable("test3")
        radioHandler.callback = this::onChanged
        //the unselection will not work for radiobuttons as the does not allow it them selvs..
        //radioHandler.allowDeselection(this::onUnSelected)

        buttonHandler += binding.radio11.asToggleable("test11")
        buttonHandler += binding.radio22.asToggleable("test22")
        buttonHandler += binding.radio33.asToggleable("test33")
        buttonHandler.callback = this::onChanged
        buttonHandler.allowDeselection(this::onUnSelected)

    }


    fun onChanged(value: String) {
        safeToast(value)
    }

    fun onUnSelected() {
        safeToast("<nothing !!>")
    }
}
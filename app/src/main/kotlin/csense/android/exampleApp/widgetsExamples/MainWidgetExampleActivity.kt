package csense.android.exampleApp.widgetsExamples

import com.commonsense.android.kotlin.system.extensions.*
import com.commonsense.android.kotlin.views.databinding.activities.*
import com.commonsense.android.kotlin.views.input.selection.*
import csense.android.exampleApp.databinding.*

/**
 * Created by Kasper Tvede on 07-09-2017.
 */
class MainWidgetExampleActivity : BaseDatabindingActivity<MainWidgetExampleActivityBinding>() {

    override fun createBinding(): InflaterFunctionSimple<MainWidgetExampleActivityBinding> =
            MainWidgetExampleActivityBinding::inflate

    val radioHandler = SingleSelectionHandler(this::onChanged)
    val buttonHandler = SingleSelectionHandler(this::onChanged)

    override fun useBinding() {
        radioHandler += binding.radio1.asToggleable("test")
        radioHandler += binding.radio2.asToggleable("test2")
        radioHandler += binding.radio3.asToggleable("test3")

        buttonHandler += binding.radio11.asToggleable("test11")
        buttonHandler += binding.radio22.asToggleable("test22")
        buttonHandler += binding.radio33.asToggleable("test33")
        buttonHandler.allowDeselection(this::onDeselect)

    }

    fun onChanged(value: String) {
        safeToast(value)
    }

    fun onDeselect() {
        safeToast("<nothing selected>")
    }
}
package csense.android.exampleApp.views.dataAware

import android.os.*
import com.commonsense.android.kotlin.system.logging.*
import com.commonsense.android.kotlin.views.databinding.activities.*
import csense.android.exampleApp.databinding.*

enum class Test2 {
    A, B
}

class DataBActivity : BaseDatabindingActivityWithData<DataBActivityBinding, Test2?>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        L.error("test", "omg")
    }

    override fun useBinding() {
//        somebutton.setonclickasync {
//            startActivityWithData(DataBActivity::class, Test2.B, 849, Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK, null)`
//        }

//        binding.

        binding.dataBActivityText.text = data.toString()
    }


    override fun createBinding(): InflaterFunctionSimple<DataBActivityBinding> =
            DataBActivityBinding::inflate

}
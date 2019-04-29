package csense.android.exampleApp.views.dataAware

import android.content.*
import com.commonsense.android.kotlin.system.base.*
import com.commonsense.android.kotlin.views.databinding.activities.*
import com.commonsense.android.kotlin.views.extensions.*
import csense.android.exampleApp.databinding.*

class DataAActivity : BaseDatabindingActivity<DataAActivityBinding>() {
    override fun useBinding() {
        binding.dataAActivityButton.setOnclickAsync {
            startActivityWithData(DataBActivity::class, null, 849, Intent.FLAG_ACTIVITY_CLEAR_TASK, null)
        }
    }

    override fun createBinding(): InflaterFunctionSimple<DataAActivityBinding> =
            DataAActivityBinding::inflate


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

    }

}
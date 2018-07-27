package csense.android.exampleApp.activity

import com.commonsense.android.kotlin.views.databinding.activities.*
import csense.android.exampleApp.databinding.*

/**
 * Created by Kasper Tvede on 14-07-2017.
 */
class TodoExampleActivity : BaseDatabindingActivity<TodoActivityBinding>() {
    override fun createBinding(): InflaterFunctionSimple<TodoActivityBinding> = TodoActivityBinding::inflate

    override fun useBinding() {
    }

}
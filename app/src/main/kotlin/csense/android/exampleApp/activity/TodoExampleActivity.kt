package csense.android.exampleApp.activity

import com.CommonSenseAndroidKotlin.example.databinding.TodoActivityBinding
import com.commonsense.android.kotlin.views.databinding.activities.BaseDatabindingActivity
import com.commonsense.android.kotlin.views.databinding.activities.InflaterFunctionSimple

/**
 * Created by Kasper Tvede on 14-07-2017.
 */
class TodoExampleActivity : BaseDatabindingActivity<TodoActivityBinding>() {
    override fun createBinding(): InflaterFunctionSimple<TodoActivityBinding> = TodoActivityBinding::inflate

    override fun useBinding() {
    }

}
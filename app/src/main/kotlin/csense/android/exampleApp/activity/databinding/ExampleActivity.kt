package csense.android.exampleApp.activity.databinding

import android.app.*
import android.os.*
import csense.android.exampleApp.databinding.*

/**
 * Example's usage
 */
class ExampleActivity : Activity() {
    private var binding: ExampleActivityBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ExampleActivityBinding.inflate(layoutInflater).also {
            setContentView(it.root)
        }
        //use binding here
        binding?.exampleActivityTextview?.text = "example"
    }
}


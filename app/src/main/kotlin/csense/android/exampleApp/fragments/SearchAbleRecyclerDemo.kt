package csense.android.exampleApp.fragments

import android.graphics.Color
import android.support.v7.widget.LinearLayoutManager
import android.text.Editable
import android.text.TextWatcher
import com.CommonSenseAndroidKotlin.example.databinding.DemoRecyclerSearchableBinding
import com.CommonSenseAndroidKotlin.example.databinding.SimpleListItemBinding
import com.commonsense.android.kotlin.base.extensions.collections.repeateToSize
import com.commonsense.android.kotlin.views.databinding.adapters.BaseSearchableDataBindingRecyclerAdapter
import com.commonsense.android.kotlin.views.databinding.adapters.IRenderModelSearchItem
import com.commonsense.android.kotlin.views.databinding.adapters.toSearchable
import com.commonsense.android.kotlin.views.databinding.fragments.BaseDatabindingFragment
import com.commonsense.android.kotlin.views.databinding.fragments.InflateBinding
import com.commonsense.android.kotlin.views.extensions.setup

/**
 * Created by kasper on 01/06/2017.
 */

class SearchAbleSimpleListItemRender(item: String) : SimpleListItemRender(item, 0, {}), IRenderModelSearchItem<String, SimpleListItemBinding, String> {
    override fun isAcceptedByFilter(value: String): Boolean = item.contains(value)
}

class SearchAbleRecyclerDemo : BaseDatabindingFragment<DemoRecyclerSearchableBinding>() {
    override fun getInflater(): InflateBinding<DemoRecyclerSearchableBinding>
            = DemoRecyclerSearchableBinding::inflate

    private val adapter by lazy {
        context?.let { BaseSearchableDataBindingRecyclerAdapter<String>(it) }
    }

    override fun useBinding() {
        val adapter = adapter ?: return
        val items = mutableListOf<IRenderModelSearchItem<*, *, String>>(
                SearchAbleSimpleListItemRender("First []"),
                SimpleListImageItemRender(Color.BLUE, 0).toSearchable { _, _ -> false },
                SearchAbleSimpleListItemRender("Whats up "),
                SimpleListImageItemRender(Color.RED, 0).toSearchable { _, _ -> false }
        ).repeateToSize(50000)

        adapter.setSection(items, 0)
        binding.demoRecyclerSearchableRecyclerview.recyclerView.setup(adapter, LinearLayoutManager(context))
        binding.demoRecyclerSearchableRecyclerview2.recyclerView.setup(adapter, LinearLayoutManager(context))

        binding.demoRecyclerSearchableEdit.addTextChangedListener(SafeTextWatcher(this::performFilter))
    }

    fun performFilter(s: Editable) {
        val adapter = adapter ?: return
        val temp = s.toString()
        if (temp.isEmpty()) {
            adapter.removeFilter()
        } else {
            temp.let(adapter::filterBy)
        }

    }
}

class SafeTextWatcher(private val onAfterTextChanged: (Editable) -> Unit) : TextWatcher {

    override fun afterTextChanged(afterChanged: Editable?) {
        if (afterChanged != null) {
            this.onAfterTextChanged(afterChanged)
        }
    }

    override fun beforeTextChanged(changedText: CharSequence?, p1: Int, p2: Int, p3: Int) {

    }

    override fun onTextChanged(afterChanged: CharSequence?, p1: Int, p2: Int, p3: Int) {

    }
}
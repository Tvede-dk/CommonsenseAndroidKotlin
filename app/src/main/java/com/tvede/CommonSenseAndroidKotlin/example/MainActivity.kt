package com.tvede.CommonSenseAndroidKotlin.example


import android.support.annotation.DrawableRes
import android.view.LayoutInflater
import com.commonsense.android.kotlin.baseClasses.databinding.BaseDataBindingAdapter
import com.commonsense.android.kotlin.baseClasses.databinding.BaseDatabindingActivity
import com.tvede.CommonSenseAndroidKotlin.example.databinding.ActivityMainBinding
import com.tvede.CommonSenseAndroidKotlin.example.databinding.SimpleListImageItemBinding
import com.tvede.CommonSenseAndroidKotlin.example.databinding.SimpleListItemBinding


class MainActivity : BaseDatabindingActivity<ActivityMainBinding>() {
    override fun useBinding() {
        binding.mainTitle.setText(R.string.testTitle)
        val adapter = BaseDataBindingAdapter(this)
//        adapter.add(BaseAdapterItemBindingFunc(R.layout.simple_list_item, SimpleListItemBinding::class.java, {
//            it.useData("asd")
//        }))
//        adapter.add(BaseAdapterItemBindingFunc(R.layout.simple_list_image_item, SimpleListImageItemBinding::class.java, {
//            it.useImage(R.mipmap.ic_launcher)
//        }))
//        adapter.add(BaseAdapterItemBindingFunc(R.layout.simple_list_item, SimpleListItemBinding::class.java, {
//            it.useData("asd")
//        }))
        binding.mainList.adapter = adapter
    }

    override fun createBinding(inflater: LayoutInflater) = ActivityMainBinding.inflate(inflater)


}

fun SimpleListItemBinding.useData(data: String) {
    simpleListText.text = data
}

fun SimpleListImageItemBinding.useImage(@DrawableRes image: Int) {
    simpleListItemImageImage.setImageResource(image)
}




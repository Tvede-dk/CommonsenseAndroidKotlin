package com.CommonSenseAndroidKotlin.example.models

import android.databinding.ObservableField
import com.CommonSenseAndroidKotlin.example.databinding.UserViewBinding
import com.commonsense.android.kotlin.views.ViewInflatingFunction
import com.commonsense.android.kotlin.views.databinding.adapters.BaseSearchRenderModel
import com.commonsense.android.kotlin.views.databinding.adapters.BaseViewHolderItem

/**
 * Created by Kasper Tvede on 11-06-2017.
 */
data class User(
        val Username: String,
        val password: String,
        val email: String,
        val realName: String)


class UserListItemRender<in F : Any>(item: User) : BaseSearchRenderModel<User, UserViewBinding, F>(item, UserViewBinding::class.java) {
    override fun renderFunction(view: UserViewBinding, model: User, viewHolder: BaseViewHolderItem<UserViewBinding>) {
    }

    override fun isAcceptedByFilter(value: F): Boolean {
        //TODO impl me.
        return false
    }

    override fun getInflaterFunction(): ViewInflatingFunction<UserViewBinding> = UserViewBinding::inflate
}

class UserViewModel {
    val username = ObservableField<String>()

    val password = ObservableField<String>()

    val email = ObservableField<String>()

    val realName = ObservableField<String>()

}

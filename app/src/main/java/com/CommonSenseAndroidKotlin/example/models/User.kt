package com.CommonSenseAndroidKotlin.example.models

import android.databinding.ObservableField
import com.CommonSenseAndroidKotlin.example.databinding.UserViewBinding
import com.commonsense.android.kotlin.baseClasses.databinding.BaseSearchRenderModel
import com.commonsense.android.kotlin.baseClasses.databinding.ViewInflatingFunction

/**
 * Created by Kasper Tvede on 11-06-2017.
 */
data class User(
        val Username: String,
        val password: String,
        val email: String,
        val realName: String)


class UserListItemRender<F : Any>(item: User) : BaseSearchRenderModel<User, UserViewBinding, F>(item, UserViewBinding::class.java) {


    override fun isAcceptedByFilter(value: F): Boolean {
        //TODO impl me.
        return false
    }

    override fun getInflaterFunction(): ViewInflatingFunction<UserViewBinding> = UserViewBinding::inflate

    override fun renderFunction(view: UserViewBinding, model: User) {
        //TODO impl me.
    }
}

class UserViewModel {
    val Username = ObservableField<String>()

    val password = ObservableField<String>()

    val email = ObservableField<String>()

    val realName = ObservableField<String>()

}
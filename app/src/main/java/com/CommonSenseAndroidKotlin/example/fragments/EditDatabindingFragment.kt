package com.CommonSenseAndroidKotlin.example.fragments

import com.CommonSenseAndroidKotlin.example.databinding.UserExampleViewBinding
import com.CommonSenseAndroidKotlin.example.models.User
import com.CommonSenseAndroidKotlin.example.models.UserViewModel
import com.commonsense.android.kotlin.android.extensions.widets.setOnclickAsync
import com.commonsense.android.kotlin.baseClasses.databinding.BaseDatabindingFragment
import com.commonsense.android.kotlin.baseClasses.databinding.InflateBinding

/**
 * Created by Kasper Tvede on 11-06-2017.
 */
class EditDatabindingFragment : BaseDatabindingFragment<UserExampleViewBinding>() {

    val user = User("", "", "", "")

    override fun getInflater(): InflateBinding<UserExampleViewBinding>
            = UserExampleViewBinding::inflate

    override fun useBinding() {
        binding.userExampleItemView?.user = user
        binding.userExampleEditView?.user = user.toViewModel()
        binding.userExampleUpdateBtn.setOnclickAsync {
            binding.userExampleEditView?.user?.toModel().let {
                binding.userExampleItemView?.user = it
            }
        }
    }

}

private fun UserViewModel.toModel(): User {
    return User(this.Username.get(), this.email.get(), this.password.get(), this.realName.get())
}

private fun User.toViewModel(): UserViewModel {
    return UserViewModel().also {
        it.Username.set(this.Username)
        it.email.set(this.email)
        it.password.set(this.password)
        it.realName.set(this.realName)
    }
}

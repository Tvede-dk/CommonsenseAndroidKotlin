package csense.android.exampleApp.models

import android.databinding.*
import com.commonsense.android.kotlin.views.*
import com.commonsense.android.kotlin.views.databinding.adapters.*
import csense.android.exampleApp.databinding.*

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

package com.commonsense.android.kotlin.system.base.helpers

import android.app.*
import android.content.*
import android.os.*
import com.commonsense.android.kotlin.base.extensions.collections.*
import com.commonsense.android.kotlin.system.base.*
import com.commonsense.android.kotlin.system.dataFlow.*
import com.commonsense.android.kotlin.system.logging.*
import kotlin.reflect.*

/**
 * Created by Kasper Tvede on 23-07-2017.
 * Incapsulate the fact that an activity that has a required input needs this , strongly typed and "safely";
 * safely means: since the parcelable "flow" is twistly broken (theres a buffer of approx 1 MB in android
 * which is used for all parcelable elements, if this buffer is filled, your app gets killed), we are to
 * use another strategy. so we resort to reference counting and a very limited ability to reference / deference
 * the data outside of the controlled classes ( BaseActvity and BaseActivityData).
 * This makes sure that you as the developer should not "worry" or be able to "fuck up", the flow / usage
 * of data.
 *
 */
abstract class BaseActivityData<out InputType> : BaseActivity() {

    /**
     * Unfortunately we are unable to Type safe bypass the map though this. not in this generic manner
     */
    @Suppress("UNCHECKED_CAST")
    val data: InputType
        get() {
            val safeIntent = intent ?: throw RuntimeException("intent is null")
            val intentIndex = safeIntent.getStringExtra(dataIntentIndex)
                    ?: throw RuntimeException("Intent content not presented; extra is: ${safeIntent.extras}")
            val item = BaseActivityData.dataReferenceMap.getItemOr(intentIndex)
                    ?: throw RuntimeException("Data is not in map, so this activity is " +
                            "\"${this.javaClass.simpleName}\" referring to the data after closing.")
            return item as InputType
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!haveRequiredIndex()) { // bad start. skip it.
            beforeCloseOnBadData() //hookpoint for users who needs to do "something" before the activity is killed.
            setResult(Activity.RESULT_CANCELED)
            finish()
        } else {
            onSafeData()
        }
    }

    /**
     * This will be called after oncreate, iff and only iff the data is valid and accessible.
     *
     */
    abstract fun onSafeData()

    /**
     * run'ed before we kill this activity
     */
    open fun beforeCloseOnBadData() {
        logError("required data for this activity not presented, please make sure you provide it;")
        val isBadCall = intent?.getStringExtra(dataIntentIndex).isNullOrBlank()
        isBadCall.ifTrue {
            logError("You properly called this without the data index;" +
                    " use the \"startActivityWithData\" method, that takes care of it")

        }


    }

    /**
     * Verifies that we have the data we expected (the index)
     */
    private fun haveRequiredIndex(): Boolean {
        val index = dataIndex ?: return false
        return BaseActivityData.dataReferenceMap.hasItem(index)
    }


    private inline val dataIndex: String?
        get() = intent?.getStringExtra(dataIntentIndex)


    companion object {
        internal val dataIntentIndex = "baseActivity-data-index"
        internal val dataReferenceMap = ReferenceCountingMap()

        fun <Input, T : BaseActivityData<Input>> createDataActivityIntent(context: Context,
                                                                          activity: KClass<T>,
                                                                          data: Input)
                : IntentAndDataIndex =
                createDataActivityIntent(context, activity.java, data)

        fun <Input, T : BaseActivityData<Input>> createDataActivityIntent(context: Context,
                                                                          activityToStart: Class<T>,
                                                                          data: Input)
                : IntentAndDataIndex {
            val index = BaseActivityData.dataReferenceMap.count.toString()
            val intent = Intent(context, activityToStart).apply {
                BaseActivityData.dataReferenceMap.addItem(data, index)
                putExtra(BaseActivityData.dataIntentIndex, index)
            }
            return IntentAndDataIndex(intent, index)
        }
    }
}

data class IntentAndDataIndex(val intent: Intent, val index: String)

fun <Input, T : BaseActivityData<Input>>
        BaseActivity.startActivityWithData(activity: KClass<T>,
                                           data: Input,
                                           requestCode: Int,
                                           optOnResult: AsyncActivityResultCallback?) {
    startActivityWithData(activity.java, data, requestCode, optOnResult)
}

fun <Input, T : BaseActivityData<Input>>
        BaseActivity.startActivityWithData(activity: Class<T>,
                                           data: Input,
                                           requestCode: Int,
                                           optOnResult: AsyncActivityResultCallback?) {

    val intentAndDataIndex = BaseActivityData.createDataActivityIntent(
            this,
            activity,
            data)
    startActivityForResultAsync(
            intentAndDataIndex.intent,
            null,
            requestCode,
            { resultCode: Int, resultIntent: Intent? ->
                BaseActivityData.dataReferenceMap.decrementCounter(intentAndDataIndex.index)
                optOnResult?.invoke(resultCode, resultIntent)
            })
}


fun <Input, T : BaseActivityData<Input>>
        BaseFragment.startActivityWithData(activity: KClass<T>,
                                           data: Input,
                                           requestCode: Int,
                                           optOnResult: AsyncActivityResultCallback?) {
    baseActivity?.startActivityWithData(activity, data, requestCode, optOnResult)
}


fun <Input, T : BaseActivityData<Input>>
        BaseFragment.startActivityWithData(activity: Class<T>,
                                           data: Input,
                                           requestCode: Int,
                                           optOnResult: AsyncActivityResultCallback?) {
    baseActivity?.startActivityWithData(activity, data, requestCode, optOnResult)
}


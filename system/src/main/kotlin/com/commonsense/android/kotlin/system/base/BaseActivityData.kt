package com.commonsense.android.kotlin.system.base

import android.app.*
import android.content.*
import android.os.*
import com.commonsense.android.kotlin.base.extensions.collections.*
import com.commonsense.android.kotlin.system.base.helpers.*
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
     * The data associated with this activity. it will be accessible when the activity have started. (onSafeData) and onwards
     */
    val data: InputType
        get() {
            val safeIntent = intent ?: throw RuntimeException("intent is null")
            val intentIndex = safeIntent.getStringExtra(dataIntentIndex)
                    ?: throw RuntimeException("Intent content not presented; extra is: ${safeIntent.extras}")
            val item = dataReferenceMap.getItemOr(intentIndex)
                    ?: throw RuntimeException("Data is not in map, so this activity is " +
                            "\"${this.javaClass.simpleName}\" referring to the data after closing.")
            @Suppress("UNCHECKED_CAST") // Unfortunately we are unable to Type safe bypass the map though this. not in this generic manner
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
     * This will be called after onCreate, iff and only iff the data is valid and accessible.
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
     * @return Boolean true if we have the index and the item
     */
    private fun haveRequiredIndex(): Boolean {
        val index = dataIndex ?: return false
        return dataReferenceMap.hasItem(index)
    }

    /**
     * Retrives the data index from the intent or returns null
     */
    private inline val dataIndex: String?
        get() = intent?.getStringExtra(dataIntentIndex)


    companion object {
        internal val dataIntentIndex = "baseActivity-data-index"
        internal val dataReferenceMap = ReferenceCountingMap()

        /**
         *
         * @param context Context
         * @param activity KClass<T>
         * @param flags Int
         * @param data Input
         * @return IntentAndDataIndex
         */
        fun <Input, T : BaseActivityData<Input>> createDataActivityIntent(context: Context,
                                                                          activity: KClass<T>,
                                                                          flags: Int? = null,
                                                                          data: Input)
                : IntentAndDataIndex =
                createDataActivityIntent(context, activity.java, flags, data)

        /**
         *
         * @param context Context
         * @param activityToStart Class<T>
         * @param flags Int
         * @param data Input
         * @return IntentAndDataIndex
         */
        fun <Input, T : BaseActivityData<Input>> createDataActivityIntent(context: Context,
                                                                          activityToStart: Class<T>,
                                                                          flags: Int? = null,
                                                                          data: Input)
                : IntentAndDataIndex {
            val index = dataReferenceMap.count.toString()
            val intent = Intent(context, activityToStart).apply {
                dataReferenceMap.addItem(data, index)
                putExtra(dataIntentIndex, index)
                flags?.let {
                    this.flags = it
                }
            }
            return IntentAndDataIndex(intent, index)
        }
    }
}

/**
 * Describes the Intent and the index for the data
 * @property intent Intent
 * @property index String
 */
data class IntentAndDataIndex(val intent: Intent, val index: String)

/**
 * Starts the given activity with data with the provided data and so on

 * @receiver BaseActivity
 * @param activity KClass<T>
 * @param data Input
 * @param requestCode Int
 * @param flags Int the regular activity flags. these will be set.
 * @param optOnResult AsyncActivityResultCallback?
 */
fun <Input, T : BaseActivityData<Input>>
        BaseActivity.startActivityWithData(activity: KClass<T>,
                                           data: Input,
                                           requestCode: Int,
                                           flags: Int? = null,
                                           optOnResult: AsyncActivityResultCallback?) {
    startActivityWithData(activity.java, data, requestCode, flags, optOnResult)
}

/**
 *
 * @receiver BaseFragment
 * @param activity KClass<T>
 * @param data Input
 * @param requestCode Int
 * @param optOnResult AsyncActivityResultCallback?
 */
fun <Input, T : BaseActivityData<Input>>
        BaseFragment.startActivityWithData(activity: KClass<T>,
                                           data: Input,
                                           requestCode: Int,
                                           flags: Int? = null,
                                           optOnResult: AsyncActivityResultCallback?) {
    baseActivity?.startActivityWithData(activity, data, requestCode, flags, optOnResult)
}

/**
 *
 * @receiver BaseFragment
 * @param activity Class<T>
 * @param data Input
 * @param requestCode Int
 * @param optOnResult AsyncActivityResultCallback?
 */
fun <Input, T : BaseActivityData<Input>>
        BaseFragment.startActivityWithData(activity: Class<T>,
                                           data: Input,
                                           requestCode: Int,
                                           flags: Int = 0,
                                           optOnResult: AsyncActivityResultCallback?) {
    baseActivity?.startActivityWithData(activity, data, requestCode, flags, optOnResult)
}


/**
 *
 * @receiver BaseActivity
 * @param activity Class<T>
 * @param data Input
 * @param requestCode Int
 * @param optOnResult AsyncActivityResultCallback?
 */
fun <Input, T : BaseActivityData<Input>>
        BaseActivity.startActivityWithData(activity: Class<T>,
                                           data: Input,
                                           requestCode: Int,
                                           flags: Int? = null,
                                           optOnResult: AsyncActivityResultCallback?) {

    val intentAndDataIndex = BaseActivityData.createDataActivityIntent(
            this,
            activity,
            flags,
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
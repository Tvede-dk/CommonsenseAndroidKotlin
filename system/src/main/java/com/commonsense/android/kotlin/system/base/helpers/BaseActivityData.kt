package com.commonsense.android.kotlin.system.base.helpers

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.commonsense.android.kotlin.base.extensions.collections.ifTrue
import com.commonsense.android.kotlin.system.base.BaseActivity
import com.commonsense.android.kotlin.system.base.BaseFragment
import com.commonsense.android.kotlin.system.dataFlow.ReferenceCountingMap
import com.commonsense.android.kotlin.system.logging.logError
import kotlin.reflect.KClass

/**
 * Created by Kasper Tvede on 23-07-2017.
 * Encapsulate the fact that an activity that has a required input needs this , strongly typed and "safely";
 * safely means: since the parcelable "flow" is broken (there is a buffer of approx 1 MB in android
 * which is used for all parcelable elements, if this buffer is filled, your app gets killed), we are to
 * use another strategy. so we resort to reference counting and a very limited ability to reference / deference
 * the data outside of the controlled classes ( BaseActivity and BaseActivityData).
 * This makes sure that you as the developer should not "worry" or be able to "fuck up", the flow / usage
 * of data.
 *
 */
open class BaseActivityData<out InputType> : BaseActivity() {

    /**
     * the data we are supplied when we got launched.
     */
    val data: InputType
        get() {
            val item = BaseActivityData.dataReferenceMap.getItemOr(dataIndex)
                    ?: throw RuntimeException("Data is not in map, so this activity is " +
                            "\"${this.javaClass.simpleName}\" referring to the data after closing." +
                            "\n did you use \"data\" in onDestroy ?")
            //Unfortunately we are unable to Type safe bypass the map though this. not in this generic manner
            @Suppress("UNCHECKED_CAST")
            return item as InputType
        }

    /**
     *
     */
    private val dataIndex: String
        get() {
            val safeIntent = intent ?: throw RuntimeException("intent is null")
            return safeIntent.getStringExtra(dataIntentIndex)
                    ?: throw RuntimeException("Intent content not presented; extra is: ${safeIntent.extras}")
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!haveRequiredIndex()) { // bad start. skip it.
            beforeCloseOnBadData() //hook point for users who needs to do "something" before the activity is killed.
            setResult(Activity.RESULT_CANCELED)
            finish()
        }
    }

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

    override fun onDestroy() {
        if (isFinishing) {
            cleanUpActivityWithDataMap(dataIndex)
        }
        super.onDestroy()

    }

    /**
     * Verifies that we have the data we expected (the index)
     */
    private fun haveRequiredIndex(): Boolean =
            intent != null && !intent.getStringExtra(dataIntentIndex).isNullOrBlank()

    /**
     * internal such that the ActivityWithData can get these.
     */
    internal companion object {
        //the data intent index containing the index in the map.
        internal const val dataIntentIndex = "baseActivity-data-index"

        //shared storage.
        internal val dataReferenceMap = ReferenceCountingMap()
    }
}


//TODO should these be here ?

//there are 2 cases we solve, and 2 we do not solve;
/**
 * We solve the
 * [ctx -> Base ]
 * [base -> Base ]
 * but not the following
 * [ctx -> ctx]     } would require a generic non overriding solution which is impossible.
 * [base -> ctx]    } would mean that a generic activity or alike, would understand the data store,
 *                      which requires opening it up, but also for the user to copy +- the implementation
 *                         in BaseActivityData, which seem rather orthodox
 *
 *
 */

internal fun BaseActivity.cleanUpActivityWithDataMap(index: String) {
    BaseActivityData.dataReferenceMap.decrementCounter(index)
}

/**
 * Starts a given BaseDataActivity with the required /supplied data.
 *
 *
 *  this is the [ctx -> BaseDataActivity] case
 */
fun <Input, T : BaseActivityData<Input>> Context.startActivityWithData(
        activity: Class<T>,
        data: Input,
        flags: Int = 0) {
    val intent = Intent(this, activity)
    val index = BaseActivityData.dataReferenceMap.count.toString()
    BaseActivityData.dataReferenceMap.addItem(data, index)
    intent.putExtra(BaseActivityData.dataIntentIndex, index)
    startActivity(intent)
}

fun <Input, T : BaseActivityData<Input>> Context.startActivityWithData(
        activity: KClass<T>,
        data: Input,
        flags: Int = 0) {
    startActivityWithData(activity.java, data)
}

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
    val intent = Intent(this, activity)
    val index = BaseActivityData.dataReferenceMap.count.toString()
    BaseActivityData.dataReferenceMap.addItem(data, index)
    intent.putExtra(BaseActivityData.dataIntentIndex, index)
    startActivityForResultAsync(intent, null, requestCode, { resultCode, resultIntent ->
        BaseActivityData.dataReferenceMap.decrementCounter(index)
        optOnResult?.invoke(resultCode, resultIntent)
    })
}


//TODO USE THE FOLLOWING PASSAGE FROM andorid dev doc:

/*
https://developer.android.com/guide/components/activities/activity-lifecycle.html#ondestroy

onDestroy()
Called before the activity is destroyed.
 This is the final call that the activity receives.
 The system either invokes this callback because the activity is finishing due to someone's calling finish(),
  or because the system is temporarily destroying the process containing the activity to save space.
   You can distinguish between these two scenarios with the isFinishing() method.
    The system may also call this method when an orientation change occurs, and then immediately call onCreate()
     to recreate the process (and the components that it contains) in the new orientation.

The onDestroy() callback releases all resources that have not yet been released by earlier callbacks such as onStop()

 */



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
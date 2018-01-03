package com.commonsense.android.kotlin.system.base.helpers

import android.app.Activity
import android.os.Bundle
import com.commonsense.android.kotlin.base.extensions.collections.ifTrue
import com.commonsense.android.kotlin.system.base.BaseActivity
import com.commonsense.android.kotlin.system.logging.logError

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
open class BaseActivityData<out InputType> : BaseActivity() {

    /**
     * Unfortunately we are unable to Type safe bypass the map though this. not in this generic manner
     */
    @Suppress("UNCHECKED_CAST")
    val data: InputType
        get() {
            val safeIntent = intent ?: throw RuntimeException("intent is null")
            val intentIndex = safeIntent.getStringExtra(dataIntentIndex)
                    ?: throw RuntimeException("Intent content not presented; extra is: ${safeIntent.extras}")
            val item = BaseActivity.dataReferenceMap.getItemOr(intentIndex)
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

    /**
     * Verifies that we have the data we expected (the index)
     */
    private fun haveRequiredIndex(): Boolean =
            intent != null && !intent.getStringExtra(dataIntentIndex).isNullOrBlank()
}
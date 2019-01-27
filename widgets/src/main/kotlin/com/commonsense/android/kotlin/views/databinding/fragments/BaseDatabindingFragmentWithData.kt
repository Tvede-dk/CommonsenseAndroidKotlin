package com.commonsense.android.kotlin.views.databinding.fragments

import android.content.*
import android.databinding.*
import android.os.*
import com.commonsense.android.kotlin.base.extensions.collections.*
import com.commonsense.android.kotlin.system.base.*
import com.commonsense.android.kotlin.system.dataFlow.*
import com.commonsense.android.kotlin.system.extensions.*
import com.commonsense.android.kotlin.system.logging.*

abstract class BaseDatabindingFragmentWithData<
        ViewType : ViewDataBinding,
        DataType> : BaseDatabindingFragment<ViewType>() {

    /**
     * Handles the interaction with intent(s) and data.
     * in the future this will work even when android performs "empty process".
     */
    private val dataHandler: IntentDataAble<DataType> = IntentDataAble()
    /**
     * The data associated with this activity. it will be accessible when the activity have started. (onSafeData) and onwards
     */
    val data: DataType
        get() = dataHandler.getData(arguments)


    override fun useBinding() {
        if (!dataHandler.haveRequiredIndex(arguments)) {
            beforeCloseOnBadData() //hookpoint for users who needs to do "something" before the activity is killed.
            popThisFragment()
            return
        } else {
            onSafeDataAndBinding()
        }
    }


    /**
     * run'ed before we kill this activity
     */
    open fun beforeCloseOnBadData() {
        logError("required data for this activity not presented, please make sure you provide it;")
        val isBadCall = dataHandler.getDataIndex(arguments).isNullOrBlank()
        isBadCall.ifTrue {
            logError("You properly called this without the data index;" +
                    " use the \"startActivityWithData\" method, that takes care of it")
        }
    }

    abstract fun onSafeDataAndBinding()

    companion object {
        internal const val dataIntentIndex = "baseFragment-data-index"
        internal val dataReferenceMap = ReferenceCountingMap()
    }

}

fun <T> BaseDatabindingFragmentWithData<*, T>.setData(data: T) {
    val index = BaseDatabindingFragmentWithData.dataReferenceMap.count.toString()
    arguments = Bundle().apply {
        BaseDatabindingFragmentWithData.dataReferenceMap.addItem(data, index)
        putString(BaseDatabindingFragmentWithData.dataIntentIndex,index)
    }

}
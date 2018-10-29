package com.commonsense.android.kotlin.system.base

import android.os.*

/**
 * Handles the data aware part of an BaseActivityData or BaseFragmentData
 * @param InputType
 * @property data InputType
 * @property intent Intent?
 * @property dataIndex String?
 */
class IntentDataAble<InputType> {
    /**
     * The data associated with this activity. it will be accessible when the activity have started. (onSafeData) and onwards
     */
    fun getData(intent: Bundle?): InputType {
        val safeIntent = intent ?: throw Exception("intent is null")
        val intentIndex = safeIntent.getString(BaseActivityData.dataIntentIndex)
                ?: throw Exception("Intent content not presented; extra is: $safeIntent")
        val item = BaseActivityData.dataReferenceMap.getItemOr(intentIndex)
                ?: throw Exception("Data is not in map, so this activity/fragment " +
                        "\"${this.javaClass.simpleName}\" is referring to the data after closing.")
        @Suppress("UNCHECKED_CAST") // Unfortunately we are unable to Type safe bypass the map though this. not in this generic manner
        return item as InputType
    }


    /**
     * Verifies that we have the data we expected (the index)
     * @return Boolean true if we have the index and the item
     */
    fun haveRequiredIndex(intent: Bundle?): Boolean {
        val index = getDataIndex(intent) ?: return false
        return BaseActivityData.dataReferenceMap.hasItem(index)
    }

    /**
     * Retrives the data index from the intent or returns null
     */
    fun getDataIndex(intent: Bundle?): String? =
            intent?.getString(BaseActivityData.dataIntentIndex)
}

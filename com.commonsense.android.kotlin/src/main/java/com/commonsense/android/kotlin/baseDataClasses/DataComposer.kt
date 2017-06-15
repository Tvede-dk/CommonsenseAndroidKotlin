package com.commonsense.android.kotlin.baseDataClasses

import android.support.annotation.AnyThread
import com.commonsense.android.kotlin.patterns.ObserverPattern
import com.commonsense.android.kotlin.patterns.callback
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch

/**
 * Created by Kasper Tvede on 30-05-2017.
 */


class DataComposer<T, U> : callback<T> {


    private val startRetriveData: suspend () -> T

    private val transformData: (fromServer: T) -> U


    private val onUpdateUI: (data: U, didChange: Boolean) -> Unit

    private var oldData: T? = null

    constructor(startRetriveData: suspend () -> T,
                transformData: (T) -> U,
                onUpdateUI: (U, Boolean) -> Unit) {
        this.startRetriveData = startRetriveData
        this.transformData = transformData
        this.onUpdateUI = onUpdateUI
    }


    @AnyThread
    fun load() = launch(CommonPool) {
        loadAsync()
    }

    @AnyThread
    suspend fun loadAsync() {
        val fromServer = startRetriveData()
        onLoadedFromServer(fromServer)
    }


    fun observeFor(observableCollection: ObserverPattern<T>) {
        observableCollection.addListener(this)
        //register for the collection to our self.
    }

    fun stopObserving(observableCollection: ObserverPattern<T>) {
        observableCollection.removeListener(this)
    }

    /**
     * register this data composer to a lifecycle.
     */
    fun registerToLifeCycle(observableCollection: Any) {

    }

    //callback from registered listener
    override fun invoke(newData: T) {
        onLoadedFromServer(newData)
    }

    private fun onLoadedFromServer(fromServer: T) = launch(CommonPool) {
        val transformed = transformData(fromServer)
        val isEqual = fromServer == oldData
        oldData = fromServer
        updateUiWith(transformed, isEqual)
    }

    private fun updateUiWith(transformed: U, isEqual: Boolean) = launch(UI) {
        onUpdateUI(transformed, !isEqual)
    }


}
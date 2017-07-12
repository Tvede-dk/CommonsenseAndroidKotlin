package com.commonsense.android.kotlin.baseDataClasses

import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.async

/**
 * Created by Kasper Tvede on 12-07-2017.
 */
typealias DataComposerFetcher<Raw> = suspend () -> Raw
typealias RawDataConveter<Raw, Logic> = (Raw) -> Logic
typealias SplitDataConverter<Logic, SplitLogic> = (Logic) -> List<SplitLogic>
typealias LogicProcessing<SplitLogic, ProcessedLogic> = (SplitLogic) -> ProcessedLogic
typealias postProcessing<ProcessLogic, PostLogic> = (ProcessLogic) -> PostLogic
typealias ComposeResult<PostLogic> = suspend (List<PostLogic>) -> Unit


class DataComposer2<Raw, Logic, SplitterLogic, ProcessedLogic>(
        val fetchSteps: DataComposerFetcher<Raw>,
        val rawToLogic: RawDataConveter<Raw, Logic>,
        val splitLogic: (Logic) -> List<SplitterLogic>,
        val processLogic: LogicProcessing<SplitterLogic, ProcessedLogic>,
        val postResult: ComposeResult<ProcessedLogic>) {

    suspend fun startProcess() = processFlow()

    private fun processFlow() = async(CommonPool) {
        val fetched = fetchSteps()
        val logicModel = rawToLogic(fetched)
        val splittet = splitLogic(logicModel)
        //start processing in parallel
        val jobsPreProcessLogic = splittet.map { async(CommonPool) { processLogic(it) } }
        val processedLogic = jobsPreProcessLogic.map { it.await() }
        postResult(processedLogic)
    }


}
package com.commonsense.android.kotlin.example.views.tools

import com.commonsense.android.kotlin.base.EmptyFunction
import com.commonsense.android.kotlin.base.time.TimeUnit
import com.commonsense.android.kotlin.example.databinding.ToolsFpsBinding
import com.commonsense.android.kotlin.system.logging.L
import com.commonsense.android.kotlin.tools.fps.FpsWatcher
import com.commonsense.android.kotlin.views.databinding.activities.BaseDatabindingActivity
import com.commonsense.android.kotlin.views.databinding.activities.InflaterFunctionFull
import com.commonsense.android.kotlin.views.extensions.setOnclickAsync
import kotlinx.coroutines.experimental.Job
import java.util.*

/**
 * Created by Kasper Tvede on 01-03-2018.
 * Purpose:
 *
 */
class ToolsFpsActivity : BaseDatabindingActivity<ToolsFpsBinding>() {

    private val fpsWatcher: FpsWatcher by lazy {
        FpsWatcher(this, this::onFpsCallback)
    }

    private val randomJankFunction: EmptyFunction = {
        Thread.sleep(Random().nextLong().coerceAtMost(1000))
    }
    private var randomJunkJob: Job? = null

    override fun createBinding(): InflaterFunctionFull<ToolsFpsBinding> =
            ToolsFpsBinding::inflate

    override fun useBinding() {
        binding.toolsFpsStartButton.setOnclickAsync {
            startMonitoring()
        }

        binding.toolsFpsStall.setOnclickAsync {
            Thread.sleep(100)
        }
        binding.toolsFpsStopAll.setOnclickAsync {
            stopMonitoring()
        }

        binding.toolsFpsStartInlineGraph.setOnclickAsync {

        }

        binding.toolsFpsStartNotification.setOnclickAsync {

        }

        binding.toolsFpsStallRandom.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                randomJunkJob = launchInUiCancelable(""){

                }
            } else {

            }
        }

    }


    private fun stopMonitoring() {
        fpsWatcher.stop()
    }

    private fun startMonitoring() {
        fpsWatcher.start()
    }

    private fun onFpsCallback(currentFps: Float,
                              droppedFrames: Float,
                              overdueTime: TimeUnit) {
        L.warning("test", ".. got callback")
        binding.toolsFpsInlineCounter.text = "current fps : $currentFps\n" +
                "dropped frames :$droppedFrames\n" +
                "overdue time in ms :${overdueTime.toMilliseconds().milliSeconds}"
    }

//    override fun onDestroy() {
//        fpsWatcher.stop()
//        super.onDestroy()
//    }
}
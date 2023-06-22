package csense.android.exampleApp

import com.commonsense.android.kotlin.prebuilt.baseClasses.*
import com.commonsense.android.kotlin.system.extensions.*
import com.commonsense.android.kotlin.system.logging.*
import csense.android.tools.resources.*
import csense.android.tools.ui.*

class MainApplication : BaseApplication() {
    override fun isDebugMode(): Boolean = BuildConfig.DEBUG

    private val performanceTracker: LifeCycleMonitor = LifeCycleMonitor(this)

//    private val dynamicResources: DynamicResources = DynamicResources(
//            R.string::class,
//            R.drawable::class,
//            R.layout::class,
//            R.color::class,
//            R.raw::class,
//            R.dimen::class,
//            R.style::class,
//            R.styleable::class,
//            R.attr::class,
////            R.animator::class,
//            R.anim::class
//    )

    override fun afterOnCreate() {
        L.warning("test", "here")
//        dynamicResources.allLayouts.filterNot {
//            it.name.contains("leak_canary") ||
//                    it.name.contains("abc_") ||
//                    it.name.contains("design_") ||
//                    it.name.contains("notification_template")
//        }.forEach {
//            L.warning(MainApplication::class, "layout: ${it.name}:${it.id}")
//        }
        L.warning("test", "here end")
    }

    override fun onApplicationResumed() {
        super.onApplicationResumed()
        safeToast("application resumed")
    }

    override fun onApplicationPaused() {
        super.onApplicationPaused()
        safeToast("application paused.")

    }
}
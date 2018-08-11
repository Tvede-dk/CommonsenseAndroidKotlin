package csense.android.tools.crash

import android.annotation.SuppressLint
import android.content.Context
import android.support.annotation.AnyThread
import android.support.annotation.UiThread
import com.commonsense.android.kotlin.base.extensions.collections.ifTrue
import com.commonsense.android.kotlin.base.extensions.isNotNull
import com.commonsense.android.kotlin.system.logging.L
import com.commonsense.android.kotlin.system.logging.tryAndLog

/**
 * Created by Kasper Tvede on 01-02-2018.
 * Purpose:
 *
 */
class CrashListener(context: Context) : Thread.UncaughtExceptionHandler {

    private val applicationContext: Context = context.applicationContext

    private var shouldShowUi = true
    private var shouldLogException = true

    @AnyThread
    override fun uncaughtException(t: Thread?, e: Throwable?) {
        tryAndLog("uncaughtExceptionHandler") {
            shouldLogException.ifTrue { logger("", "", e) }
            shouldShowUi.ifTrue { openUI(t, e) }
        }
        //rethrow.
        val error = e ?: return
        throw error
    }

    /**
     * opens the ui with the thread and throwable
     */
    private fun openUI(t: Thread?, e: Throwable?) {
        L.debug(CrashListener::class.java.simpleName, "opening ui for crash")
//        applicationContext.startActivityWithData(CrashDisplayActivity::class, CrashDisplayData(t, e))
    }

    /**
     * A function given a tag, message and a throwable, which returns nothing.
     * usually mapped to the library's error logging
     */
    private var logger: Function3<String, String, Throwable?, Unit> = L::error

    /**
     * The other callback to call after handling.
     */
    private var chain: Thread? = null


    companion object {
        /**
         * The global listener
         */
        @SuppressLint("StaticFieldLeak") // uses application context.
        private var singleListener: CrashListener? = null

        @UiThread
        fun setupListenerGlobally(context: Context) {
            if (singleListener.isNotNull) {
                return
            }
            singleListener = CrashListener(context).apply {
                val old = Thread.currentThread()
                this.chain = old
                Thread.setDefaultUncaughtExceptionHandler(this)
            }

        }
    }

}
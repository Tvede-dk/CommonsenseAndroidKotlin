//package csense.android.tools.crash
//
//import com.commonsense.android.kotlin.system.extensions.*
//import com.commonsense.android.kotlin.views.databinding.activities.*
//import csense.android.tools.databinding.*
//
///**
// * Created by Kasper Tvede on 01-02-2018.
// * Purpose:
// *
// */
//data class CrashDisplayData(val thread: Thread?, val throwable: Throwable?)
//
//
//class CrashDisplayActivity : BaseDatabindingActivityWithData<CrashActivityViewBinding, CrashDisplayData>() {
//    override fun createBinding(): InflaterFunctionSimple<CrashActivityViewBinding> =
//            CrashActivityViewBinding::inflate
//
//    override fun useBinding() {
//        safeToast("hello!!")
//    }
//
//}
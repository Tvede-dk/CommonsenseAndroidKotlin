package com.commonsense.android.kotlin.views.input

import android.widget.*
import com.commonsense.android.kotlin.base.extensions.*
import com.commonsense.android.kotlin.test.*
import org.junit.*
import java.util.concurrent.*

/**
 * Created by kasper on 21/08/2017.
 */
class InputValidatorTest : BaseRoboElectricTest() {

    @Test
    fun testSimple() {
        val validator = InputValidator.Builder<String>()
        val editText = EditText(context)
        editText.text = "magic2".toEditable()
        //3 parts, first what object, then a list of rules of validation with messages on each so

        validator.add(editText, { it: EditText ->
            return@add when {
                it.text.isBlank() -> "blank not allowed"
                it.text.length < 5 -> "short names not allowed"
                it.text.toString() == "magic" -> "magic not allowed"
                else -> null
            }
        }, null)
        val builded = validator.build()
        builded.validate().assert(true, "magic2 should not be caught by the rules.")

        editText.text = "magic".toEditable()
        builded.validate().assert(false, "magic should be caught by the rules.")

        editText.text = "a".toEditable()
        builded.validate().assert(false, "a should be caught by the short name rule.")

        editText.text = "".toEditable()
        builded.validate().assert(false, "<> should be caught by the Blank rule.")


    }

    @Throws(InterruptedException::class)
    @Test
    fun testCallbacks() {
        val validator = InputValidator.Builder<String>()
        val inputTextEdit = EditText(context)
        inputTextEdit.text = "magic".toEditable()
        val sem = Semaphore(0)

        validator.add(inputTextEdit,
                validationCallback = { it: EditText ->
                    return@add when {
                        it.text.isBlank() -> "blank not allowed"
                        it.text.length < 5 -> "short names not allowed"
                        it.text.toString() == "magic" -> "magic not allowed"
                        else -> null
                    }
                },
                onValidationFailed = { editText, errorMessage ->
                    errorMessage.assert("magic not allowed")
                    editText.text.toString().assert("magic")
                    sem.release()
                }
                /*,   attachEvents = { editText: ExtendedEditTextView, validationCall: EmptyFunction ->
                       editText.setTextChangeListener(OnTextChangedWatcher {
                           validationCall()
                       })
                   }*/
        )

        //add global failure listener.
        validator.addOnValidationFailedCallback { errorMessage: String ->
            errorMessage.assert("magic not allowed")
            sem.release()
        }
        val isValid = validator.build().validate()
        isValid.assert(false, "")
        sem.tryAcquire(2, 10, TimeUnit.SECONDS).assert(true)

    }

    @Ignore
    @Test
    fun validate() {
    }


}
package com.commonsense.android.kotlin.views.input

import com.commonsense.android.kotlin.base.FunctionUnit

/**
 * Created by kasper on 21/08/2017.
 */

typealias ValidationCallback<T, U> = (T) -> U?
typealias ValidationFailedCallback<T, U> = (T, U) -> Unit
private typealias MutableRuleList<U> = MutableList<ValidationRule<*, U>>
private typealias RuleList<U> = List<ValidationRule<*, U>>
class InputValidator<U> {

    private val elementsToValidate: RuleList<U>

    private val onFailedValidation: FunctionUnit<U>?

    private constructor(elementsToValidate: RuleList<U>, onFailedValidation: FunctionUnit<U>?) {
        this.elementsToValidate = elementsToValidate
        this.onFailedValidation = onFailedValidation
    }


    fun validate(): Boolean {
        return elementsToValidate.all { rule ->
            rule.validate(onFailedValidation)
        }
    }


    class Builder<U> {

        private val elementsToValidate: MutableRuleList<U> = mutableListOf()

        private var onFailedValidation: FunctionUnit<U>? = null

        fun <T> add(objectToUse: T,
                    validationCallback: ValidationCallback<T, U>,
                    onValidationFailed: ValidationFailedCallback<T, U>? = null
        ) {
            val rule = ValidationRule(objectToUse, validationCallback, onValidationFailed)
            elementsToValidate.add(rule)
            /*   attachEvents?.invoke(objectToUse, {

                   //TODO something in this manner.
                   rule.validate(onFailedValidation)
               })*/
        }

        fun addOnValidationFailedCallback(callback: FunctionUnit<U>) {
            onFailedValidation = callback
        }

        fun addOnValidationCallback() {

        }


        fun build(): InputValidator<U> {
            return InputValidator(elementsToValidate.toList(), onFailedValidation)
        }

    }

}

private class ValidationRule<T, U>(val theObject: T, val validationRules: ValidationCallback<T, U>,
                                   val onValidationFailed: ValidationFailedCallback<T, U>?) {

    fun validate(optErrorCallback: FunctionUnit<U>?): Boolean {
        //if null then no errors , otherwise theres an error.
        return validationRules(theObject)?.let {
            onValidationFailed?.invoke(theObject, it)
            optErrorCallback?.invoke(it)
            false
        } ?: true
    }
}


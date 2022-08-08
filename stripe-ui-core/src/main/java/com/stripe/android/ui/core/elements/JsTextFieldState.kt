package com.stripe.android.ui.core.elements

import androidx.annotation.RestrictTo
import com.stripe.android.ui.core.R
import kotlinx.serialization.Serializable

/**
 * This represents the different states a field can be in,
 * in each of these cases there might be a reason to show the
 * error in a different way.  This interface separates how the state
 * is implemented from what information is required by clients of the interface.
 * This will allow the implementation to change without impacting the clients.
 */
@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP_PREFIX)
@Serializable
class JsTextFieldState(
    /**
     * Indicate if this is an error that should be displayed to the user.
     * This cannot be used to determine if the field is valid or not because
     * there are some cases such as incomplete or blank where the error is not
     * displayed, but also not valid.
     */
    val shouldShowErrorHasFocus: Boolean,
    val shouldShowErrorHasNoFocus: Boolean,

    /**
     * This is used to indicate the field contains the maximum number of characters.
     * This is needed to know when to advance to the next field.
     */
    val full: Boolean,

    /**
     * This is used to indicate the field is blank which can be helpful when ignoring optional
     * fields.
     */
    val blank: Boolean,

    /**
     * Indicates an field is valid and field extraction can happen
     * and be used to create PaymentMethod Parameters
     */
    val valid: Boolean,

    val errorMsg: String?

) : TextFieldState {
    override fun shouldShowError(hasFocus: Boolean): Boolean {
        return if (hasFocus) {
            shouldShowErrorHasFocus
        } else {
            shouldShowErrorHasNoFocus
        }
    }

    override fun isValid() = valid

    /**
     * If in a state where isValid() returns false, this function returns the error message.
     * It is up to calling shouldShowError to determine if it should be displayed on screen.
     */
    override fun getError() = errorMsg?.let {
        FieldError(R.string.str, arrayOf(errorMsg))
    }

    override fun isFull() = full

    override fun isBlank() = blank
}

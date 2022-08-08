package com.stripe.android.ui.core.elements

import androidx.annotation.RestrictTo
import androidx.annotation.StringRes
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import com.stripe.android.ui.core.R

@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
class USBankAccountNumberConfig() : TextFieldConfig {
    override val capitalization: KeyboardCapitalization = KeyboardCapitalization.None
    override val debugLabel = "us_bank_account_number"

    @StringRes
    override val label = R.string.us_bank_account_widget_account_number
    override val keyboard = KeyboardType.Number

    override val visualTransformation: VisualTransformation? = null

    override fun filter(userTyped: String) =
        userTyped.filter { VALID_INPUT_RANGES.contains(it) }.take(LENGTH)

    override fun convertToRaw(displayName: String) = displayName

    override fun convertFromRaw(rawValue: String) = rawValue

    override fun determineState(input: String): TextFieldState {
        input.ifBlank {
            return TextFieldStateConstants.Error.Blank
        }

        if (input.length < LENGTH) {
            return TextFieldStateConstants.Error.Incomplete(
                R.string.us_bank_account_widget_account_number_incomplete
            )
        }

        val bank = banks.firstOrNull {
            input.startsWith(it.prefix)
        }

        if (bank == null || input.length > LENGTH) {
            return TextFieldStateConstants.Error.Invalid(
                R.string.us_bank_account_widget_account_number_invalid
            )
        }

        return TextFieldStateConstants.Valid.Full
    }

    private companion object {
        const val LENGTH = 12
        val VALID_INPUT_RANGES = ('0'..'9')
    }
}

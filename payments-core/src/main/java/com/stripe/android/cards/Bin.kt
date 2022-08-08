package com.stripe.android.cards

import androidx.annotation.RestrictTo
import com.stripe.android.core.model.StripeModel
import kotlinx.parcelize.Parcelize

@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
@Parcelize
data class Bin internal constructor(
    internal val value: String
) : StripeModel {
    override fun toString() = value

    internal companion object {
        fun create(cardNumber: String): Bin? {
            return cardNumber
                .take(BIN_LENGTH)
                .takeIf {
                    it.length == BIN_LENGTH
                }?.let {
                    Bin(it)
                }
        }

        private const val BIN_LENGTH = 6
    }
}

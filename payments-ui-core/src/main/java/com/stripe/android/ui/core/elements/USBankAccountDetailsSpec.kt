package com.stripe.android.ui.core.elements

import androidx.annotation.RestrictTo
import kotlinx.parcelize.Parcelize

@Parcelize
@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
internal data class USBankAccountDetailsSpec(
    override val identifier: IdentifierSpec
) : FormItemSpec(), RequiredItemSpec {
    fun transform(): FormElement =
        USBankAccountDetailsElement(
            this.identifier
        )
}

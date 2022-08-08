package com.stripe.android.ui.core.elements

import kotlinx.parcelize.Parcelize

@Parcelize
internal object USBankAccountNumberSpec : SectionFieldSpec(IdentifierSpec.Generic("us_bank_account_number")) {
    fun transform(): SectionFieldElement =
        SimpleTextElement(
            this.identifier,
            TextFieldController(USBankAccountNumberConfig())
        )
}

package com.stripe.android.ui.core.elements

import kotlinx.parcelize.Parcelize

@Parcelize
internal object USBankRoutingNumberSpec : SectionFieldSpec(IdentifierSpec.Generic("us_bank_routing_number")) {
    fun transform(): SectionFieldElement =
        SimpleTextElement(
            this.identifier,
            TextFieldController(USBankRoutingNumberConfig())
        )
}

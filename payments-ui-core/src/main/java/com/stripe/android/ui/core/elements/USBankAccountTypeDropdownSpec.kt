package com.stripe.android.ui.core.elements

import com.stripe.android.ui.core.R
import kotlinx.parcelize.Parcelize

@Parcelize
internal data class USBankAccountTypeDropdownSpec(
    override val identifier: IdentifierSpec,
) : SectionFieldSpec(identifier) {
    fun transform(): SectionFieldElement =
        SimpleDropdownElement(
            this.identifier,
            DropdownFieldController(
                SimpleDropdownConfig(
                    label = R.string.us_bank_account_widget_account_type,
                    items = listOf(
                        DropdownItemSpec(
                            value = "checking",
                            text = "Checking"
                        ),
                        DropdownItemSpec(
                            value = "savings",
                            text = "Savings"
                        )
                    )
                )
            )
        )
}

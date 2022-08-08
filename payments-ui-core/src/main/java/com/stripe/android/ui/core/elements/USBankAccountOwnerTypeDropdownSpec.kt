package com.stripe.android.ui.core.elements

import com.stripe.android.ui.core.R
import kotlinx.parcelize.Parcelize

@Parcelize
internal data class USBankAccountOwnerTypeDropdownSpec(
    override val identifier: IdentifierSpec,
) : SectionFieldSpec(identifier) {
    fun transform(): SectionFieldElement =
        SimpleDropdownElement(
            this.identifier,
            DropdownFieldController(
                SimpleDropdownConfig(
                    label = R.string.us_bank_account_widget_account_owner_type,
                    items = listOf(
                        DropdownItemSpec(
                            value = "individual",
                            text = "Individual"
                        ),
                        DropdownItemSpec(
                            value = "company",
                            text = "Company"
                        )
                    )
                )
            )
        )
}

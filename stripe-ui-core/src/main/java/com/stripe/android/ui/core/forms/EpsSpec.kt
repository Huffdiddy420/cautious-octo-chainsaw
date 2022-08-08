package com.stripe.android.ui.core.forms

import com.stripe.android.ui.core.R
import com.stripe.android.ui.core.elements.BankDropdownSpec
import com.stripe.android.ui.core.elements.IdentifierSpec
import com.stripe.android.ui.core.elements.LayoutSpec
import com.stripe.android.ui.core.elements.SectionSpec
import com.stripe.android.ui.core.elements.SimpleTextSpec
import com.stripe.android.ui.core.elements.SupportedBankType
import com.stripe.android.ui.core.elements.billingParams

internal val EpsRequirement = PaymentMethodRequirements(

    /**
     * Disabling this support so that it doesn't negatively impact our ability
     * to save cards when the user selects SFU set and the PI has PM that don't support
     * SFU to be set.
     *
     * When supported there are no known pi requirements and can be set to an empty set.
     */
    piRequirements = null,
    siRequirements = null, // this is not supported by this payment method
    confirmPMFromCustomer = null
)

internal val epsParams: MutableMap<String, Any?> = mutableMapOf(
    "bank" to null,
)

internal val EpsParamKey: MutableMap<String, Any?> = mutableMapOf(
    "type" to "eps",
    "billing_details" to billingParams,
    "eps" to epsParams
)

internal val epsNameSection = SectionSpec(
    IdentifierSpec.Generic("name_section"),
    SimpleTextSpec.NAME
)
internal val epsBankSection =
    SectionSpec(
        IdentifierSpec.Generic("bank_section"),
        BankDropdownSpec(
            IdentifierSpec.Generic("bank"),
            R.string.eps_bank,
            SupportedBankType.Eps
        )
    )

internal val EpsForm = LayoutSpec.create(
    epsNameSection,
    epsBankSection
)

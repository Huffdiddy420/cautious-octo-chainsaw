package com.stripe.android.ui.core.forms

import com.stripe.android.ui.core.R
import com.stripe.android.ui.core.elements.BankDropdownSpec
import com.stripe.android.ui.core.elements.EmailSpec
import com.stripe.android.ui.core.elements.IdentifierSpec
import com.stripe.android.ui.core.elements.LayoutSpec
import com.stripe.android.ui.core.elements.SectionSpec
import com.stripe.android.ui.core.elements.SimpleTextSpec
import com.stripe.android.ui.core.elements.SupportedBankType
import com.stripe.android.ui.core.elements.billingParams

internal val P24Requirement = PaymentMethodRequirements(
    /**
     * Disabling this support so that it doesn't negatively impact our ability
     * to save cards when the user selects SFU set and the PI has PM that don't support
     * SFU to be set.
     *
     * When supported there are no known PI requirements and can be set to an empty set.
     */
    piRequirements = null,
    siRequirements = null, // this is not supported by this payment method

    /**
     * This cannot be saved to a customer object.
     */
    confirmPMFromCustomer = null
)

internal val p24Params: MutableMap<String, Any?> = mutableMapOf(
    "bank" to null,
)

internal val P24ParamKey: MutableMap<String, Any?> = mutableMapOf(
    "type" to "p24",
    "billing_details" to billingParams,
    "p24" to p24Params
)

internal val p24NameSection = SectionSpec(
    IdentifierSpec.Generic("name_section"),
    SimpleTextSpec.NAME
)
internal val p24EmailSection = SectionSpec(
    IdentifierSpec.Generic("email_section"),
    EmailSpec
)
internal val p24BankSection =
    SectionSpec(
        IdentifierSpec.Generic("bank_section"),
        BankDropdownSpec(
            IdentifierSpec.Generic("bank"),
            R.string.p24_bank,
            SupportedBankType.P24
        )
    )

internal val P24Form = LayoutSpec.create(
    p24NameSection,
    p24EmailSection,
    p24BankSection
)

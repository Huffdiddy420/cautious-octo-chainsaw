package com.stripe.android.ui.core.forms

import androidx.annotation.RestrictTo
import com.stripe.android.ui.core.elements.EmailSpec
import com.stripe.android.ui.core.elements.IdentifierSpec
import com.stripe.android.ui.core.elements.LayoutSpec
import com.stripe.android.ui.core.elements.SectionSpec
import com.stripe.android.ui.core.elements.SimpleTextSpec
import com.stripe.android.ui.core.elements.USBankAccountNumberSpec
import com.stripe.android.ui.core.elements.USBankAccountOwnerTypeDropdownSpec
import com.stripe.android.ui.core.elements.USBankAccountTypeDropdownSpec
import com.stripe.android.ui.core.elements.USBankRoutingNumberSpec

@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
val USBankAccountParamKey: MutableMap<String, Any?> = mutableMapOf(
    "type" to "us_bank_account"
)

internal val usBankAccountNameSection = SectionSpec(
    IdentifierSpec.Name,
    SimpleTextSpec.NAME
)

internal val usBankAccountEmailSection = SectionSpec(
    IdentifierSpec.Email,
    EmailSpec
)

internal val usBankAccountNumberSection = SectionSpec(
    IdentifierSpec.Generic("us_bank_account_number"),
    USBankAccountNumberSpec
)

internal val usBankRoutingNumberSection = SectionSpec(
    IdentifierSpec.Generic("us_bank_routing_number"),
    USBankRoutingNumberSpec
)

internal val usBankAccountTypeSection =
    SectionSpec(
        IdentifierSpec.Generic("account_type_section"),
        USBankAccountTypeDropdownSpec(
            IdentifierSpec.Generic("type")
        )
    )

internal val usBankAccountOwnerTypeSection =
    SectionSpec(
        IdentifierSpec.Generic("account_owner_type_section"),
        USBankAccountOwnerTypeDropdownSpec(
            IdentifierSpec.Generic("owner")
        )
    )

@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
val USBankAccountForm = LayoutSpec.create(
    usBankAccountNameSection,
    usBankAccountEmailSection,
    usBankAccountNumberSection,
    usBankRoutingNumberSection,
    usBankAccountTypeSection,
    usBankAccountOwnerTypeSection
)

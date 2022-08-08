package com.stripe.android.ui.core.forms

import androidx.annotation.RestrictTo
import com.stripe.android.ui.core.elements.IdentifierSpec
import com.stripe.android.ui.core.elements.LayoutSpec
import com.stripe.android.ui.core.elements.OptionalEmailSpec
import com.stripe.android.ui.core.elements.SectionSpec
import com.stripe.android.ui.core.elements.SimpleTextSpec
import com.stripe.android.ui.core.elements.USBankAccountDetailsSpec

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
    OptionalEmailSpec
)

internal val usBankAccountAccountDetailsSpec = USBankAccountDetailsSpec(
    IdentifierSpec.Generic("accoount_details")
)

@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
val USBankAccountForm = LayoutSpec.create(
    usBankAccountNameSection,
    usBankAccountEmailSection,
    usBankAccountAccountDetailsSpec
)

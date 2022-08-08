package com.stripe.android.ui.core.forms

import com.stripe.android.ui.core.R
import com.stripe.android.ui.core.elements.CountrySpec
import com.stripe.android.ui.core.elements.EmailSpec
import com.stripe.android.ui.core.elements.IdentifierSpec
import com.stripe.android.ui.core.elements.LayoutSpec
import com.stripe.android.ui.core.elements.SaveForFutureUseSpec
import com.stripe.android.ui.core.elements.SectionSpec
import com.stripe.android.ui.core.elements.SimpleTextSpec
import com.stripe.android.ui.core.elements.StaticTextSpec
import com.stripe.android.ui.core.elements.billingParams

internal val SofortRequirement = PaymentMethodRequirements(
    piRequirements = setOf(Delayed),

    /**
     * Currently we will not support this PaymentMethod for use with PI w/SFU,
     * or SI until there is a way of retrieving valid mandates associated with a customer PM.
     *
     * The reason we are excluding it is because after PI w/SFU set or PI
     * is used, the payment method appears as a SEPA payment method attached
     * to a customer.  Without this block the SEPA payment method would
     * show in PaymentSheet.  If the user used this save payment method
     * we would have no way to know if the existing mandate was valid or how
     * to request the user to re-accept the mandate.
     *
     * SEPA Debit does support PI w/SFU and SI (both with and without a customer),
     * and it is Delayed in this configuration.
     */
    siRequirements = null,

    /**
     * This PM cannot be attached to a customer, it should be noted that it
     * will be attached as a SEPA Debit payment method and have the requirements
     * of that PaymentMethod, but for now SEPA is not supported either so we will
     * call it false.
     */
    confirmPMFromCustomer = false
)

internal val sofortParams: MutableMap<String, Any?> = mutableMapOf(
    "country" to null,
)

internal val SofortParamKey: MutableMap<String, Any?> = mutableMapOf(
    "type" to "sofort",
    "billing_details" to billingParams,
    "sofort" to sofortParams
)

internal val sofortNameSection = SectionSpec(
    IdentifierSpec.Generic("name_section"),
    SimpleTextSpec.NAME
)
internal val sofortEmailSection = SectionSpec(IdentifierSpec.Generic("email_section"), EmailSpec)
internal val sofortCountrySection =
    SectionSpec(
        IdentifierSpec.Generic("country_section"),
        CountrySpec(setOf("AT", "BE", "DE", "ES", "IT", "NL"))
    )
internal val sofortMandate = StaticTextSpec(
    IdentifierSpec.Generic("mandate"),
    R.string.sepa_mandate,
    R.color.mandate_text_color
)

internal val SofortForm = LayoutSpec.create(
    sofortNameSection,
    sofortEmailSection,
    sofortCountrySection,
    SaveForFutureUseSpec(listOf(sofortNameSection, sofortEmailSection, sofortMandate)),
    sofortMandate,
)

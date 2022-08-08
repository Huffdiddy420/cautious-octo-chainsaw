package com.stripe.android.ui.core.elements

/**
 * Header that displays information about installments for Afterpay
 */
internal data class AfterpayClearpayTextSpec(
    override val identifier: IdentifierSpec
) : FormItemSpec(), RequiredItemSpec

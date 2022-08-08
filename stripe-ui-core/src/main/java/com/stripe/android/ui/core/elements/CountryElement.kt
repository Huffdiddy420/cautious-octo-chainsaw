package com.stripe.android.ui.core.elements

internal data class CountryElement(
    override val identifier: IdentifierSpec,
    override val controller: DropdownFieldController
) : SectionSingleFieldElement(identifier)

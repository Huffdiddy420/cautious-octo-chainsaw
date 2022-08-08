package com.stripe.android.ui.core.elements

internal data class SimpleDropdownElement(
    override val identifier: IdentifierSpec,
    override val controller: DropdownFieldController
) : SectionSingleFieldElement(identifier)

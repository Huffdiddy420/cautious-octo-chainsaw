package com.stripe.android.ui.core.elements

import com.stripe.android.ui.core.forms.FormFieldEntry
import kotlinx.coroutines.flow.Flow

internal sealed interface SectionFieldElement {
    val identifier: IdentifierSpec

    fun getFormFieldValueFlow(): Flow<List<Pair<IdentifierSpec, FormFieldEntry>>>
    fun sectionFieldErrorController(): SectionFieldErrorController
    fun setRawValue(rawValuesMap: Map<IdentifierSpec, String?>)
}

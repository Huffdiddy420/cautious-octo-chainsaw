package com.stripe.android.ui.core.elements

import androidx.annotation.RestrictTo
import com.stripe.android.ui.core.forms.FormFieldEntry
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
class RowElement constructor(
    _identifier: IdentifierSpec,
    val fields: List<SectionSingleFieldElement>,
    val controller: RowController
) : SectionMultiFieldElement(_identifier) {
    override fun getFormFieldValueFlow(): Flow<List<Pair<IdentifierSpec, FormFieldEntry>>> =
        combine(fields.map { it.getFormFieldValueFlow() }) {
            it.toList().flatten()
        }

    override fun getTargetFlow(): Flow<JsRequest.Target?> =
        combine(fields.map { it.getTargetFlow() }) {
            val children = it.toList().filterNotNull()

            val target = JsRequest.Target(
                id = identifier.value,
                children = children
            )
            children.forEach { child ->
                child.parent = target
            }

            target
        }

    override fun sectionFieldErrorController() = controller

    override fun setRawValue(rawValuesMap: Map<IdentifierSpec, String?>) {
        fields.forEach {
            it.setRawValue(rawValuesMap)
        }
    }
}

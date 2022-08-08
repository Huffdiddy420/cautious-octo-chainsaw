package com.stripe.android.ui.core.elements

import androidx.annotation.RestrictTo
import kotlinx.parcelize.Parcelize

/**
 * This is an element that will make elements (as specified by identifier) hidden
 * when save for future use is unchecked
 */
@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
@Parcelize
data class SaveForFutureUseSpec(
    val identifierRequiredForFutureUse: List<RequiredItemSpec>
) : FormItemSpec(), RequiredItemSpec {
    override val api_path = IdentifierSpec.SaveForFutureUse

    fun transform(initialValue: Boolean, merchantName: String): FormElement =
        SaveForFutureUseElement(
            this.api_path,
            SaveForFutureUseController(
                this.identifierRequiredForFutureUse.map { requiredItemSpec ->
                    requiredItemSpec.api_path
                },
                initialValue
            ),
            merchantName
        )
}

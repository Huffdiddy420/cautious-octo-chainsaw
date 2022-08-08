package com.stripe.android.ui.core.elements

import androidx.annotation.RestrictTo
import kotlinx.parcelize.Parcelize

@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
@Parcelize
object EmailSpec : SectionFieldSpec(IdentifierSpec.Email) {
    fun transform(email: String?): SectionFieldElement =
        EmailElement(
            this.identifier,
            SimpleTextFieldController(EmailConfig(), initialValue = email),
        )
}

@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
@Parcelize
object OptionalEmailSpec : SectionFieldSpec(IdentifierSpec.Email) {
    fun transform(email: String?): SectionFieldElement =
        EmailElement(
            this.identifier,
            SimpleTextFieldController(EmailConfig(), initialValue = email, showOptionalLabel = true),
        )
}

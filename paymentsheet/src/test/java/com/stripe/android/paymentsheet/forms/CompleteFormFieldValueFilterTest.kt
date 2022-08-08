package com.stripe.android.paymentsheet.forms

import com.google.common.truth.Truth.assertThat
import com.stripe.android.paymentsheet.elements.EmailElement
import com.stripe.android.ui.core.elements.EmailConfig
import com.stripe.android.ui.core.elements.SectionController
import com.stripe.android.paymentsheet.elements.SectionElement
import com.stripe.android.ui.core.elements.TextFieldController
import com.stripe.android.ui.core.elements.IdentifierSpec
import com.stripe.android.paymentsheet.model.PaymentSelection
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Test

@ExperimentalCoroutinesApi
class CompleteFormFieldValueFilterTest {

    private val emailController = com.stripe.android.ui.core.elements.TextFieldController(com.stripe.android.ui.core.elements.EmailConfig())
    private val emailSection = SectionElement(
        identifier = com.stripe.android.ui.core.elements.IdentifierSpec.Generic("email_section"),
        EmailElement(
            com.stripe.android.ui.core.elements.IdentifierSpec.Email,
            emailController
        ),
        com.stripe.android.ui.core.elements.SectionController(emailController.label, listOf(emailController))
    )

    private val hiddenIdentifersFlow = MutableStateFlow<List<com.stripe.android.ui.core.elements.IdentifierSpec>>(emptyList())

    private val fieldFlow = MutableStateFlow(
        mapOf(
            com.stripe.android.ui.core.elements.IdentifierSpec.Country to FormFieldEntry("US", true),
            com.stripe.android.ui.core.elements.IdentifierSpec.Email to FormFieldEntry("email@email.com", false),
        )
    )

    private val transformElementToFormFieldValueFlow = CompleteFormFieldValueFilter(
        fieldFlow,
        hiddenIdentifersFlow,
        showingMandate = MutableStateFlow(true),
        userRequestedReuse = MutableStateFlow(PaymentSelection.CustomerRequestedSave.NoRequest)
    )

    @Test
    fun `With only some complete controllers and no hidden values the flow value is null`() {
        runBlockingTest {
            assertThat(transformElementToFormFieldValueFlow.filterFlow().first()).isNull()
        }
    }

    @Test
    fun `If all controllers are complete and no hidden values the flow value has all values`() {
        runBlockingTest {
            fieldFlow.value =
                mapOf(
                    com.stripe.android.ui.core.elements.IdentifierSpec.Country to FormFieldEntry("US", true),
                    com.stripe.android.ui.core.elements.IdentifierSpec.Email to FormFieldEntry("email@email.com", true),
                )

            val formFieldValue = transformElementToFormFieldValueFlow.filterFlow().first()

            assertThat(formFieldValue).isNotNull()
            assertThat(formFieldValue?.fieldValuePairs)
                .containsKey(com.stripe.android.ui.core.elements.IdentifierSpec.Email)
            assertThat(formFieldValue?.fieldValuePairs)
                .containsKey(com.stripe.android.ui.core.elements.IdentifierSpec.Country)
        }
    }

    @Test
    fun `If an hidden field is incomplete field pairs have the non-hidden values`() {
        runBlockingTest {
            hiddenIdentifersFlow.value = listOf(com.stripe.android.ui.core.elements.IdentifierSpec.Email)

            val formFieldValues = transformElementToFormFieldValueFlow.filterFlow()

            val formFieldValue = formFieldValues.first()
            assertThat(formFieldValue).isNotNull()
            assertThat(formFieldValue?.fieldValuePairs)
                .doesNotContainKey(com.stripe.android.ui.core.elements.IdentifierSpec.Email)
            assertThat(formFieldValue?.fieldValuePairs)
                .containsKey(com.stripe.android.ui.core.elements.IdentifierSpec.Country)
        }
    }

    @Test
    fun `If an hidden field is complete field pairs contain only the non-hidden values`() {
        runBlockingTest {
            fieldFlow.value =
                mapOf(
                    com.stripe.android.ui.core.elements.IdentifierSpec.Country to FormFieldEntry("US", true),
                    com.stripe.android.ui.core.elements.IdentifierSpec.Email to FormFieldEntry("email@email.com", true),
                )

            hiddenIdentifersFlow.value = listOf(emailSection.fields[0].identifier)

            val formFieldValue = transformElementToFormFieldValueFlow.filterFlow().first()

            assertThat(formFieldValue).isNotNull()
            assertThat(formFieldValue?.fieldValuePairs)
                .doesNotContainKey(com.stripe.android.ui.core.elements.IdentifierSpec.Email)
            assertThat(formFieldValue?.fieldValuePairs)
                .containsKey(com.stripe.android.ui.core.elements.IdentifierSpec.Country)
        }
    }
}

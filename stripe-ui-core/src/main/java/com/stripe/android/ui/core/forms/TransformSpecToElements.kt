package com.stripe.android.ui.core.forms

import android.util.Log
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import com.stripe.android.ui.core.Amount
import com.stripe.android.ui.core.R
import com.stripe.android.ui.core.address.AddressFieldElementRepository
import com.stripe.android.ui.core.elements.AddressSpec
import com.stripe.android.ui.core.elements.AfterpayClearpayTextSpec
import com.stripe.android.ui.core.elements.BankDropdownSpec
import com.stripe.android.ui.core.elements.BankRepository
import com.stripe.android.ui.core.elements.CountrySpec
import com.stripe.android.ui.core.elements.EmailSpec
import com.stripe.android.ui.core.elements.EmptyFormElement
import com.stripe.android.ui.core.elements.EmptyFormSpec
import com.stripe.android.ui.core.elements.FormElement
import com.stripe.android.ui.core.elements.FormItemSpec
import com.stripe.android.ui.core.elements.IbanSpec
import com.stripe.android.ui.core.elements.IdentifierSpec
import com.stripe.android.ui.core.elements.JsEngine
import com.stripe.android.ui.core.elements.JsSimpleTextElement
import com.stripe.android.ui.core.elements.JsTextFieldController
import com.stripe.android.ui.core.elements.KlarnaCountrySpec
import com.stripe.android.ui.core.elements.LayoutSpec
import com.stripe.android.ui.core.elements.SaveForFutureUseSpec
import com.stripe.android.ui.core.elements.SectionController
import com.stripe.android.ui.core.elements.SectionElement
import com.stripe.android.ui.core.elements.SectionFieldSpec
import com.stripe.android.ui.core.elements.SectionSpec
import com.stripe.android.ui.core.elements.SimpleTextFieldConfig
import com.stripe.android.ui.core.elements.SimpleTextSpec
import com.stripe.android.ui.core.elements.StaticTextSpec
import com.stripe.android.ui.core.forms.resources.ResourceRepository
import kotlinx.coroutines.flow.filter

/**
 * Transform a [LayoutSpec] data object into an Element, which
 * has a controller and identifier.  With only a single field in a section the section
 * controller will be a pass through the field controller.
 */
class TransformSpecToElements(
    private val resourceRepository: ResourceRepository,
    private val initialValues: Map<IdentifierSpec, String?>,
    private val amount: Amount?,
    private val country: String?,
    private val saveForFutureUseInitialValue: Boolean,
    private val merchantName: String,
    private val jsEngine: JsEngine
) {
    fun transform(
        list: List<FormItemSpec>
    ): List<FormElement> {
        val identifierSpec = IdentifierSpec.Generic("tst")
        val jsSimpleTextElement = listOf(
            JsSimpleTextElement(
                identifierSpec,
                JsTextFieldController(
                    jsEngine,
                    jsEngine.responseFlow.filter {
                        Log.e("MLB", "response id: ${it.id}")
                        it.id == identifierSpec.value
                    },
                    SimpleTextFieldConfig(
                        R.string.address_label_name,
                        KeyboardCapitalization.Words,
                        KeyboardType.Ascii
                    ),
                    identifierSpec,
                    onChangeJs = "{\n" +
                        // TODO: Must have curly braces. Put all javascript client side logic in here
                        "    Android.response(Android.post(\"/name\", JSON.stringify(e)));" +
                        "}"

                )
            )
        )
        return list.map {
            when (it) {
                is SaveForFutureUseSpec -> it.transform(
                    saveForFutureUseInitialValue,
                    merchantName
                )
                is SectionSpec -> it.transform(
                    initialValues,
                    amount?.currencyCode,
                    country,
                    resourceRepository.getBankRepository(),
                    resourceRepository.getAddressRepository()
                )
                is StaticTextSpec -> it.transform(merchantName)
                is AfterpayClearpayTextSpec ->
                    it.transform(requireNotNull(amount))
                is EmptyFormSpec -> EmptyFormElement()
            }
        }.plus(
            SectionElement(
                IdentifierSpec.Generic("section_tst"),
                jsSimpleTextElement,
                SectionController(
                    null,
                    jsSimpleTextElement.map { it.sectionFieldErrorController() }
                )
            )

        )
    }

    private fun SectionSpec.transform(
        initialValues: Map<IdentifierSpec, String?>,
        currencyCode: String?,
        country: String?,
        bankRepository: BankRepository,
        addressRepository: AddressFieldElementRepository
    ): SectionElement {
        val fieldElements = this.fields.transform(
            initialValues,
            currencyCode,
            country,
            bankRepository,
            addressRepository
        )

        // The controller of the section element will be the same as the field element
        // as there is only a single field in a section
        return SectionElement(
            this.identifier,
            fieldElements,
            SectionController(
                this.title,
                fieldElements.map { it.sectionFieldErrorController() }
            )
        )
    }

    /**
     * This function will transform a list of specs into a list of elements
     */
    private fun List<SectionFieldSpec>.transform(
        initialValues: Map<IdentifierSpec, String?>,
        currencyCode: String?,
        country: String?,
        bankRepository: BankRepository,
        addressRepository: AddressFieldElementRepository
    ) =
        this.map {
            when (it) {
                is EmailSpec -> it.transform(initialValues[IdentifierSpec.Email])
                is IbanSpec -> it.transform()
                is BankDropdownSpec -> it.transform(bankRepository)
                is SimpleTextSpec -> it.transform(initialValues)
                is AddressSpec -> it.transform(
                    initialValues,
                    addressRepository
                )
                is CountrySpec -> it.transform(
                    initialValues[IdentifierSpec.Country]
                )
                is KlarnaCountrySpec -> it.transform(
                    currencyCode,
                    country
                )
            }
        }
}

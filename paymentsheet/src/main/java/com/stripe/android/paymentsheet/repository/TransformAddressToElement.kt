package com.stripe.android.paymentsheet.repository

import androidx.annotation.StringRes
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import com.stripe.android.paymentsheet.R
import com.stripe.android.paymentsheet.repository.TransformAddressToSpec.CountryAddressSchema
import com.stripe.android.paymentsheet.forms.transform
import com.stripe.android.paymentsheet.specifications.IdentifierSpec
import com.stripe.android.paymentsheet.specifications.SectionFieldSpec
import kotlinx.coroutines.withContext
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json
import java.io.InputStream
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.CoroutineContext

@Singleton
internal class TransformAddressToSpec @Inject internal constructor(
    private val workContext: CoroutineContext
) {
    private val format = Json { ignoreUnknownKeys = true }

    internal suspend fun parseAddressesSchema(inputStream: InputStream?) =
        getJsonStringFromInputStream(inputStream)?.let {
            format.decodeFromString<List<CountryAddressSchema>>(
                it
            )
        }

    private suspend fun getJsonStringFromInputStream(inputStream: InputStream?) =
        withContext(workContext) {
            inputStream?.bufferedReader().use { it?.readText() }
        }

    companion object {
        private object FieldTypeAsStringSerializer : KSerializer<FieldType?> {
            override val descriptor: SerialDescriptor =
                PrimitiveSerialDescriptor("FieldType", PrimitiveKind.STRING)

            override fun serialize(encoder: Encoder, value: FieldType?) {
                encoder.encodeString(value?.serializedValue ?: "")
            }

            override fun deserialize(decoder: Decoder): FieldType? {
                return FieldType.from(decoder.decodeString())
            }
        }
    }

    @Serializable(with = FieldTypeAsStringSerializer::class)
    internal enum class FieldType(val serializedValue: String) {
        AddressLine1("addressLine1"),
        AddressLine2("addressLine2"),
        Locality("locality"),
        PostalCode("postalCode"),
        AdministrativeArea("administrativeArea"),
        Name("name");

        companion object {
            fun from(value: String) = values().firstOrNull {
                it.serializedValue == value
            }
        }
    }

    @Suppress("unused", "EnumEntryName")
    internal enum class NameType(@StringRes val stringResId: Int) {
        area(R.string.address_label_hk_area),
        cedex(R.string.address_label_cedex),
        city(R.string.address_label_city),
        country(R.string.address_label_country),
        county(R.string.address_label_county),
        department(R.string.address_label_department),
        district(R.string.address_label_district),
        do_si(R.string.address_label_kr_do_si),
        eircode(R.string.address_label_ie_eircode),
        emirate(R.string.address_label_ae_emirate),
        island(R.string.address_label_island),
        neighborhood(R.string.address_label_neighborhood),
        oblast(R.string.address_label_oblast),
        parish(R.string.address_label_bb_jm_parish),
        pin(R.string.address_label_in_pin),
        post_town(R.string.address_label_post_town),
        postal(R.string.address_label_postal_code),
        prefecture(R.string.address_label_jp_prefecture),
        province(R.string.address_label_province),
        state(R.string.address_label_state),
        suburb(R.string.address_label_suburb),
        suburb_or_city(R.string.address_label_au_suburb_or_city),
        townland(R.string.address_label_ie_townland),
        village_township(R.string.address_label_village_township),
        zip(R.string.address_label_zip_code)
    }

    @Serializable
    internal class FieldSchema(
        val isNumeric: Boolean = false,
        val nameType: NameType, // label,
    )

    @Serializable
    internal class CountryAddressSchema(
        val type: FieldType?,
        val required: Boolean,
        val schema: FieldSchema? = null
    )
}

internal fun List<CountryAddressSchema>.transformToElementList() =
    this.mapNotNull {
        when (it.type) {
            TransformAddressToSpec.FieldType.AddressLine1 -> {
                SectionFieldSpec.SimpleText(
                    IdentifierSpec("line1"),
                    it.schema?.nameType?.stringResId ?: R.string.address_label_address,
                    capitalization = KeyboardCapitalization.Words,
                    keyboardType = getKeyboard(it.schema),
                    showOptionalLabel = !it.required
                )
            }
            TransformAddressToSpec.FieldType.AddressLine2 -> {
                SectionFieldSpec.SimpleText(
                    IdentifierSpec("line2"),
                    it.schema?.nameType?.stringResId ?: R.string.address_label_address_line2,
                    capitalization = KeyboardCapitalization.Words,
                    keyboardType = getKeyboard(it.schema),
                    showOptionalLabel = !it.required
                )
            }
            TransformAddressToSpec.FieldType.Locality -> {
                SectionFieldSpec.SimpleText(
                    IdentifierSpec("city"),
                    it.schema?.nameType?.stringResId ?: R.string.address_label_city,
                    capitalization = KeyboardCapitalization.Words,
                    keyboardType = getKeyboard(it.schema),
                    showOptionalLabel = !it.required
                )
            }
            TransformAddressToSpec.FieldType.AdministrativeArea -> {
                SectionFieldSpec.SimpleText(
                    IdentifierSpec("state"),
                    it.schema?.nameType?.stringResId
                        ?: TransformAddressToSpec.NameType.state.stringResId,
                    capitalization = KeyboardCapitalization.Words,
                    keyboardType = getKeyboard(it.schema),
                    showOptionalLabel = !it.required
                )
            }
            TransformAddressToSpec.FieldType.PostalCode -> {
                SectionFieldSpec.SimpleText(
                    IdentifierSpec("postal_code"),
                    it.schema?.nameType?.stringResId ?: R.string.address_label_postal_code,
                    capitalization = KeyboardCapitalization.None,
                    keyboardType = getKeyboard(it.schema),
                    showOptionalLabel = !it.required
                )
            }
            else -> null
        }
    }.map {
        it.transform()
    }

private fun getKeyboard(fieldSchema: TransformAddressToSpec.FieldSchema?) =
    if (fieldSchema?.isNumeric == true) {
        KeyboardType.Number
    } else {
        KeyboardType.Text
    }

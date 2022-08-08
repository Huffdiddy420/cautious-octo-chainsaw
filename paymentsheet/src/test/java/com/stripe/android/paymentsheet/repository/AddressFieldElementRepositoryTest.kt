package com.stripe.android.paymentsheet.repository

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.common.truth.Truth.assertThat
import com.stripe.android.paymentsheet.repository.AddressFieldElementRepository.Companion.DEFAULT_COUNTRY_CODE
import com.stripe.android.paymentsheet.repository.AddressFieldElementRepository.Companion.supportedCountries
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestCoroutineDispatcher
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.mock
import java.io.File

@ExperimentalCoroutinesApi
class AddressFieldElementRepositoryTest {
    @get:Rule
    val rule = InstantTaskExecutorRule()

    private val testDispatcher = TestCoroutineDispatcher()

    private val transformAddressToSpec = TransformAddressToSpec(testDispatcher)
    private val addressFieldElementRepository = AddressFieldElementRepository(
        mock(),
        transformAddressToSpec
    )

    @Test
    fun `Default country should always be in the supported country list`() {
        assertThat(supportedCountries).contains("ZZ")
    }

    @Test
    fun `Country that doesn't exist return the default country`() {
        runBlocking {
            addressFieldElementRepository.init(
                listOf("ZZ").associateWith { countryCode ->
                    "src/main/assets/addressinfo/$countryCode.json"
                }
                    .mapValues { (_, assetFileName) ->
                        requireNotNull(
                            transformAddressToSpec.parseAddressesSchema(
                                File(assetFileName).inputStream()
                            )
                        )
                    }
            )

            assertThat(addressFieldElementRepository.get("GG"))
                .isEqualTo(addressFieldElementRepository.get(DEFAULT_COUNTRY_CODE))
        }
    }

    @Test
    fun `Correct supported country is returned`() {
        runBlocking {
            addressFieldElementRepository.init(
                supportedCountries.associateWith { countryCode ->
                    "src/main/assets/addressinfo/$countryCode.json"
                }
                    .mapValues { (_, assetFileName) ->
                        requireNotNull(
                            transformAddressToSpec.parseAddressesSchema(
                                File(assetFileName).inputStream()
                            )
                        )
                    }
            )

            assertThat(supportedCountries).doesNotContain("NB")

            assertThat(addressFieldElementRepository.get("NB"))
                .isEqualTo(addressFieldElementRepository.get(DEFAULT_COUNTRY_CODE))
        }
    }

    @Test
    fun `Verify only supported countries have json file`() {
        val files = File("src/main/assets/addressinfo").listFiles()

        if (files?.isEmpty() == false) {
            files.forEach {
                assertThat(supportedCountries).contains(it.nameWithoutExtension)
            }
        }
    }

    @Test
    fun `Verify all supported countries deserialize`() {
        runBlocking {
            addressFieldElementRepository.init(
                supportedCountries.associateWith { countryCode ->
                    "src/main/assets/addressinfo/$countryCode.json"
                }
                    .mapValues { (_, assetFileName) ->
                        requireNotNull(
                            transformAddressToSpec.parseAddressesSchema(
                                File(assetFileName).inputStream()
                            )
                        )
                    }
            )
        }
    }
}

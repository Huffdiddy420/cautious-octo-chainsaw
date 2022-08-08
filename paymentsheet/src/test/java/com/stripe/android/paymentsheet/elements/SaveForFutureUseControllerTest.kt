package com.stripe.android.paymentsheet.elements

import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Test

class SaveForFutureUseControllerTest {
    private val mandateIdentifier = com.stripe.android.ui.core.elements.IdentifierSpec.Generic("mandate")
    private val nameSectionIdentifier = com.stripe.android.ui.core.elements.IdentifierSpec.Name
    private val hiddenIdentifiers = listOf(nameSectionIdentifier, mandateIdentifier)
    private val saveForFutureUseController = SaveForFutureUseController(hiddenIdentifiers, true)

    @Test
    fun `Save for future use is initialized as false and mandate becomes hidden`() =
        runBlocking {
            val saveForFutureUseController = SaveForFutureUseController(hiddenIdentifiers, false)

            assertThat(saveForFutureUseController.saveForFutureUse.first()).isFalse()
            assertThat(saveForFutureUseController.hiddenIdentifiers.first())
                .isEqualTo(
                    listOf(
                        com.stripe.android.ui.core.elements.IdentifierSpec.Name,
                        com.stripe.android.ui.core.elements.IdentifierSpec.Generic("mandate")
                    )
                )
        }

    @Test
    fun `Save for future use is initialized as true and no hidden items`() =
        runBlocking {
            assertThat(saveForFutureUseController.saveForFutureUse.first()).isTrue()
            assertThat(saveForFutureUseController.hiddenIdentifiers.first()).isEmpty()
        }

    @Test
    fun `Save for future use when set to false shows the hidden items specified`() =
        runBlocking {
            saveForFutureUseController.onValueChange(false)
            assertThat(saveForFutureUseController.saveForFutureUse.first()).isFalse()
            assertThat(saveForFutureUseController.hiddenIdentifiers.first()).isEqualTo(
                hiddenIdentifiers
            )
        }
}

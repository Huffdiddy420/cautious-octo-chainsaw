package com.stripe.android.paymentsheet.paymentdatacollection.ach

import androidx.annotation.StringRes
import com.stripe.android.payments.paymentlauncher.PaymentResult


sealed class USBankAccountFormScreenState {
    data class NameAndEmailCollection(
        @StringRes val error: Int? = null
    ): USBankAccountFormScreenState()
    data class MandateCollection(
        val intentId: String,
        val linkedAccountId: String,
        val bankName: String?,
        val displayName: String?,
        val last4: String?
    ) : USBankAccountFormScreenState()
    data class VerifyWithMicrodeposits(
        val intentId: String,
        val linkedAccountId: String,
        val bankName: String?,
        val displayName: String?,
        val last4: String?
    ) : USBankAccountFormScreenState()
    data class FinishProcessing(
        val result: PaymentResult
    ) : USBankAccountFormScreenState()
    object Finished : USBankAccountFormScreenState()
}

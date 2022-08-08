package com.stripe.android.paymentsheet.paymentdatacollection.ach

sealed class USBankAccountFormScreenState {
    object NameAndEmailCollection : USBankAccountFormScreenState()
    data class MandateCollection(
        val bankName: String?,
        val displayName: String?,
        val last4: String?
    ) : USBankAccountFormScreenState()
}

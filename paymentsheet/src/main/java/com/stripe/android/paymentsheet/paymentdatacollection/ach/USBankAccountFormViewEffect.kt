package com.stripe.android.paymentsheet.paymentdatacollection.ach

internal sealed class USBankAccountFormViewEffect {
    object Init : USBankAccountFormViewEffect()
    data class RequiredFieldsCollected(
        val name: String
    ) : USBankAccountFormViewEffect()
    object BankAccountCollected : USBankAccountFormViewEffect()
    data class Error(
        val message: String?
    ) : USBankAccountFormViewEffect()
}

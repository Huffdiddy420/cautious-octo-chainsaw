package com.stripe.android.paymentsheet.model

import com.stripe.android.paymentsheet.PaymentSheetActivity
import com.stripe.android.paymentsheet.viewmodels.BaseSheetViewModel

/**
 * This will show the state of the [PaymentSheetActivity] as it does work.  The states always
 * progress as follows: Ready -> StartProcessing -> FinishProcessing -> ProcessResult
 */
internal sealed class PaymentSheetViewState(
    val errorMessage: BaseSheetViewModel.UserErrorMessage? = null,
) {
    data class Reset(
        private val message: BaseSheetViewModel.UserErrorMessage? = null
    ) : PaymentSheetViewState(message)

    data class PreProcessing(val step: Step) : PaymentSheetViewState()

    object StartProcessing : PaymentSheetViewState()

    data class FinishProcessing(
        val onComplete: () -> Unit
    ) : PaymentSheetViewState()
}

interface Step {
    var currentStep: Step?
    var buttonText: String?
}

sealed class USBankAccountStep : Step {
    override var currentStep: Step? = NameAndEmailCollection
    override var buttonText: String? = null

    object NameAndEmailCollection : USBankAccountStep() {
        override var buttonText: String? = "Begin linking account"
    }
    object BankAccountCollection : USBankAccountStep()
    data class MandateCollection(
        val accountDetails: String
    ) : USBankAccountStep()
}

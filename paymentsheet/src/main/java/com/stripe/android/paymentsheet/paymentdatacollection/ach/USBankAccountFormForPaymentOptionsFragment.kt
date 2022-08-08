package com.stripe.android.paymentsheet.paymentdatacollection.ach

import android.view.ViewGroup
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.stripe.android.paymentsheet.PaymentOptionsActivity
import com.stripe.android.paymentsheet.R
import com.stripe.android.paymentsheet.ui.PrimaryButton
import com.stripe.android.paymentsheet.viewmodels.BaseSheetViewModel

/**
 * Fragment that displays a form for us_bank_account payment data collection in payment options flow
 */
internal class USBankAccountFormForPaymentOptionsFragment : USBankAccountFormFragment() {
    override val sheetViewModel: BaseSheetViewModel<*>? by lazy {
        (requireActivity() as? PaymentOptionsActivity)?.viewModel
    }
    override val bottomPadding: Dp = 26.dp
    override val labelProvider: (PrimaryButton) -> Unit = {
        it.setLabel(getString(R.string.stripe_paymentsheet_continue_button_label))
    }
    override val lockVisible: Boolean = false
    override val primaryButtonHeight: Int = ViewGroup.LayoutParams.WRAP_CONTENT
    override val primaryButtonTopPadding: Dp = 10.dp
    override val primaryButtonBottomPadding: Dp = 4.dp

    override fun mandateCollectionOnClick(intentId: String, linkedAccountId: String) {
        clientSecret?.let {
            viewModel.attach(it, intentId, linkedAccountId)
        }
    }

    override fun verifyWithMicrodepositsOnClick(intentId: String, linkedAccountId: String) {
        clientSecret?.let {
            viewModel.attach(it, intentId, linkedAccountId)
        }
    }
}

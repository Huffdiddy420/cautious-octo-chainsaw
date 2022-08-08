package com.stripe.android.paymentsheet.paymentdatacollection.ach

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.stripe.android.paymentsheet.PaymentSheetActivity
import com.stripe.android.paymentsheet.R
import com.stripe.android.paymentsheet.ui.PrimaryButton
import com.stripe.android.paymentsheet.viewmodels.BaseSheetViewModel

/**
 * Fragment that displays a form for us_bank_account payment data collection in payment sheet flow
 */
internal class USBankAccountFormForPaymentSheetFragment : USBankAccountFormFragment() {
    override val sheetViewModel: BaseSheetViewModel<*>? by lazy {
        (requireActivity() as? PaymentSheetActivity)?.viewModel
    }
    override val bottomPadding: Dp = 0.dp
    override val labelProvider: (PrimaryButton) -> Unit = { }
    override val lockVisible: Boolean = true
    override val primaryButtonHeight: Int by lazy {
        requireContext().resources.getDimensionPixelSize(R.dimen.stripe_paymentsheet_primary_button_height)
    }
    override val primaryButtonTopPadding: Dp = 12.dp
    override val primaryButtonBottomPadding: Dp = 4.dp

    override fun mandateCollectionOnClick(intentId: String, linkedAccountId: String) {
        clientSecret?.let {
            viewModel.attach(it, intentId, linkedAccountId)
            viewModel.confirm(it)
        }
    }

    override fun verifyWithMicrodepositsOnClick(intentId: String, linkedAccountId: String) {
        clientSecret?.let {
            viewModel.attach(it, intentId, linkedAccountId)
            viewModel.confirm(it)
        }
    }
}

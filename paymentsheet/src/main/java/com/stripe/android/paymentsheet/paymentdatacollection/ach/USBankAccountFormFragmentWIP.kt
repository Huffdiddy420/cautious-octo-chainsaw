package com.stripe.android.paymentsheet.paymentdatacollection.ach

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.stripe.android.model.PaymentIntent
import com.stripe.android.model.PaymentMethod
import com.stripe.android.model.PaymentMethodCreateParams
import com.stripe.android.model.SetupIntent
import com.stripe.android.payments.bankaccount.navigation.CollectBankAccountResult
import com.stripe.android.payments.paymentlauncher.PaymentResult
import com.stripe.android.paymentsheet.PaymentOptionsActivity
import com.stripe.android.paymentsheet.PaymentSheetActivity
import com.stripe.android.paymentsheet.R
import com.stripe.android.paymentsheet.model.ClientSecret
import com.stripe.android.paymentsheet.model.PaymentIntentClientSecret
import com.stripe.android.paymentsheet.model.PaymentSelection
import com.stripe.android.paymentsheet.model.SetupIntentClientSecret
import com.stripe.android.paymentsheet.paymentdatacollection.ComposeFormDataCollectionFragment
import com.stripe.android.paymentsheet.paymentdatacollection.FormFragmentArguments
import com.stripe.android.paymentsheet.ui.PrimaryButton
import com.stripe.android.ui.core.PaymentsTheme
import com.stripe.android.ui.core.PaymentsThemeDefaults
import com.stripe.android.ui.core.elements.H6Text
import com.stripe.android.ui.core.elements.Html
import com.stripe.android.ui.core.elements.IdentifierSpec
import com.stripe.android.ui.core.elements.SectionCard
import com.stripe.android.ui.core.elements.SectionController
import com.stripe.android.ui.core.elements.SectionElement
import com.stripe.android.ui.core.elements.SectionElementUI
import com.stripe.android.ui.core.isSystemDarkTheme

/**
 * Fragment that displays a form for us_bank_account payment data collection
 */
internal class USBankAccountFormFragmentWIP : Fragment() {

    private val formArgs by lazy {
        requireNotNull(
            requireArguments().getParcelable<FormFragmentArguments>(
                ComposeFormDataCollectionFragment.EXTRA_CONFIG
            )
        )
    }

    private val isPaymentSheet by lazy {
        requireActivity() is PaymentSheetActivity
    }

    private val sheetViewModel by lazy {
        (requireActivity() as? PaymentSheetActivity)?.viewModel ?:
            (requireActivity() as? PaymentOptionsActivity)?.viewModel
    }

    private val clientSecret by lazy {
        when (val intent = sheetViewModel?.stripeIntent?.value) {
            is PaymentIntent -> PaymentIntentClientSecret(intent.clientSecret!!)
            is SetupIntent -> SetupIntentClientSecret(intent.clientSecret!!)
            else -> null
        }
    }

    val viewModel by viewModels<USBankAccountFormViewModel> {
        USBankAccountFormViewModel.Factory(
            { requireActivity().application },
            { USBankAccountFormViewModel.Args(formArgs, isPaymentSheet) },
            this
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.registerFragment(this)
        sheetViewModel?.shouldHidePrimaryButton?.postValue(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = ComposeView(inflater.context).apply {
        layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        lifecycleScope.launchWhenStarted {
            viewModel.currentScreenState.collect { screenState ->
                when (screenState) {
                    is USBankAccountFormScreenState.NameAndEmailCollection -> {
                        setContent {
                            PaymentsTheme {
                                NameAndEmailCollectionScreen(screenState.error)
                            }
                        }
                    }
                    is USBankAccountFormScreenState.MandateCollection -> {
                        setContent {
                            PaymentsTheme {
                                MandateCollectionScreen(
                                    screenState.bankName,
                                    screenState.displayName,
                                    screenState.last4,
                                    screenState.intentId,
                                    screenState.linkedAccountId,
                                )
                            }
                        }
                    }
                    is USBankAccountFormScreenState.VerifyWithMicrodeposits -> {
                        val formattedMerchantName = formArgs.merchantName.trimEnd { it == '.' }
                        setContent {
                            PaymentsTheme {
                                VerifyWithMicrodepositsScreen(
                                    screenState.bankName,
                                    screenState.displayName,
                                    screenState.last4,
                                    screenState.intentId,
                                    screenState.linkedAccountId,
                                    formattedMerchantName
                                )
                            }
                        }
                    }
                    is USBankAccountFormScreenState.FinishProcessing -> {
                        sheetViewModel?.onPaymentResult(screenState.result)
                        sheetViewModel?.onFinish()
                    }
                    is USBankAccountFormScreenState.Finished -> {
                        sheetViewModel?.updateSelection(
                            PaymentSelection.New.GenericPaymentMethod(
                                labelResource = R.string.stripe_paymentsheet_payment_method_us_bank_account,
                                iconResource = R.drawable.stripe_ic_bank,
                                paymentMethodCreateParams = PaymentMethodCreateParams.createUSBankAccount(
                                    PaymentMethod.BillingDetails(
                                        name = viewModel.name.value,
                                        email = viewModel.email.value
                                    )
                                ),
                                customerRequestedSave = PaymentSelection.CustomerRequestedSave.RequestReuse
                            )
                        )
                        sheetViewModel?.onFinish()
                    }
                }
            }
        }
    }

    override fun onDetach() {
        viewModel.onDestroy()
        sheetViewModel?.shouldHidePrimaryButton?.postValue(false)
        super.onDetach()
    }

    @Composable
    private fun NameAndEmailCollectionScreen(@StringRes error: Int? = null) {
        Column(Modifier.fillMaxWidth().padding(bottom = if (!isPaymentSheet) 30.dp else 0.dp)) {
            NameAndEmailForm()
            error?.let {
                sheetViewModel?.onError(error)
            }
            PrimaryButton(
                onClick = {
                    clientSecret?.let {
                        viewModel.collectBankAccount(it)
                    }
                }
            )
        }
    }

    @Composable
    private fun MandateCollectionScreen(
        bankName: String?,
        displayName: String?,
        last4: String?,
        intentId: String,
        linkedAccountId: String,
    ) {
        Column(Modifier.fillMaxWidth().padding(bottom = if (!isPaymentSheet) 30.dp else 0.dp)) {
            NameAndEmailForm()
            AccountDetailsForm(bankName, displayName, last4)
            PrimaryButton(
                onClick = {
                    clientSecret?.let {
                        viewModel.attach(it, intentId, linkedAccountId)
                        if (isPaymentSheet) {
                            viewModel.confirm(it)
                        }
                    }
                }
            )
            VerifiedMandate()
        }
    }

    @Composable
    private fun VerifyWithMicrodepositsScreen(
        bankName: String?,
        displayName: String?,
        last4: String?,
        intentId: String,
        linkedAccountId: String,
        formattedMerchantName: String
    ) {
        Column(Modifier.fillMaxWidth().padding(bottom = if (!isPaymentSheet) 30.dp else 0.dp)) {
            NameAndEmailForm()
            AccountDetailsForm(bankName, displayName, last4)
            PrimaryButton(
                onClick = {
                    clientSecret?.let {
                        viewModel.attach(it, intentId, linkedAccountId)
                        if (isPaymentSheet) {
                            viewModel.confirm(it)
                        }
                    }
                }
            )
            VerifyWithMicrodespoitsMandate(formattedMerchantName = formattedMerchantName)
        }
    }

    @Composable
    private fun NameAndEmailForm() {
        Column(Modifier.fillMaxWidth()) {
            H6Text(
                text = stringResource(R.string.us_bank_account_payment_sheet_title),
                modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(0.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                SectionElementUI(
                    enabled = true,
                    element = SectionElement(
                        identifier = IdentifierSpec.Name,
                        fields = listOf(viewModel.nameElement),
                        controller = SectionController(
                            null,
                            listOf(viewModel.nameElement.sectionFieldErrorController())
                        )
                    ),
                    emptyList(),
                    viewModel.nameElement.identifier
                )
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(0.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                SectionElementUI(
                    enabled = true,
                    element = SectionElement(
                        identifier = IdentifierSpec.Email,
                        fields = listOf(viewModel.emailElement),
                        controller = SectionController(
                            null,
                            listOf(viewModel.emailElement.sectionFieldErrorController())
                        )
                    ),
                    emptyList(),
                    viewModel.emailElement.identifier
                )
            }
        }
    }

    @Composable
    private fun AccountDetailsForm(
        bankName: String?,
        displayName: String?,
        last4: String?
    ) {
        val openDialog = remember { mutableStateOf(false) }
        val bankIcon = TransformToBankIcon(bankName ?: "")

        Column(
            Modifier.fillMaxWidth()
        ) {
            H6Text(
                text = stringResource(R.string.us_bank_account_payment_sheet_bank_account),
                modifier = Modifier.padding(vertical = 8.dp)
            )
            SectionCard(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(all = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(bankIcon ?: R.drawable.stripe_ic_bank),
                            contentDescription = null,
                            modifier = Modifier
                                .height(40.dp)
                                .width(56.dp)
                        )
                        Text(text = "$displayName ••••$last4", fontWeight = FontWeight.Bold)
                    }
                    Image(
                        painter = painterResource(R.drawable.stripe_ic_clear),
                        contentDescription = null,
                        modifier = Modifier
                            .height(20.dp)
                            .width(20.dp)
                            .clickable {
                                openDialog.value = true
                            }
                    )
                }
            }
        }
        if (openDialog.value) {
            AlertDialog(
                onDismissRequest = {
                    openDialog.value = false
                },
                confirmButton = {
                    Button(onClick = {
                        openDialog.value = false
                        viewModel.reset()
                    }) {
                        Text(stringResource(id = R.string.us_bank_account_payment_sheet_alert_remove))
                    }
                },
                dismissButton = {
                    Button(onClick = {
                        openDialog.value = false
                    }) {
                        Text(stringResource(id = R.string.us_bank_account_payment_sheet_alert_cancel))
                    }
                },
                title = {
                    Text(stringResource(id = R.string.us_bank_account_payment_sheet_alert_title))
                },
                text = {
                    last4?.let {
                        Text(text = stringResource(id = R.string.us_bank_account_payment_sheet_alert_text, last4))
                    }
                }
            )
        }
    }

    @Composable
    private fun VerifiedMandate() {
        Html(
            html = stringResource(R.string.us_bank_account_payment_sheet_mandate),
            imageGetter = emptyMap(),
            color = PaymentsTheme.colors.subtitle,
            style = PaymentsTheme.typography.body1
        )
    }

    @Composable
    private fun VerifyWithMicrodespoitsMandate(formattedMerchantName: String) {
        Html(
            html = stringResource(
                R.string.us_bank_account_payment_sheet_mandate_verify_with_microdeposit,
                formattedMerchantName
            ),
            imageGetter = emptyMap(),
            color = PaymentsTheme.colors.subtitle,
            style = PaymentsTheme.typography.body1
        )
    }

    @Composable
    private fun PrimaryButton(
        onClick: () -> Unit,
    ) {
        val enabled = viewModel.requiredFields.collectAsState(initial = false)
        val state = viewModel.primaryButtonState.collectAsState(initial = PrimaryButton.State.Ready())

        AndroidView(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    top = 12.dp,
                    bottom = 4.dp
                ),
            factory = { context ->
                PrimaryButton(context).apply {
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        if (isPaymentSheet) {
                            context.resources.getDimensionPixelSize(R.dimen.stripe_paymentsheet_primary_button_height)
                        } else {
                            ViewGroup.LayoutParams.WRAP_CONTENT
                        }
                    )

                    if (!isPaymentSheet) {
                        setLabel(getString(R.string.stripe_paymentsheet_continue_button_label))
                    }

                    setCornerRadius(PaymentsThemeDefaults.shapes.cornerRadius)
                    setDefaultBackGroundColor(
                        sheetViewModel?.config?.primaryButtonColor ?: ColorStateList.valueOf(
                            PaymentsThemeDefaults.colors(context.isSystemDarkTheme()).primary.toArgb()
                        )
                    )
                    updateState(state.value)
                    setOnClickListener {
                        updateState(PrimaryButton.State.StartProcessing)
                        onClick()
                    }
                    isEnabled = enabled.value
                    lockVisible = isPaymentSheet
                }
            },
            update = { primaryButton ->
                primaryButton.updateState(state.value)
                primaryButton.setOnClickListener {
                    primaryButton.updateState(PrimaryButton.State.StartProcessing)
                    onClick()
                }
                primaryButton.isEnabled = enabled.value
            }
        )
    }
}

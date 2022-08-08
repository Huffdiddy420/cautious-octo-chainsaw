package com.stripe.android.paymentsheet.paymentdatacollection.ach

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.stripe.android.PaymentConfiguration
import com.stripe.android.connections.model.BankAccount
import com.stripe.android.connections.model.LinkedAccount
import com.stripe.android.payments.bankaccount.CollectBankAccountConfiguration
import com.stripe.android.payments.bankaccount.CollectBankAccountLauncher
import com.stripe.android.payments.bankaccount.navigation.CollectBankAccountResult
import com.stripe.android.paymentsheet.PaymentSheetActivity
import com.stripe.android.paymentsheet.PaymentSheetViewModel
import com.stripe.android.paymentsheet.R
import com.stripe.android.ui.core.PaymentsTheme
import com.stripe.android.ui.core.elements.SectionCard

/**
 * Fragment that displays a form for us_bank_account payment data collection
 */
internal class USBankAccountFormFragment : Fragment() {

    private val viewModelFactory: ViewModelProvider.Factory = PaymentSheetViewModel.Factory(
        { requireActivity().application },
        {
            requireNotNull(
                requireArguments().getParcelable(PaymentSheetActivity.EXTRA_STARTER_ARGS)
            )
        },
        (activity as? AppCompatActivity) ?: this,
        (activity as? AppCompatActivity)?.intent?.extras
    )

    private val sheetViewModel by activityViewModels<PaymentSheetViewModel> {
        viewModelFactory
    }

    private val viewModel by viewModels<USBankAccountFormViewModel> {
        USBankAccountFormViewModel.Factory(this)
    }

    private lateinit var collectBankAccountLauncher: CollectBankAccountLauncher

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        collectBankAccountLauncher = CollectBankAccountLauncher.create(
            this@USBankAccountFormFragment
        ) { result ->
            when (result) {
                is CollectBankAccountResult.Completed -> {
                    val paymentAccount = result.response.paymentAccount
                    val bankName = (paymentAccount as? LinkedAccount)?.institutionName
                        ?: (paymentAccount as? BankAccount)?.bankName
                    val displayName = (paymentAccount as? LinkedAccount)?.displayName
                        ?: (paymentAccount as? BankAccount)?.routingNumber
                    val last4 = (paymentAccount as? LinkedAccount)?.last4
                        ?: (paymentAccount as? BankAccount)?.last4

                    viewModel.updateScreenState(
                        USBankAccountFormScreenState.MandateCollection(
                            bankName = bankName,
                            displayName = displayName,
                            last4 = last4
                        )
                    )
                }
                is CollectBankAccountResult.Failed -> {
                    sheetViewModel.handleUSBankAccountFormViewEffect(
                        USBankAccountFormViewEffect.Error(
                            getString(R.string.us_bank_account_payment_sheet_something_went_wrong)
                        )
                    )
                }
                is CollectBankAccountResult.Cancelled -> {
                    sheetViewModel.handleUSBankAccountFormViewEffect(
                        USBankAccountFormViewEffect.Error(null)
                    )
                }
            }
        }
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
            sheetViewModel.handleUSBankAccountFormViewEffect(USBankAccountFormViewEffect.Init)
            viewModel.currentScreenState.collect { screenState ->
                when (screenState) {
                    is USBankAccountFormScreenState.NameAndEmailCollection -> {
                        sheetViewModel.primaryButtonPressed.removeObservers(viewLifecycleOwner)
                        sheetViewModel.primaryButtonPressed.observe(viewLifecycleOwner) {
                            collectBankAccountLauncher.presentWithPaymentIntent(
                                PaymentConfiguration.getInstance(requireContext()).publishableKey,
                                sheetViewModel.args.clientSecret.value,
                                CollectBankAccountConfiguration.USBankAccount(
                                    viewModel.name.value,
                                    viewModel.email.value
                                )
                            )
                        }
                        setContent {
                            PaymentsTheme {
                                NameAndEmailCollectionScreen()
                            }
                        }
                    }
                    is USBankAccountFormScreenState.MandateCollection -> {
                        sheetViewModel.primaryButtonPressed.removeObservers(viewLifecycleOwner)
                        sheetViewModel.primaryButtonPressed.observe(viewLifecycleOwner) {
                            sheetViewModel.handleUSBankAccountFormViewEffect(
                                USBankAccountFormViewEffect.BankAccountCollected
                            )
                        }
                        setContent {
                            PaymentsTheme {
                                MandateCollectionScreen(
                                    screenState.bankName,
                                    screenState.displayName,
                                    screenState.last4
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        sheetViewModel.detachPaymentMethod()
        super.onDestroy()
    }

    @Composable
    private fun NameAndEmailCollectionScreen() {
        NameAndEmailForm()
    }

    @Composable
    private fun MandateCollectionScreen(
        bankName: String?,
        displayName: String?,
        last4: String?
    ) {
        Column(Modifier.fillMaxSize()) {
            NameAndEmailForm()
            AccountDetailsForm(bankName, displayName, last4)
        }
    }

    @Composable
    private fun NameAndEmailForm() {
        val name by viewModel.name.collectAsState("")
        val email by viewModel.email.collectAsState("")

        Column(Modifier.fillMaxSize()) {
            SectionCard(modifier = Modifier.padding(top = 16.dp)) {
                TextField(
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Full name") },
                    value = name,
                    onValueChange = {
                        viewModel.updateName(it)
                        sheetViewModel.handleUSBankAccountFormViewEffect(
                            USBankAccountFormViewEffect.RequiredFieldsCollected(
                                name = name
                            )
                        )
                    },
                    colors = textFieldColors()
                )
            }
            SectionCard(modifier = Modifier.padding(top = 16.dp)) {
                TextField(
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Email") },
                    value = email,
                    onValueChange = {
                        viewModel.updateEmail(it)
                    },
                    colors = textFieldColors()
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
        SectionCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(all = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(R.drawable.stripe_ic_bank),
                    contentDescription = null,
                    modifier = Modifier
                        .height(40.dp)
                        .width(56.dp)
                )
                Text(text = "$bankName $displayName ••••$last4")
                Image(
                    painter = painterResource(R.drawable.stripe_ic_trash),
                    contentDescription = null,
                    modifier = Modifier
                        .height(40.dp)
                        .width(56.dp)
                )
            }
        }
    }

    @Composable
    private fun textFieldColors() =
        TextFieldDefaults.textFieldColors(
            backgroundColor = MaterialTheme.colors.background,
            focusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
        )
}

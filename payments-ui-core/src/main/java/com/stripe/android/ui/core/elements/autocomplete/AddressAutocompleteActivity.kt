package com.stripe.android.ui.core.elements.autocomplete

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import com.stripe.android.model.Address
import com.stripe.android.ui.core.DefaultPaymentsTheme
import com.stripe.android.ui.core.R
import com.stripe.android.ui.core.elements.AddressAutocompleteTextField
import com.stripe.android.ui.core.elements.AddressAutocompleteTextFieldController
import com.stripe.android.ui.core.paymentsColors

class AddressAutocompleteActivity : AppCompatActivity() {

    val args by lazy {
        requireNotNull(
            AddressAutocompleteContract.Args.fromIntent(intent)
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            DefaultPaymentsTheme {
                AddressAutocompleteScreen()
            }
        }
    }

    @Composable
    fun AddressAutocompleteScreen() {
        Scaffold(
            bottomBar = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .background(
                            color = colorResource(
                                id = R.color.stripe_paymentsheet_shipping_address_background
                            )
                        )
                        .fillMaxWidth()
                        .imePadding()
                        .navigationBarsPadding()
                        .padding(vertical = 8.dp)
                ) {
                    ClickableText(
                        text = buildAnnotatedString {
                            append(
                                stringResource(
                                    id = R.string.stripe_paymentsheet_enter_address_manually
                                )
                            )
                        },
                        style = TextStyle.Default.copy(
                            color = MaterialTheme.paymentsColors.materialColors.primary
                        )
                    ) {
                        val autoCompleteResult =
                            AddressAutocompleteResult.Succeeded(Address.Builder().build())
                        setResult(
                            autoCompleteResult.resultCode,
                            Intent().putExtras(autoCompleteResult.toBundle())
                        )
                        finish()
                    }
                }
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .systemBarsPadding()
                    .padding(paddingValues)
            ) {
                AddressAutocompleteTextField(
                    controller = AddressAutocompleteTextFieldController(
                        context = this@AddressAutocompleteActivity,
                        country = args.country,
                        googlePlacesApiKey = args.googlePlacesApiKey,
                        workerScope = lifecycleScope
                    ),
                ) {
                    val autoCompleteResult =
                        AddressAutocompleteResult.Succeeded(it)
                    setResult(
                        autoCompleteResult.resultCode,
                        Intent().putExtras(autoCompleteResult.toBundle())
                    )
                    finish()
                }
            }
        }
    }
}
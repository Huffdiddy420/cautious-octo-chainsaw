package com.stripe.example.activity

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.stripe.android.googlepaylauncher.GooglePayEnvironment
import com.stripe.android.googlepaylauncher.GooglePayLauncher

class GooglePayLauncherComposeActivity : StripeIntentActivity() {
    private val googlePayConfig = GooglePayLauncher.Config(
        environment = GooglePayEnvironment.Test,
        merchantCountryCode = COUNTRY_CODE,
        merchantName = "Widget Store",
        billingAddressConfig = GooglePayLauncher.BillingAddressConfig(
            isRequired = true,
            format = GooglePayLauncher.BillingAddressConfig.Format.Full,
            isPhoneNumberRequired = false
        ),
        existingPaymentMethodRequired = false
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            var clientSecret by rememberSaveable { mutableStateOf("") }
            var googlePayReady by rememberSaveable { mutableStateOf<Boolean?>(null) }
            var completed by rememberSaveable { mutableStateOf(false) }
            var log by rememberSaveable { mutableStateOf("") }

            LaunchedEffect(Unit) {
                if (clientSecret.isBlank()) {
                    viewModel.createPaymentIntent(COUNTRY_CODE).observe(
                        this@GooglePayLauncherComposeActivity
                    ) { result ->
                        result.fold(
                            onSuccess = { json ->
                                clientSecret = json.getString("secret")
                                log = log.plus("Fetched client secret $clientSecret\n\n")
                            },
                            onFailure = { error ->
                                completed = true
                                log =
                                    log.plus("Could not create PaymentIntent: ${error.message}\n\n")
                            }
                        )
                    }
                }
            }

            val readyCallback = remember {
                GooglePayLauncher.ReadyCallback { ready ->
                    if (googlePayReady == null) {
                        googlePayReady = ready

                        if (!ready) {
                            completed = true
                        }

                        log = log.plus("Google Pay ready? $ready\n\n")
                    }
                }
            }

            val resultCallback = remember {
                GooglePayLauncher.ResultCallback { result ->
                    when (result) {
                        GooglePayLauncher.Result.Completed -> {
                            completed = true
                            "Successfully collected payment."
                        }
                        GooglePayLauncher.Result.Canceled -> {
                            "Customer cancelled Google Pay."
                        }
                        is GooglePayLauncher.Result.Failed -> {
                            "Google Pay failed: ${result.error.message}"
                        }
                    }.let {
                        log = log.plus("$it\n\n")
                    }
                }
            }

            val googlePayLauncher = GooglePayLauncher.rememberLauncher(
                config = googlePayConfig,
                readyCallback = readyCallback,
                resultCallback = resultCallback
            )

            GooglePayScreen(
                googlePayReady = googlePayReady,
                clientSecret = clientSecret,
                completed = completed,
                log = log,
                onGooglePayButtonClick = {
                    googlePayLauncher.presentForPaymentIntent(clientSecret)
                }
            )
        }
    }

    @Composable
    private fun GooglePayScreen(
        googlePayReady: Boolean?,
        clientSecret: String,
        completed: Boolean,
        log: String,
        onGooglePayButtonClick: () -> Unit
    ) {
        Column(Modifier.fillMaxWidth()) {
            if (googlePayReady == null || clientSecret.isBlank()) {
                LinearProgressIndicator(Modifier.fillMaxWidth())
            }

            Spacer(
                Modifier
                    .height(8.dp)
                    .fillMaxWidth()
            )

            AndroidView(
                factory = { context ->
                    GooglePayButton(context)
                },
                modifier = Modifier
                    .wrapContentWidth()
                    .clickable(
                        enabled = googlePayReady == true &&
                            clientSecret.isNotBlank() && !completed,
                        onClick = onGooglePayButtonClick
                    )
            )

            Text(text = log)
        }
    }

    private companion object {
        private const val COUNTRY_CODE = "US"
    }
}

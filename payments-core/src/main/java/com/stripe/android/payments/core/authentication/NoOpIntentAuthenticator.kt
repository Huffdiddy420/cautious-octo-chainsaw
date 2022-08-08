package com.stripe.android.payments.core.authentication

import com.stripe.android.PaymentRelayStarter
import com.stripe.android.model.StripeIntent
import com.stripe.android.networking.ApiRequest
import com.stripe.android.view.AuthActivityStarterHost
import javax.inject.Inject
import javax.inject.Singleton

/**
 * [IntentAuthenticator] implementation to perform no-op, just return to client's host.
 */
@Singleton
internal class NoOpIntentAuthenticator @Inject constructor(
    private val paymentRelayStarterFactory: (AuthActivityStarterHost) -> PaymentRelayStarter,
) : IntentAuthenticator {

    override suspend fun authenticate(
        host: AuthActivityStarterHost,
        stripeIntent: StripeIntent,
        threeDs1ReturnUrl: String?,
        requestOptions: ApiRequest.Options
    ) {
        paymentRelayStarterFactory(host)
            .start(
                PaymentRelayStarter.Args.create(stripeIntent, requestOptions.stripeAccount)
            )
    }
}

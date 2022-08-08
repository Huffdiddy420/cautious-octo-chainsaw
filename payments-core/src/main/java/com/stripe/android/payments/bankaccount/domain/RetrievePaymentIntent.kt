package com.stripe.android.payments.bankaccount.domain

import com.stripe.android.core.networking.ApiRequest
import com.stripe.android.model.PaymentIntent
import com.stripe.android.networking.StripeRepository
import javax.inject.Inject

internal class RetrievePaymentIntent @Inject constructor(
    private val stripeRepository: StripeRepository
) {
    suspend fun get(clientSecret: String, publishableKey: String): Result<PaymentIntent> = kotlin.runCatching {
        stripeRepository.retrievePaymentIntent(
            clientSecret = clientSecret,
            options = ApiRequest.Options(publishableKey)
        )
    }.mapCatching { it ?: throw InternalError("Error retrieving PaymentIntent") }
}

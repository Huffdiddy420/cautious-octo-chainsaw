package com.stripe.android.payments.core.authentication.threeds2

import android.content.Context
import android.content.Intent
import android.os.Parcelable
import androidx.activity.result.contract.ActivityResultContract
import androidx.core.os.bundleOf
import com.stripe.android.model.StripeIntent
import com.stripe.android.networking.ApiRequest
import com.stripe.android.payments.PaymentFlowResult
import kotlinx.parcelize.Parcelize

internal class Stripe3ds2TransactionContract :
    ActivityResultContract<Stripe3ds2TransactionContract.Args, PaymentFlowResult.Unvalidated>() {
    override fun createIntent(
        context: Context,
        input: Args
    ): Intent {
        return Intent(context, Stripe3ds2TransactionActivity::class.java)
            .putExtras(input.toBundle())
    }

    override fun parseResult(
        resultCode: Int,
        intent: Intent?
    ): PaymentFlowResult.Unvalidated {
        return PaymentFlowResult.Unvalidated.fromIntent(intent)
    }

    @Parcelize
    data class Args(
        val stripeIntent: StripeIntent,
        val nextActionData: StripeIntent.NextActionData.SdkData.Use3DS2,
        val threeDs1ReturnUrl: String?,
        val requestOptions: ApiRequest.Options
    ) : Parcelable {
        fun toBundle() = bundleOf(EXTRA_ARGS to this)

        internal companion object {
            private const val EXTRA_ARGS = "extra_args"

            fun fromIntent(intent: Intent): Args? {
                return intent.getParcelableExtra(EXTRA_ARGS)
            }
        }
    }
}

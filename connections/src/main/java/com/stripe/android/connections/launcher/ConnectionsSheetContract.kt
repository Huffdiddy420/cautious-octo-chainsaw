package com.stripe.android.connections.launcher

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import androidx.activity.result.contract.ActivityResultContract
import androidx.core.os.bundleOf
import com.stripe.android.connections.ConnectionsSheet
import com.stripe.android.connections.presentation.ConnectionsSheetActivity
import com.stripe.android.connections.model.LinkAccountSession
import com.stripe.android.model.Token
import kotlinx.parcelize.Parcelize
import java.security.InvalidParameterException

internal class ConnectionsSheetContract :
    ActivityResultContract<ConnectionsSheetContract.Args, ConnectionsSheetContract.Result>() {

    override fun createIntent(
        context: Context,
        input: Args
    ): Intent {
        return Intent(context, ConnectionsSheetActivity::class.java).putExtra(EXTRA_ARGS, input)
    }

    override fun parseResult(
        resultCode: Int,
        intent: Intent?
    ): Result {
        return intent?.getParcelableExtra(EXTRA_RESULT) ?: Result.Failed(
            IllegalArgumentException("Failed to retrieve a ConnectionsSheetResult.")
        )
    }

    sealed class Args constructor(
        open val configuration: ConnectionsSheet.Configuration,
    ) : Parcelable {

        @Parcelize
        data class Default(
            override val configuration: ConnectionsSheet.Configuration
        ) : Args(configuration)

        @Parcelize
        data class ForToken(
            override val configuration: ConnectionsSheet.Configuration
        ) : Args(configuration)

        fun validate() {
            if (configuration.linkAccountSessionClientSecret.isBlank()) {
                throw InvalidParameterException(
                    "The link account session client secret cannot be an empty string."
                )
            }
            if (configuration.publishableKey.isBlank()) {
                throw InvalidParameterException(
                    "The publishable key cannot be an empty string."
                )
            }
        }

        companion object {
            internal fun fromIntent(intent: Intent): Args? {
                return intent.getParcelableExtra(EXTRA_ARGS)
            }
        }
    }

    internal sealed class Result : Parcelable {
        /**
         * The customer completed the connections session.
         * @param linkAccountSession The link account session connected
         */
        @Parcelize
        data class Completed(
            val linkAccountSession: LinkAccountSession,
            val token: Token? = null
        ) : Result()

        /**
         * The customer canceled the connections session attempt.
         */
        @Parcelize
        object Canceled : Result()

        /**
         * The connections session attempt failed.
         * @param error The error encountered by the customer.
         */
        @Parcelize
        data class Failed(
            val error: Throwable
        ) : Result()

        fun toBundle(): Bundle {
            return bundleOf(EXTRA_RESULT to this)
        }
    }

    companion object {
        const val EXTRA_ARGS =
            "com.stripe.android.connections.launcher.ConnectionsSheetContract.extra_args"
        private const val EXTRA_RESULT =
            "com.stripe.android.connections.launcher.ConnectionsSheetContract.extra_result"
    }
}
package com.stripe.android.financialconnections.features.consent

import com.airbnb.mvrx.MavericksViewModel
import com.airbnb.mvrx.MavericksViewModelFactory
import com.airbnb.mvrx.ViewModelContext
import com.stripe.android.core.Logger
import com.stripe.android.financialconnections.di.financialConnectionsSubComponentBuilderProvider
import com.stripe.android.financialconnections.domain.AcceptConsent
import com.stripe.android.financialconnections.domain.NativeAuthFlowCoordinator
import com.stripe.android.financialconnections.domain.NativeAuthFlowCoordinator.Message.RequestNextStep
import com.stripe.android.financialconnections.domain.NativeAuthFlowCoordinator.Message.UpdateManifest
import com.stripe.android.financialconnections.features.consent.ConsentState.ViewEffect.OpenUrl
import com.stripe.android.financialconnections.model.FinancialConnectionsSessionManifest
import com.stripe.android.financialconnections.navigation.NavigationDirections
import com.stripe.android.financialconnections.presentation.FinancialConnectionsUrls
import kotlinx.coroutines.launch
import javax.inject.Inject

internal class ConsentViewModel @Inject constructor(
    initialState: ConsentState,
    private val acceptConsent: AcceptConsent,
    private val nativeAuthFlowCoordinator: NativeAuthFlowCoordinator,
    private val logger: Logger
) : MavericksViewModel<ConsentState>(initialState) {

    fun onContinueClick() {
        viewModelScope.launch {
            val manifest: FinancialConnectionsSessionManifest = acceptConsent()
            with(nativeAuthFlowCoordinator()) {
                emit(UpdateManifest(manifest))
                emit(RequestNextStep(currentStep = NavigationDirections.consent))
            }
        }
    }

    fun onClickableTextClick(tag: String) {
        setState {
            when (ConsentClickableText.values().firstOrNull { it.value == tag }) {
                ConsentClickableText.TERMS ->
                    copy(viewEffect = OpenUrl(stripeToSUrl))
                ConsentClickableText.PRIVACY ->
                    copy(viewEffect = OpenUrl(FinancialConnectionsUrls.StripePrivacyPolicy))
                ConsentClickableText.DISCONNECT ->
                    copy(viewEffect = OpenUrl(disconnectUrl))
                ConsentClickableText.DATA ->
                    copy(viewEffect = ConsentState.ViewEffect.OpenBottomSheet)
                ConsentClickableText.PRIVACY_CENTER ->
                    copy(viewEffect = OpenUrl(privacyCenterUrl))
                ConsentClickableText.DATA_ACCESS ->
                    copy(viewEffect = OpenUrl(dataPolicyUrl))
                null -> {
                    logger.error("Unrecognized clickable text: $tag")
                    this
                }
            }
        }
    }

    fun onManifestChanged(manifest: FinancialConnectionsSessionManifest) {
        setState {
            copy(
                disconnectUrl = ConsentUrlBuilder.getDisconnectUrl(manifest),
                faqUrl = ConsentUrlBuilder.getFAQUrl(manifest),
                dataPolicyUrl = ConsentUrlBuilder.getDataPolicyUrl(manifest),
                stripeToSUrl = ConsentUrlBuilder.getStripeTOSUrl(manifest),
                privacyCenterUrl = ConsentUrlBuilder.getPrivacyCenterUrl(manifest),
                title = ConsentTextBuilder.getConsentTitle(manifest),
                bullets = ConsentTextBuilder.getBullets(manifest),
                requestedDataTitle = ConsentTextBuilder.getDataRequestedTitle(manifest),
                requestedDataBullets = ConsentTextBuilder.getRequestedDataBullets(manifest)
            )
        }
    }

    fun onViewEffectLaunched() {
        setState {
            copy(
                viewEffect = null
            )
        }
    }

    companion object : MavericksViewModelFactory<ConsentViewModel, ConsentState> {

        override fun create(
            viewModelContext: ViewModelContext,
            state: ConsentState
        ): ConsentViewModel {
            return viewModelContext.financialConnectionsSubComponentBuilderProvider
                .consentSubComponentBuilder.get()
                .initialState(state)
                .build()
                .viewModel
        }
    }
}

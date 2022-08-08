package com.stripe.android.ui.core.elements

import android.content.Context
import com.stripe.android.model.Address
import com.stripe.android.ui.core.R
import com.stripe.android.ui.core.elements.autocomplete.PlacesClientProxy
import com.stripe.android.ui.core.elements.autocomplete.model.AutocompletePrediction
import com.stripe.android.ui.core.elements.autocomplete.transformGoogleToStripeAddress
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

internal class AddressAutocompleteTextFieldController(
    private val context: Context,
    val country: String,
    val googlePlacesApiKey: String?,
    val workerScope: CoroutineScope
) : Controller {

    private val client = googlePlacesApiKey?.let {
        PlacesClientProxy.create(context, googlePlacesApiKey)
    }
    private var searchJob: Job? = null

    val predictions = MutableStateFlow<List<AutocompletePrediction>?>(null)
    val loading = MutableStateFlow(false)
    val addressResult = MutableStateFlow<Result<Address?>?>(null)
    val textFieldController = SimpleTextFieldController(
        SimpleTextFieldConfig(
            label = R.string.address_label_address,
            trailingIcon = MutableStateFlow(
                TextFieldIcon.Trailing(
                    idRes = R.drawable.stripe_ic_clear,
                    isTintable = true,
                    onClick = { clearQuery() }
                )
            )
        )
    )

    init {
        startWatching(
            workerScope,
            textFieldController.formFieldValue
                .map { it.takeIf { it.isComplete }?.value }
                .stateIn(workerScope, SharingStarted.WhileSubscribed(), "")
        )
    }

    fun selectPrediction(prediction: AutocompletePrediction) {
        workerScope.launch {
            loading.value = true
            client?.fetchPlace(
                placeId = prediction.placeId
            )?.fold(
                onSuccess = {
                    loading.value = false
                    addressResult.value = Result.success(it.place.transformGoogleToStripeAddress(context))
                },
                onFailure = {
                    loading.value = false
                    addressResult.value = Result.failure(it)
                }
            )
        }
    }

    private fun startWatching(
        coroutineScope: CoroutineScope,
        addressFlow: StateFlow<String?>
    ) {
        coroutineScope.launch {
            addressFlow.collect { query ->
                searchJob?.cancel()
                query?.let {
                    if (query.length > MIN_CHARS_AUTOCOMPLETE) {
                        searchJob = launch {
                            delay(SEARCH_DEBOUNCE_MS)
                            client?.findAutocompletePredictions(
                                query = query,
                                country = country,
                                limit = MAX_DISPLAYED_RESULTS
                            )?.fold(
                                onSuccess = {
                                    loading.value = false
                                    predictions.value = it.autocompletePredictions
                                },
                                onFailure = {
                                    loading.value = false
                                    addressResult.value = Result.failure(it)
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    private fun clearQuery() {
        textFieldController.onRawValueChange("")
    }

    private companion object {
        const val SEARCH_DEBOUNCE_MS = 1000L
        const val MAX_DISPLAYED_RESULTS = 4
        const val MIN_CHARS_AUTOCOMPLETE = 3
        val autocompleteSupportedCountries = setOf(
            "AU",
            "BE",
            "BR",
            "CA",
            "CH",
            "DE",
            "ES",
            "FR",
            "GB",
            "IE",
            "IN",
            "IT",
            "JP",
            "MX",
            "MY",
            "NO",
            "NL",
            "PH",
            "PL",
            "RU",
            "SE",
            "SG",
            "TR",
            "US",
            "ZA"
        )
    }
}

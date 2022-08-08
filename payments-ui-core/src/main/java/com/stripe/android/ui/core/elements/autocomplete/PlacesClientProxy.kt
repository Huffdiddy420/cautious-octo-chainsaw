package com.stripe.android.ui.core.elements.autocomplete

import android.content.Context
import android.graphics.Typeface
import android.text.style.StyleSpan
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.model.TypeFilter
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.stripe.android.BuildConfig
import com.stripe.android.ui.core.R
import com.stripe.android.ui.core.elements.autocomplete.model.AddressComponent
import com.stripe.android.ui.core.elements.autocomplete.model.AutocompletePrediction
import com.stripe.android.ui.core.elements.autocomplete.model.FetchPlaceResponse
import com.stripe.android.ui.core.elements.autocomplete.model.FindAutocompletePredictionsResponse
import com.stripe.android.ui.core.elements.autocomplete.model.Place
import com.stripe.android.ui.core.isSystemDarkTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExecutorCoroutineDispatcher
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.tasks.asDeferred
import kotlinx.coroutines.tasks.await
import java.util.concurrent.Executors
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

internal interface PlacesClientProxy {
    suspend fun findAutocompletePredictions(
        query: String?,
        country: String,
        limit: Int
    ) : Result<FindAutocompletePredictionsResponse>

    suspend fun fetchPlace(
        placeId: String
    ) : Result<FetchPlaceResponse>

    companion object {
        fun create(
            context: Context,
            googlePlacesApiKey: String,
            isPlacesAvailable: IsPlacesAvailable = DefaultIsPlacesAvailable(),
            clientFactory: (Context) -> PlacesClient = { Places.createClient(context) },
            initializer: () -> Unit = { Places.initialize(context, googlePlacesApiKey) }
        ): PlacesClientProxy {
            return if (isPlacesAvailable()) {
                initializer()
                DefaultPlacesClientProxy(
                    clientFactory(context)
                )
            } else {
                UnsupportedPlacesClientProxy()
            }
        }

        fun getPlacesPoweredByGoogleDrawable(context: Context): Int? {
            return if (DefaultIsPlacesAvailable().invoke()) {
                if (context.isSystemDarkTheme()) {
                    R.drawable.places_powered_by_google_dark
                } else {
                    R.drawable.places_powered_by_google_light
                }
            } else {
                if (BuildConfig.DEBUG) {
                    throw IllegalStateException(
                        "Missing Google Places dependency, please add it to your apps build.gradle"
                    )
                }
                null
            }
        }
    }
}

internal class DefaultPlacesClientProxy(
    private val client: PlacesClient
) : PlacesClientProxy {
    private val token = AutocompleteSessionToken.newInstance()

    override suspend fun findAutocompletePredictions(
        query: String?,
        country: String,
        limit: Int
    ) : Result<FindAutocompletePredictionsResponse> {
        val request = com.google.android.libraries.places.api.net
            .FindAutocompletePredictionsRequest
            .builder()
            .setSessionToken(token)
            .setQuery(query)
            .setCountry(country)
            .setTypeFilter(TypeFilter.ADDRESS)
            .build()
        try {
            val response = client.findAutocompletePredictions(request).await()
            return Result.success(
                FindAutocompletePredictionsResponse(
                    autocompletePredictions = response.autocompletePredictions.map {
                        AutocompletePrediction(
                            primaryText = it.getPrimaryText(StyleSpan(Typeface.BOLD)),
                            secondaryText = it.getSecondaryText(StyleSpan(Typeface.BOLD)),
                            placeId = it.placeId
                        )
                    }.take(limit)
                )
            )
        } catch (e: Exception) {
            return Result.failure(
                Exception("Could not find autocomplete predictions: ${e.message}")
            )
        }
    }

    override suspend fun fetchPlace(
        placeId: String
    ) : Result<FetchPlaceResponse> {
        val result = suspendCoroutine<Result<FetchPlaceResponse>> { continuation ->
            client.fetchPlace(
                FetchPlaceRequest.newInstance(
                    placeId,
                    listOf(
                        com.google.android.libraries.places.api.model.Place.Field.ADDRESS_COMPONENTS
                    )
                )
            ).addOnSuccessListener { response ->
                continuation.resume(
                    Result.success(
                        FetchPlaceResponse(
                            Place(
                                response.place.addressComponents?.asList()?.map {
                                    AddressComponent(
                                        shortName = it.shortName,
                                        longName = it.name,
                                        types = it.types
                                    )
                                }
                            )
                        )
                    )
                )
            }.addOnFailureListener {
                continuation.resume(
                    Result.failure(
                        Exception("Could not fetch place: ${it.message}")
                    )
                )
            }
        }

        return result
    }
}

internal class UnsupportedPlacesClientProxy: PlacesClientProxy {
    override suspend fun findAutocompletePredictions(
        query: String?,
        country: String,
        limit: Int
    ): Result<FindAutocompletePredictionsResponse> {
        val exception = IllegalStateException(
            "Missing Google Places dependency, please add it to your apps build.gradle"
        )
        if (BuildConfig.DEBUG) {
            throw exception
        }
        return Result.failure(exception)
    }

    override suspend fun fetchPlace(placeId: String): Result<FetchPlaceResponse> {
        val exception = IllegalStateException(
            "Missing Google Places dependency, please add it to your apps build.gradle"
        )
        if (BuildConfig.DEBUG) {
            throw exception
        }
        return Result.failure(exception)
    }
}

internal interface IsPlacesAvailable {
    operator fun invoke(): Boolean
}

internal class DefaultIsPlacesAvailable : IsPlacesAvailable {
    override fun invoke(): Boolean {
        return try {
            Class.forName("com.google.android.libraries.places.api.Places")
            true
        } catch (_: Exception) {
            false
        }
    }
}

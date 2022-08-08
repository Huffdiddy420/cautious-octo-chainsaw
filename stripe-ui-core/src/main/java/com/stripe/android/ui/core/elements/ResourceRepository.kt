package com.stripe.android.ui.core.elements

import androidx.annotation.RestrictTo
import com.stripe.android.ui.core.address.AddressFieldElementRepository
import javax.inject.Inject
import javax.inject.Singleton

/**
 * This holds all the resources read in from JSON.
 */
@Singleton
@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
class ResourceRepository @Inject internal constructor(
    val bankRepository: BankRepository,
    val addressRepository: AddressFieldElementRepository
)

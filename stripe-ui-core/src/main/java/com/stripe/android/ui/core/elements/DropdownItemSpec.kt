package com.stripe.android.ui.core.elements

import kotlinx.serialization.Serializable

@Serializable
internal data class DropdownItemSpec(
    val value: String?,
    val text: String
)

package com.stripe.android.connections.example.data.model

import com.google.gson.annotations.SerializedName
import com.stripe.android.connections.model.AccountHolder

data class CreateLinkAccountSessionBody(
    @SerializedName("accountholder") val accountHolder: AccountHolder? = null
)
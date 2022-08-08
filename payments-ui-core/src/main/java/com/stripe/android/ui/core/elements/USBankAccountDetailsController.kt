package com.stripe.android.ui.core.elements

import androidx.annotation.RestrictTo
import kotlinx.coroutines.flow.MutableStateFlow

@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
class USBankAccountDetailsController : Controller {
    val text = MutableStateFlow("")
    val visible = MutableStateFlow(false)
    val detailsClicked = MutableStateFlow(false)
}

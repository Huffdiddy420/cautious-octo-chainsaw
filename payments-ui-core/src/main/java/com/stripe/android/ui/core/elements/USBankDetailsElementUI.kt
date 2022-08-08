package com.stripe.android.ui.core.elements

import androidx.annotation.RestrictTo
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp

@Composable
@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
fun USBankAccountDetailsElementUI(
    element: USBankAccountDetailsElement
) {
    val value by element.controller.text.collectAsState("")
    val isVisible by element.controller.visible.collectAsState(false)
    if (isVisible) {
        H6Text(
            text = value,
            modifier = Modifier
                .padding(vertical = 8.dp)
                .clickable {
                    element.controller.detailsClicked.tryEmit(true)
                }
                .semantics(mergeDescendants = true) {}, // makes it a separate accessibile item
        )
    }
}

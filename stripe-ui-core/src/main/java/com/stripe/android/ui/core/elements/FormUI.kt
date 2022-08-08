package com.stripe.android.ui.core.elements

import androidx.annotation.RestrictTo
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import kotlinx.coroutines.flow.Flow

@Composable
@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
fun Form(
    hiddenIdentifiersFlow: Flow<List<IdentifierSpec>>,
    enabledFlow: Flow<Boolean>,
    elements: List<FormElement>
) {
    val hiddenIdentifiers by hiddenIdentifiersFlow.collectAsState(
        null
    )
    val enabled by enabledFlow.collectAsState(true)

    hiddenIdentifiers?.let {
        Column(
            modifier = Modifier.fillMaxWidth(1f)
        ) {
            elements.forEach { element ->
                if (!it.contains(element.identifier)) {
                    when (element) {
                        is SectionElement -> SectionElementUI(enabled, element, it)
                        is StaticTextElement -> StaticElementUI(element)
                        is SaveForFutureUseElement -> SaveForFutureUseElementUI(enabled, element)
                        is AfterpayClearpayHeaderElement -> AfterpayClearpayElementUI(
                            enabled,
                            element
                        )
                    }
                }
            }
        }
    }
}

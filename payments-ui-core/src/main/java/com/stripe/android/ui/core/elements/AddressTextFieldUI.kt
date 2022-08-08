package com.stripe.android.ui.core.elements

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.text.input.ImeAction
import com.stripe.android.ui.core.elements.autocomplete.AddressAutocompleteContract

@Composable
internal fun AddressTextFieldUI(
    controller: AddressTextFieldController,
) {
    val address = controller.address.collectAsState(null)
    val launcher = rememberLauncherForActivityResult(
        contract = AddressAutocompleteContract(),
        onResult = {
            it?.address?.let {
                controller.address.value = it
            }
        }
    )

    TextField(
        textFieldController = controller.apply {
            address.value?.line1?.let {
                onRawValueChange(it)
            }
        },
        imeAction = ImeAction.Next,
        enabled = true,
        interactionSource = remember { MutableInteractionSource() }
            .also { interactionSource ->
                if (controller.shouldUseAutocomplete) {
                    LaunchedEffect(interactionSource) {
                        interactionSource.interactions.collect {
                            if (it is PressInteraction.Release) {
                                launcher.launch(
                                    AddressAutocompleteContract.Args(
                                        country = controller.country,
                                        googlePlacesApiKey = controller.googlePlacesApiKey,
                                    )
                                )
                            }
                        }
                    }
                }
            }
    )
}

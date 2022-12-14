package com.stripe.android.ui.core.elements

import androidx.annotation.RestrictTo
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.selection.toggleable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.unit.dp
import com.stripe.android.ui.core.R
import com.stripe.android.ui.core.elements.menu.Checkbox

const val SAVE_FOR_FUTURE_CHECKBOX_TEST_TAG = "SAVE_FOR_FUTURE_CHECKBOX_TEST_TAG"

@Composable
@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
fun SaveForFutureUseElementUI(
    enabled: Boolean,
    element: SaveForFutureUseElement
) {
    val controller = element.controller
    val checked by controller.saveForFutureUse.collectAsState(true)
    val label by controller.label.collectAsState(null)
    val resources = LocalContext.current.resources

    val description = stringResource(
        if (checked) {
            R.string.selected
        } else {
            R.string.not_selected
        }
    )

    Row(
        modifier = Modifier
            .padding(vertical = 2.dp)
            .semantics {
                testTag = SAVE_FOR_FUTURE_CHECKBOX_TEST_TAG
                stateDescription = description
            }
            .toggleable(
                value = checked,
                role = Role.Checkbox,
                onValueChange = {
                    controller.onValueChange(!checked)
                },
                enabled = enabled
            )
            .fillMaxWidth()
            .requiredHeight(48.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = null, // needs to be null for accessibility on row click to work
            enabled = enabled
        )
        label?.let {
            H6Text(
                text = resources.getString(it, element.merchantName),
                modifier = Modifier
                    .padding(start = 4.dp)
                    .align(Alignment.CenterVertically)
            )
        }
    }
}

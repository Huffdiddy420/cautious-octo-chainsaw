package com.stripe.android.ui.core.elements

import androidx.annotation.RestrictTo
import androidx.annotation.StringRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.AppBarDefaults
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.Checkbox
import androidx.compose.material.Colors
import androidx.compose.material.ContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.stripe.android.ui.core.R

/**
 * This is the style for the section card
 */
internal data class CardStyle(
    private val isDarkTheme: Boolean,
    val cardBorderColor: Color = if (isDarkTheme) {
        Color(0xFF787880)
    } else {
        Color(0x14000000)
    },
    val cardBorderWidth: Dp = 1.dp,
    val cardElevation: Dp = 0.dp,
    val cardStyleBackground: Color = Color(0x20FFFFFF)
)

/**
 * This is the style for the section title.
 *
 * Once credit card is converted use one of the default material theme styles.
 */
internal data class SectionTitle constructor(
    val light: Color = Color.DarkGray,
    val dark: Color = Color.White,
    val fontWeight: FontWeight = FontWeight.Bold,
    val paddingBottom: Dp = 4.dp,
    val letterSpacing: TextUnit = (-0.01f).sp,
    val fontSize: TextUnit = 13.sp
)

/**
 * This is a simple section that holds content in a card view.  It has a label, content specified
 * by the caller, and an error string.
 */
@Composable
internal fun Section(
    @StringRes title: Int?,
    error: String?,
    content: @Composable () -> Unit
) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        SectionTitle(title)
        SectionCard(content)
        if (error != null) {
            SectionError(error)
        }
    }
}

/**
 * This is the layout for the section title
 */
@Composable
internal fun SectionTitle(@StringRes titleText: Int?) {
    val sectionTitle = SectionTitle()
    titleText?.let {
        Text(
            text = stringResource(titleText),
            color = if (isSystemInDarkTheme()) {
                sectionTitle.dark
            } else {
                sectionTitle.light
            },
            style = MaterialTheme.typography.h6.copy(
                fontSize = sectionTitle.fontSize,
                fontWeight = sectionTitle.fontWeight,
                letterSpacing = sectionTitle.letterSpacing,
            ),
            modifier = Modifier
                .padding(vertical = 4.dp)
                .semantics(mergeDescendants = true) { // Need to prevent form as focusable accessibility
                    heading()
                }
        )
    }
}

/**
 * This is the layout for the section card.
 */
@Composable
@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
fun SectionCard(
    content: @Composable () -> Unit
) {
    val cardStyle = CardStyle(isSystemInDarkTheme())
    Card(
        border = BorderStroke(cardStyle.cardBorderWidth, cardStyle.cardBorderColor),
        elevation = cardStyle.cardElevation,
        backgroundColor = cardStyle.cardStyleBackground
    ) {
        Column {
            content()
        }
    }
}


val Green400 = Color(0xFF3CB043)
val Green800 = Color(0xFF234F1E)
val Yellow400 = Color(0xFFF6E547)
val Yellow700 = Color(0xFFF3B711)
val Yellow800 = Color(0xFFF29F05)

val Blue300 = Color(0xFF17AFFF)
val Blue500 = Color(0xFF0540F2)
val BlueDark = Color(0xFF00229F)

val Red300 = Color(0xFFEA6D7E)
val Red800 = Color(0xFFD00036)

val Teal = Color(0xFF0097a7)
val TealLight = Color(0xFF56c8d8)

val Purple = Color(0xFF4a148c)
val PurpleLight = Color(0xFF7c43bd)
val PurpleLightest = Color(0xFF9DA3FA)

val GrayLight = Color(0xFFF8F8F8)

@Composable
fun PreviewTheme(
    content: @Composable () -> Unit
) {

    MaterialTheme(
        Colors(
            primary = BlueDark,
            primaryVariant = Blue500,
            secondary = Purple,
            secondaryVariant = PurpleLight,
            background = Green800,
            surface = Color.White,
            error = Red800,
            onPrimary = Color.White,
            onSecondary = PurpleLightest,
            onBackground = Yellow400,
            onSurface = Green800,
            onError = Color.Magenta,
            isLight = true
        )
    ) {
        Surface{
            // Surface color is used on Sheets (which is what the paymentsheet is)
            Column {
                Column(modifier = Modifier.padding(5.dp)) {
                    content()
                }
            }
        }
    }
}

@Preview
@Composable
fun FilledText() {
    PreviewTheme {
        AppBarDefaults
        Text(
            text = "Filled Text:\n" +
                "- Filled color = onSurface? + alpha\n" +
                "- placeholder text color = onSurface\n" +
                "- label in place of placeholder until entering text\n" +
                "- label shrinks to the top of the filled box when entering text\n" +
                "- placeholder text appears when entering text\n" +
                "- value text color = black - not in material theme\n" +
                "- value text color on error = black - not in material theme\n" +
                "- error line under = error",
        )
        Text(text = "Card Details", modifier = Modifier.padding(top = 10.dp))
        CardPreviewFilled()
        Text(
            text = "CVC is invalid (No standard on where errors are shown)",
            color = MaterialTheme.colors.error
        )
    }
}

@Preview
@Composable
fun OutlinePreview() {
    PreviewTheme {
        Text(
            text = "OutlinedText:\n" +
                "- Default outline color = onSurface\n" +
                "- placeholder text color = onSurface\n" +
                "- label in place of placeholder until entering text\n" +
                "- label is part of the outline box when entering text\n" +
                "- placeholder text appears when field clicked on\n" +
                "- text field background = none - not in material theme\n" +
                "- value text color = black - not in material theme\n" +
                "- value text color on error = black - not in material theme\n" +
                "- error line around = error",
        )
        Text(text = "Card Details", modifier = Modifier.padding(top = 10.dp))
        CardPreviewOutlined()
        Text(
            text = "CVC is invalid (No standard on where errors are shown)",
            color = MaterialTheme.colors.error
        )
    }
}

@Preview
@Composable
fun CheckboxPreview() {
    PreviewTheme {
        Text(text = "Checkbox background = Secondary color\ncheck mark = background\n corner radius cannot be set it is 2.dp")
        Row {
            // corner radius always 2.dp not changeable
            Checkbox(
                checked = true,
                onCheckedChange = { },
            )
            Text(text = "Save future...")

            // corner radius always 2.dp not changeable
            Checkbox(
                checked = false,
                onCheckedChange = { },
            )
            Text(text = "Save future...")
        }
    }
}

@Preview
@Composable
fun CardPreviewWithButton() {

    PreviewTheme {

        Text(
            text = "Default card border stroke is null.\n card = surface\nselected=undefined",
        )
        Row {

            Button(
                enabled = true,
                onClick = { /*TODO*/ },
                modifier = Modifier.width(100.dp)
            ) {
                Column {
                    Image(
                        painter = painterResource(id = R.drawable.stripe_ic_amex),
                        contentDescription = null,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }

            // Disabled - Is not handled by default - I wonder if a Button would be better here?

            Button(
                enabled = false,
                onClick = { /*TODO*/ },
                modifier = Modifier
                    .padding(start = 4.dp)
                    .width(100.dp)
            ) {
                Column {
                    Image(
                        painter = painterResource(id = R.drawable.stripe_ic_amex),
                        contentDescription = null,
                        modifier = Modifier.padding(16.dp)
                    )
                    Text("disabled")
                }
            }

            // Selected
            Button(
                enabled = false,
                onClick = { /*TODO*/ },
                modifier = Modifier
                    .padding(start = 4.dp)
                    .width(100.dp)
            ) {
                Column {
                    Image(
                        painter = painterResource(id = R.drawable.stripe_ic_amex),
                        contentDescription = null,
                        modifier = Modifier.padding(16.dp)
                    )
                    Text("selected")
                }
            }

            // Not selected
            Button(
                enabled = false,
                onClick = { /*TODO*/ },
                modifier = Modifier
                    .padding(start = 4.dp)
                    .width(100.dp)
            ) {
                Column {
                    Image(
                        painter = painterResource(id = R.drawable.stripe_ic_amex),
                        contentDescription = null,
                        modifier = Modifier.padding(16.dp)
                    )
                    Text("de-selected")
                }
            }

        }
    }
}

@Preview
@Composable
fun CardPreview() {
    val cardStyle = CardStyle(isSystemInDarkTheme())
    PreviewTheme {

        Text(
            text = "Default card border stroke is null.\n card = surface\nselected=undefined",
        )
        Row {

            Card(
                backgroundColor =
                TealLight.copy(alpha = ContentAlpha.medium),
                modifier = Modifier.width(100.dp)
            ) {
                Column {
                    Image(
                        painter = painterResource(id = R.drawable.stripe_ic_amex),
                        contentDescription = null,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }

            // Disabled - Is not handled by default - I wonder if a Button would be better here?
            Card(
                backgroundColor = GrayLight,
                modifier = Modifier
                    .padding(start = 4.dp)
                    .width(100.dp)
            ) {
                Column {
                    Image(
                        painter = painterResource(id = R.drawable.stripe_ic_amex),
                        contentDescription = null,
                        modifier = Modifier.padding(16.dp)
                    )
                    Text("selected")
                }
            }

            // Selected
            Card(
                backgroundColor = Teal,
                modifier = Modifier
                    .padding(start = 4.dp)
                    .width(100.dp)
            ) {
                Column {
                    Image(
                        painter = painterResource(id = R.drawable.stripe_ic_amex),
                        contentDescription = null,
                        modifier = Modifier.padding(16.dp)
                    )
                    Text("selected")
                }
            }

            // Not selected
            Card(
                backgroundColor = TealLight,
                modifier = Modifier
                    .padding(start = 4.dp)
                    .width(100.dp)
            ) {
                Column {
                    Image(
                        painter = painterResource(id = R.drawable.stripe_ic_amex),
                        contentDescription = null,
                        modifier = Modifier.padding(16.dp)
                    )
                    Text("de-selected")
                }
            }

        }
    }
}

@Preview
@Composable
fun ButtonPreview() {
    PreviewTheme {
        Text(
            text = "Button:\nButton text - onSurface\ndefault buttoncolor = primary",
        )
        Button(onClick = { /*TODO*/ }) {
            Text(
                text = "Buy",
            )
        }
    }
}

@Composable
fun CardPreviewFilled() {
    Column(modifier = Modifier.padding(10.dp)) {
        // This the filled text field which will always have a background
        androidx.compose.material.TextField(
            value = "",
            label = { Text("Number") },
            onValueChange = {},
            placeholder = {
                Text(text = "placeholder")
            },
            modifier = Modifier
                .padding(top = 10.dp)
                .fillMaxWidth()
        )
        Row {
            androidx.compose.material.TextField(
                value = "",
                label = { Text("MM/YY") },
                onValueChange = {},
                placeholder = {
                    Text(text = "MM/YY")
                },
                modifier = Modifier.padding(top = 10.dp)
            )
            androidx.compose.material.TextField(
                value = "ABC",
                label = { Text("CVC") },
                onValueChange = {},
                placeholder = {
                    Text(text = "CVC")
                },
                isError = true,
                modifier = Modifier.padding(top = 10.dp, start = 10.dp)
            )
        }
    }
}

@Composable
fun CardPreviewOutlined() {
    Column(modifier = Modifier.padding(10.dp)) {
        // This the filled text field which will always have a background
        androidx.compose.material.OutlinedTextField(
            value = "",
            onValueChange = {},
            label = { Text("Number") },
            placeholder = {
                Text(text = "placeholder")
            },
            modifier = Modifier
                .padding(top = 10.dp)
                .fillMaxWidth()
        )
        Row {
            androidx.compose.material.OutlinedTextField(
                value = "",
                onValueChange = {},
                label = { Text("MM/YY") },
                placeholder = {
                    Text(text = "MM/YY")
                },
                modifier = Modifier.padding(top = 10.dp)
            )
            androidx.compose.material.OutlinedTextField(
                value = "ABC",
                label = { Text("CVC") },
                onValueChange = {},
                placeholder = {
                    Text(text = "123")
                },
                isError = true,
                modifier = Modifier.padding(top = 10.dp, start = 10.dp)
            )
        }
    }
}


/**
 * This is how error string for the section are displayed.
 */
@Composable
internal fun SectionError(error: String) {
    Text(
        text = error,
        color = MaterialTheme.colors.error,
        modifier = Modifier.semantics(mergeDescendants = true) { }
    )
}

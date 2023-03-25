package dev.priyankvasa.sample.android.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import dev.priyankvasa.sample.android.ui.R

// Set of Material typography styles to start with
internal val Typography
    @Composable
    get() = Typography(/*TODO Update font family; required weights: 400, 500, 700*/)

private val FontFamilyMuli
    get() = FontFamily(
        Font(
            R.font.muli_extralight,
            weight = FontWeight.ExtraLight,
            style = FontStyle.Normal,
        ),
        Font(
            R.font.muli_extralight_italic,
            weight = FontWeight.ExtraLight,
            style = FontStyle.Italic,
        ),
        Font(
            R.font.muli_light,
            weight = FontWeight.Light,
            style = FontStyle.Normal,
        ),
        Font(
            R.font.muli_light_italic,
            weight = FontWeight.Light,
            style = FontStyle.Italic,
        ),
        Font(
            R.font.muli,
            weight = FontWeight.Normal,
            style = FontStyle.Normal,
        ),
        Font(
            R.font.muli_italic,
            weight = FontWeight.Normal,
            style = FontStyle.Italic,
        ),
        Font(
            R.font.muli_semibold,
            weight = FontWeight.SemiBold,
            style = FontStyle.Normal,
        ),
        Font(
            R.font.muli_semibold_italic,
            weight = FontWeight.SemiBold,
            style = FontStyle.Italic,
        ),
        Font(
            R.font.muli_bold,
            weight = FontWeight.Bold,
            style = FontStyle.Normal,
        ),
        Font(
            R.font.muli_bold_italic,
            weight = FontWeight.Bold,
            style = FontStyle.Italic,
        ),
        Font(
            R.font.muli_extrabold,
            weight = FontWeight.ExtraBold,
            style = FontStyle.Normal,
        ),
        Font(
            R.font.muli_extrabold_italic,
            weight = FontWeight.ExtraBold,
            style = FontStyle.Italic,
        ),
        Font(
            R.font.muli_black,
            weight = FontWeight.Black,
            style = FontStyle.Normal,
        ),
        Font(
            R.font.muli_black_italic,
            weight = FontWeight.Black,
            style = FontStyle.Italic,
        ),
    )

package dev.priyankvasa.sample.android.ui.authentication.password

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign

@Composable
fun AuthenticationFormHintText(
    modifier: Modifier = Modifier,
    text: String,
) {
    Text(
        text = text,
        modifier = modifier
            .wrapContentHeight()
            .fillMaxWidth(),
        textAlign = TextAlign.Start,
        style = MaterialTheme.typography.titleSmall,
    )
}

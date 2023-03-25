package dev.priyankvasa.sample.android.ui.composeUi

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import dev.priyankvasa.sample.android.ui.R

@Composable
fun LoadingOverlay(
    modifier: Modifier = Modifier,
    loadingText: String = stringResource(id = R.string.loading),
) {
    Column(
        modifier
            .fillMaxSize()
            .zIndex(Float.MAX_VALUE)
            .alpha(0.7f)
            .background(MaterialTheme.colorScheme.background),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        CircularProgressIndicator()

        Spacer(modifier = Modifier.size(8.dp))

        Text(
            text = loadingText,
            style = MaterialTheme.typography.titleSmall,
        )
    }
}

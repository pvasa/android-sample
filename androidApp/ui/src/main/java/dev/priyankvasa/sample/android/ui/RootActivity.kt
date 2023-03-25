package dev.priyankvasa.sample.android.ui

import android.os.Bundle
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.CompositionLocalProvider
import dagger.hilt.android.AndroidEntryPoint
import dev.priyankvasa.sample.android.ui.theme.AppTheme

@AndroidEntryPoint
class RootActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CompositionLocalProvider(
                LocalBackDispatcher provides LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher,
            ) {
                AppTheme {
                    RootScreen()
                }
            }
        }
    }
}

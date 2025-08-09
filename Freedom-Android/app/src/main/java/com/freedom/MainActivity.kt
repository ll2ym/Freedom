package com.freedom

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.view.WindowCompat
import com.freedom.storage.SecurePrefs
import com.freedom.ui.NavGraph
import com.freedom.ui.theme.FreedomTheme
import com.freedom.utils.ScreenSecurity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var securePrefs: SecurePrefs

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Enable secure flag to prevent screen recording/screenshot
        ScreenSecurity.enable(this)

        // Edge-to-edge layout
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            FreedomTheme {
                NavGraph()
            }
        }
    }
}

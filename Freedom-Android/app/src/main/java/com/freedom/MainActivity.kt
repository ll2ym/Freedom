package com.freedom

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.view.WindowCompat
import com.freedom.ui.theme.FreedomTheme
import com.freedom.utils.ScreenSecurity
import com.freedom.ui.NavGraph

class MainActivity : ComponentActivity() {
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

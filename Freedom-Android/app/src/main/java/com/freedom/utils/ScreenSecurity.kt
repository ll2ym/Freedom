package com.freedom.utils

import android.app.Activity
import android.view.WindowManager

object ScreenSecurity {

    /**
     * Enable secure flag to block screenshots and screen recordings
     */
    fun enable(activity: Activity) {
        activity.window.setFlags(
            WindowManager.LayoutParams.FLAG_SECURE,
            WindowManager.LayoutParams.FLAG_SECURE
        )
    }

    /**
     * Disable secure flag if needed (e.g., for public images/screens)
     */
    fun disable(activity: Activity) {
        activity.window.clearFlags(WindowManager.LayoutParams.FLAG_SECURE)
    }
}

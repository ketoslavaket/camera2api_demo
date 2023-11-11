package com.ketoslavaket.camera2api_demo.ui


import com.ketoslavaket.camera2api_demo.presentation.ModelData
import com.ketoslavaket.camera2api_demo.presentation.UiEventHandler
import com.ketoslavaket.camera2api_demo.ui.theme.Camera2api_demoTheme

import android.app.Activity
import android.content.Context
import android.content.pm.ActivityInfo
import android.os.Build
import android.util.DisplayMetrics
import android.view.WindowManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect


class UserInterface (private val mainActivity: Activity, private val modelData: ModelData,
                     private val uiEventHandler: UiEventHandler) {


    // Settings


    companion object {
        const val LOG_TAG = "myLogs"
    }


    // Variables


    private var uiScale = 0.0F
    private var screenResolution = Pair(100, 100)
    private var screenResolutionInDp = Pair(100.0F, 100.0F)


    // Public


    init {
        // Calculate ui scale options
        screenResolution = getScreenResolution(mainActivity)
        screenResolutionInDp = getScreenResolutionInDp(mainActivity)
        uiScale = screenResolutionInDp.first / 100.0F
    }


    @Composable
    fun SetupUserInterface() {
        LockScreenOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)

        // Create user interface root
        val root = Root(mainActivity, modelData, uiEventHandler, uiScale, screenResolution,
            screenResolutionInDp)

        Camera2api_demoTheme {
            root.UserInterfaceRoot()
        }
    }


    // Private


    @Composable
    fun LockScreenOrientation(orientation: Int) {
        DisposableEffect(Unit) {
            val activity = mainActivity
            val originalOrientation = activity.requestedOrientation
            activity.requestedOrientation = orientation
            onDispose {
                // restore original orientation when view disappears
                activity.requestedOrientation = originalOrientation
            }
        }
    }


    private fun getScreenResolutionInDp(context: Context): Pair<Float, Float> {
        val density = mainActivity.resources.displayMetrics.density
        val resolution = getScreenResolution(context)
        // Return result
        return Pair(resolution.first / density, resolution.second / density)
    }
    private fun getScreenResolution(context: Context): Pair<Int, Int> {

        // Get components
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val displayMetrics = DisplayMetrics()

        // Get display
        val display = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            context.display
        } else {
            @Suppress("DEPRECATION")
            windowManager.defaultDisplay
        }

        // Get resolution
        @Suppress("DEPRECATION")
        display?.getRealMetrics(displayMetrics)

        // Return result
        return Pair(displayMetrics.widthPixels, displayMetrics.heightPixels)
    }
}
package com.ketoslavaket.camera2api_demo.ui

import com.ketoslavaket.camera2api_demo.presentation.ModelData
import com.ketoslavaket.camera2api_demo.presentation.UiEventHandler

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color


class Root (
    private val mainActivity: Activity,
    private val modelData: ModelData,
    private val uiEventHandler: UiEventHandler,
    private val uiScale: Float,
    private val screenResolution: Pair<Int, Int>,
    private val screenResolutionInDp: Pair<Float, Float>,
){


    // Variables


    // ui components
    private val generalComponents = GeneralComponents(mainActivity, modelData, uiEventHandler,
        uiScale, screenResolution, screenResolutionInDp)
    private val dialogBoxes = DialogBoxes(mainActivity, modelData, uiEventHandler, uiScale,
        generalComponents)
    private val cameraPreview = CameraPreview(mainActivity, modelData, uiEventHandler, uiScale,
        screenResolution, generalComponents)
    private val cameraControls = CameraControls(mainActivity, modelData, uiEventHandler, uiScale,
        screenResolution, generalComponents)


    // Public


    @Composable
    fun UserInterfaceRoot() {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Color.Red
        )
        {
            PageSelector()
        }
    }


    // Private


    @Composable
    fun PageSelector() {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
        ) {
            // Get active pages
            val activePages = modelData.activePages.collectAsState().value

            // Page controls

            if (activePages.contains("CameraPreview")) {
                cameraPreview.CameraPreviewPage()
            }
            if (activePages.contains("CameraControl")) {
                cameraControls.CameraControlPage()
            }
            if (activePages.contains("Selector")) {
                dialogBoxes.SelectorPage()
            }
        }
    }
}
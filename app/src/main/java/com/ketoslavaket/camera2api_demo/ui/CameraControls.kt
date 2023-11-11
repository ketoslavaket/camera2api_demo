package com.ketoslavaket.camera2api_demo.ui


import com.ketoslavaket.camera2api_demo.R
import com.ketoslavaket.camera2api_demo.presentation.ModelData
import com.ketoslavaket.camera2api_demo.presentation.UiEventHandler

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.Matrix
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.ketoslavaket.camera2api_demo.domain.StandardImage


class CameraControls (
    private var mainActivity: Activity,
    private var modelData: ModelData,
    private var uiEventHandler: UiEventHandler,
    private val uiScale: Float,
    private val screenResolution: Pair<Int, Int>,
    private var generalComponents: GeneralComponents
) {

    @Composable
    fun CameraControlPage() {
        Column (
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally,
        ){
            CameraPageControlsBottom()
        }
    }


    @Composable
    fun CameraPageControlsBottom() {
        Column (
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Slider1 (0.2F) {
                    newValue -> uiEventHandler.setNewIsoSliderValue(newValue) }
            Slider1 (0.2F) {
                    newValue -> uiEventHandler.setNewShutterSpeedSliderValue(newValue) }
            BottomControlButtons()
        }
    }


    @Composable
    fun Slider1(onStartValue: Float, onChangeSliderValue: (newValue: Float) -> Unit) {

        // Settings
        val workZoneStart = 10
        val workZoneEnd = 90

        // Calculate slider value
        var sliderValue by remember { mutableStateOf(onStartValue) }

        // Callback
        onChangeSliderValue(sliderValue)

        Box(
            modifier = androidx.compose.ui.Modifier
                .height(uiScale.dp * 12)
                .fillMaxWidth()
                .background(Color(0x991A1A1A))
                .pointerInput(Unit) {
                    detectDragGestures { change, _ ->
                        val touchPosition = Pair(change.position.x, change.position.y)
                        val touchPositionX = touchPosition.first
                        val screenResolutionX = screenResolution.first / 100.0F
                        sliderValue = mapFloatToNormalizedRange(touchPositionX,
                            workZoneStart * (screenResolutionX),
                            workZoneEnd * (screenResolutionX))
                    }
                }
        ) {
            Column (
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxSize()
            ) {
                Row (
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    Box(
                        modifier = androidx.compose.ui.Modifier
                            .height(uiScale.dp * 4)
                            .width(uiScale.dp * (100 - (workZoneStart + (100 - workZoneEnd))))
                            .background(Color(0x80FFFFFF))
                    ){
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .width(uiScale.dp * sliderValue *
                                        (100 - (workZoneStart + (100 - workZoneEnd))))
                                .background(Color.White)
                        )
                    }
                }
            }
        }
    }
    private fun mapFloatToNormalizedRange(value: Float, startRange: Float, endRange: Float): Float {
        var newValue = value
        if (newValue < startRange) {
            newValue = startRange
        }
        if (newValue > endRange) {
            newValue = endRange
        }

        val range = endRange - startRange
        return (newValue - startRange) / range
    }


    @Composable
    fun BottomControlButtons() {
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .height(uiScale.dp * 18)
                .background(Color(0x991A1A1A))
        ) {
            generalComponents.ImageButtonMain(
                R.drawable.icon_switch,
                { uiEventHandler.switchCameraButtonClicked() },
                modifier = androidx.compose.ui.Modifier
                    .width(uiScale.dp * 9)
                    .height(uiScale.dp * 9))
            generalComponents.ImageButtonMain(
                R.drawable.icon_shoot,
                { uiEventHandler.captureButtonClicked() },
                modifier = androidx.compose.ui.Modifier
                    .width(uiScale.dp * 9)
                    .height(uiScale.dp * 9))
            generalComponents.ImageButtonMain(
                R.drawable.icon_empty,
                {},
                modifier = androidx.compose.ui.Modifier
                    .width(uiScale.dp * 9)
                    .height(uiScale.dp * 9))
        }
    }
}
package com.ketoslavaket.camera2api_demo.presentation


import android.view.SurfaceView
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow


class UiEventHandler (private val modelData: ModelData) {


    // Settings


    companion object {
        const val LOG_TAG = "myLogs"
    }


    // Public


    // Sliders: Iso, Shutter speed
    private val _isoSliderValue = MutableStateFlow<Float>(0.0F)
    val isoSliderValue: StateFlow<Float> = _isoSliderValue
    private val _shutterSpeedSliderValue = MutableStateFlow<Float>(0.0F)
    val shutterSpeedSliderValue: StateFlow<Float> = _shutterSpeedSliderValue
    fun setNewIsoSliderValue(newValue: Float) {
        _isoSliderValue.value = newValue
    }
    fun setNewShutterSpeedSliderValue(newValue: Float) {
        _shutterSpeedSliderValue.value = newValue
    }


    // Switch camera button
    private var switchCameraButtonCallback: () -> Unit = {}
    fun assignSwitchCameraButtonCallback(callback: () -> Unit) {
        switchCameraButtonCallback = callback }
    fun switchCameraButtonClicked() { switchCameraButtonCallback() }


    // Capture button
    private var captureButtonCallback: () -> Unit = {}
    fun assignCaptureButtonCallback(callback: () -> Unit) {captureButtonCallback = callback}
    fun captureButtonClicked() {captureButtonCallback()}
}
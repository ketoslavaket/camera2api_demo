package com.ketoslavaket.camera2api_demo.presentation


import com.ketoslavaket.camera2api_demo.domain.StandardImage

import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow


class ModelData {


    // Settings


    companion object {
        const val LOG_TAG = "myLogs"
    }


    // Public


    // Page controls
    fun openCameraPreview() { openPage("CameraPreview") }
    fun closeCameraPreview() { closePage("CameraPreview") }
    fun openCameraControl() {
        openPage("CameraControl")
    }
    fun openSelector(
        newLabel: String,
        optionsArray: List<String>,
        defaultOptionIndex: Int,
        whenOptionSelected: (optionIndex: Int) -> Unit
    ) {
        openPage("Selector")
        updateSelector(newLabel, optionsArray, defaultOptionIndex, whenOptionSelected)
    }
    fun selectorOptionSelected(optionIndex: Int) {
        _whenOptionSelected(optionIndex)
        closePage("Selector")
    }


    // Private


    // Page controls
    private val _activePages = MutableStateFlow<List<String>>(emptyList())
    val activePages: StateFlow<List<String>> = _activePages
    private fun openPage(name: String) {
        if (!_activePages.value.contains(name)) {
            _activePages.value = _activePages.value + name
        }
        Log.i(LOG_TAG, "Open page: $name, arr: ${_activePages.value}")
    }
    private fun closePage(name: String) {
        if (_activePages.value.contains(name)) {
            _activePages.value = _activePages.value - name
        }
        Log.i(LOG_TAG, "Close page: $name, arr: ${_activePages.value}")
    }


    // Selector
    private val _selectorLabel = MutableStateFlow<String>("Selector")
    val selectorLabel: StateFlow<String> = _selectorLabel
    private val _selectorOptions = MutableStateFlow<List<String>>(emptyList())
    val selectorOptions: StateFlow<List<String>> = _selectorOptions
    private val _selectorDefaultOption = MutableStateFlow<Int>(0)
    val selectorDefaultOption: StateFlow<Int> = _selectorDefaultOption
    private var _whenOptionSelected: (optionIndex: Int) -> Unit = {}
    private fun updateSelector(newLabel: String, newOptions: List<String>, newDefaultOption: Int,
                               newWhenOptionSelected: (optionIndex: Int) -> Unit) {
        _selectorLabel.value = newLabel
        _selectorOptions.value = newOptions
        _selectorDefaultOption.value = newDefaultOption
        _whenOptionSelected = newWhenOptionSelected
    }


    // Selector
    private val _cameraPreviewImage = MutableStateFlow<StandardImage?>(null)
    val cameraPreviewImage: StateFlow<StandardImage?> = _cameraPreviewImage
    fun setCameraPreviewImage(newValue: StandardImage?) {
        _cameraPreviewImage.value = newValue
    }

}
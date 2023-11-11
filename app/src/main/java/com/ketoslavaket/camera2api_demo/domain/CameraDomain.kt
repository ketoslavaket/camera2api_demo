package com.ketoslavaket.camera2api_demo.domain


import com.ketoslavaket.camera2api_demo.data.CameraController
import com.ketoslavaket.camera2api_demo.presentation.ModelData
import com.ketoslavaket.camera2api_demo.presentation.UiEventHandler

import android.app.Activity
import android.graphics.BitmapFactory
import android.media.Image
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.util.Size
import com.ketoslavaket.camera2api_demo.data.CameraConfiguration
import com.ketoslavaket.camera2api_demo.data.CaptureOptions
import com.ketoslavaket.camera2api_demo.data.DataFilesController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.nio.ByteBuffer
import kotlin.jvm.internal.Ref.BooleanRef


class CameraDomain (
    private var mainActivity: Activity,
    private var modelData: ModelData,
    private var uiEventHandler: UiEventHandler,
    private val cameraController: CameraController,
    private var currentCameraConfiguration: CameraConfiguration
) {


    // Settings


    companion object {
        private const val LOG_TAG = "CameraService"
    }


    // Variables


    // Components
    private val dataFilesController = DataFilesController(mainActivity)
    // Camera configurations
    private var cameraConfigurations: List<CameraConfiguration> = arrayListOf()
    private var currentCameraConfigurationId = 0
    // Capture session
    private var isCaptureSessionAvailable = false
    private var isCaptureSessionOnPause = false
    private var currentCaptureOptions = CaptureOptions()
    private var updateCaptureSessionLoopJob: Job? = null
    private var isReadyToPhotoCapture = true
    private var isWaitForPhotoCapture = false
    // Capture settings
    private var previewImageReductionFactor = 8


    // Init


    init {
        assignButtonsCallbacks()
    }
    

    // Public


    fun startCameraDomain(newCameraConfigurations: List<CameraConfiguration>) {
        // Assign configurations
        cameraConfigurations = newCameraConfigurations
        // Start capture session with camera by index currentCameraConfigurationId = 0
        clearStartCaptureSession(cameraConfigurations[currentCameraConfigurationId])
    }
    fun stopCameraDomain() {
        // Stop capture session
        stopCaptureSession()
    }


    // Provide state callbacks
    fun onNewActivityState(newState: String) {
        if (newState == "onPause") {
            if (isCaptureSessionAvailable) {
                stopCaptureSession()
                isCaptureSessionOnPause = true
            }
        }
        if (newState == "OnResume") {
            if (isCaptureSessionOnPause) {
                restartCaptureSession(true)
                isCaptureSessionOnPause = false
            }
        }
    }


    // Private


    // Capture session controls

    // Start
    private fun clearStartCaptureSession(newCameraConfiguration: CameraConfiguration) {
        // Assign new configuration
        currentCameraConfiguration = newCameraConfiguration
        // Reset
        currentCaptureOptions = CaptureOptions()
        // Resume
        restartCaptureSession(true)
    }
    private fun restartCaptureSession(isForPreview: Boolean) {

        // Stop capture if available
        if (isCaptureSessionAvailable) {
            stopCaptureSession()
        }

        // Update capture
        setCaptureResolution(isForPreview)
        updateCaptureOptions()

        // Start capture
        cameraController.startCaptureSession(currentCameraConfiguration.getCameraServiceId(),
            isForPreview, currentCaptureOptions) { image ->  onNewFrameCaptured(image) }

        // Start capture session update loop
        if (updateCaptureSessionLoopJob?.isActive == false ||
            updateCaptureSessionLoopJob == null) {
            updateCaptureSessionLoopJob = CoroutineScope(Dispatchers.Default).launch {
                updateCaptureSessionLoop()
            }
        }

        // Mark as available
        isCaptureSessionAvailable = true
    }

    // Update
    private suspend fun updateCaptureSessionLoop() {
        while (updateCaptureSessionLoopJob?.isActive == true) {
            // Update capture options
            updateCaptureOptions()
            // Update capture session
            cameraController.updateCaptureSession(currentCaptureOptions)
            // Delay
            delay(25)
        }
    }

    // Stop
    private fun stopCaptureSession() {
        updateCaptureSessionLoopJob?.cancel()
        cameraController.stopCaptureSession()
        isCaptureSessionAvailable = false
        modelData.setCameraPreviewImage(null)
    }


    // Capture options


    private fun updateCaptureOptions() {

        // Iso
        val isoSliderValue = uiEventHandler.isoSliderValue.value
        val maxIso = currentCameraConfiguration.getAvailableIsoRange().upper
        currentCaptureOptions.setIso((maxIso * (isoSliderValue * isoSliderValue)).toInt())

        // Shutter speed
        val shutterSpeedSliderValue = uiEventHandler.shutterSpeedSliderValue.value
        val shVal = shutterSpeedSliderValue * shutterSpeedSliderValue * shutterSpeedSliderValue
        var maxShutterSpeed = currentCameraConfiguration.getAvailableShutterSpeedRange().upper
        if (maxShutterSpeed > 1000000000) { maxShutterSpeed = 1000000000 } // 1 second limit
        currentCaptureOptions.setShutterSpeed((maxShutterSpeed * shVal).toLong())
    }


    private fun setCaptureResolution(isPreviewResolution: Boolean) {

        // Get max resolution from configuration
        val maxResolution = currentCameraConfiguration.getAvailableResolutions()[0]

        if (isPreviewResolution) {
            // Set thumbnail resolution for preview
            currentCaptureOptions.setResolution(
                Size(
                    maxResolution.width / previewImageReductionFactor,
                    maxResolution.height / previewImageReductionFactor
                )
            )
        } else {
            // Set original resolution for photo
            currentCaptureOptions.setResolution(
                Size(maxResolution.width, maxResolution.height)
            )
        }
    }


    // Capture


    // Process new frame from capture session
    private fun onNewFrameCaptured(cameraImage: Image) {

        // Check is photo capture now

        if (isWaitForPhotoCapture) {
            // Photo captured

            // Check resolution (only currentCaptureOptions resolution)
            if (Size(cameraImage.width, cameraImage.height) != currentCaptureOptions
                    .getResolution()) { return }

            // Reset frame capture trigger
            isWaitForPhotoCapture = false

            // Enable preview again
            restartCaptureSession(true)

            // Translate image to bitmap
            val buffer: ByteBuffer = cameraImage.planes[0].buffer
            val bytes = ByteArray(buffer.remaining())
            buffer.get(bytes)
            val imageBitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)

            // Generate standard image
            var newImage = StandardImage(
                imageBitmap, currentCameraConfiguration.getRotationRelativeDeviceNormal()
            )
            // Rotate to normal
            newImage = newImage.getRotatedToNormalImage()

            // Save image
            dataFilesController.saveImage(newImage)

            // Reset state
            isReadyToPhotoCapture = true

        } else {
            // Preview captured

            // Translate image to bitmap
            val buffer: ByteBuffer = cameraImage.planes[0].buffer
            val bytes = ByteArray(buffer.remaining())
            buffer.get(bytes)
            val imageBitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)

            // Generate standard image
            var newImage = StandardImage(
                imageBitmap, currentCameraConfiguration.getRotationRelativeDeviceNormal()
            )

            // Crop preview image to original aspect ratio
            val targetResolution = currentCaptureOptions.getResolution()
            newImage = newImage.cropImageByNewAspectRatio(
                targetResolution.width.toFloat() / targetResolution.height.toFloat()
            )

            // Check resolution (bigger than currentCaptureOptions is not available)
            val imgRes = newImage.getImageResolution()
            val curRes = currentCaptureOptions.getResolution()
            val confRes = currentCameraConfiguration.getAvailableResolutions()
            val minRes = confRes[confRes.size - 1]
            if (imgRes.width > curRes.width && imgRes.width > minRes.width * 2 ||
                imgRes.height > curRes.height && imgRes.height > minRes.height * 2
            ) {
                return
            }

            // Set as camera preview image
            modelData.setCameraPreviewImage(newImage)
        }
    }


    // Capture photo
    private fun capturePhoto() {
        // Check state
        if (!isReadyToPhotoCapture || !isCaptureSessionAvailable) { return }
        // Capture request
        isWaitForPhotoCapture = true
        isReadyToPhotoCapture = false
        restartCaptureSession(false)
    }


    // Controls


    // Button callbacks
    private fun assignButtonsCallbacks() {

        // Switch camera button callback
        uiEventHandler.assignSwitchCameraButtonCallback{
            // Get cameras
            var camerasToSelect: List<String> = arrayListOf()
            for (cameraConfiguration in cameraConfigurations) {
                camerasToSelect = camerasToSelect.plus(
                    "Camera " + cameraConfiguration.getCameraServiceId()
                )
            }
            // Open selector
            modelData.openSelector("Select camera", camerasToSelect,
                currentCameraConfigurationId) { optionIndex ->
                if (optionIndex != currentCameraConfigurationId) {
                    currentCameraConfigurationId = optionIndex
                    stopCaptureSession()
                    clearStartCaptureSession(cameraConfigurations[currentCameraConfigurationId])
                }
            }
        }

        // Capture button callback
        uiEventHandler.assignCaptureButtonCallback {
            capturePhoto()
        }
    }
}
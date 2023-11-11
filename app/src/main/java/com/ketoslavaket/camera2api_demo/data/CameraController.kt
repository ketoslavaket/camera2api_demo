package com.ketoslavaket.camera2api_demo.data


import android.app.Activity
import android.content.Context
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.media.Image
import android.util.Log
import android.util.Size
import android.view.SurfaceHolder


class CameraController(private var mainActivity: Activity) {


    // Settings


    companion object {
        const val LOG_TAG = "CameraController"
    }


    // Variables


    private var cameraManager: CameraManager? = null
    private var cameraServicesArray: Array<CameraService> = arrayOf()
    private var activeCameraService: CameraService? = null


    // Public


    fun setupCameraController(
        callback: (camerasConfiguration: List<CameraConfiguration>) -> Unit
    ) {
        setupCameraController()
        val camerasConfiguration = getCamerasConfiguration()
        callback(camerasConfiguration)
    }


    fun startCaptureSession(
        cameraId: Int,
        isForPreview: Boolean,
        captureOptions: CaptureOptions,
        onNewFrameCapturedCallback: (image: Image) -> Unit
    ) {
        // Log
        Log.i(LOG_TAG, "CameraController, Start camera service with id: $cameraId")
        // Start camera service
        cameraServicesArray[cameraId].openCamera(
            isForPreview, captureOptions, onNewFrameCapturedCallback)
        // Set as active camera service
        activeCameraService = cameraServicesArray[cameraId]
    }


    fun updateCaptureSession(captureOptions: CaptureOptions) {
        activeCameraService?.updateCaptureSession(captureOptions)
    }


    fun stopCaptureSession() {
        // Log
        Log.i(LOG_TAG, "CameraController, Stop camera capture session")
        // Check is camera service exists
        activeCameraService?.closeCamera()
        activeCameraService = null
    }


    // Private


    private fun getCamerasConfiguration(): List<CameraConfiguration> {

        // Create device cameras configuration list
        var deviceCamerasConfiguration: List<CameraConfiguration> = arrayListOf()

        // Get all cameras characteristics
        var cameraServiceId = 0
        for (cameraService in cameraServicesArray) {

            // Get camera device id
            val cameraDeviceId = cameraService.getCameraDeviceId()

            // get camera raw characteristics from android by id
            val cameraCharacteristics = cameraManager!!.getCameraCharacteristics(cameraDeviceId)

            // Get available resolutions
            var availableResolutions: List<Size> = arrayListOf()
            val map = cameraCharacteristics.get(
                CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)
            if (map != null) {
                availableResolutions = map.getOutputSizes(SurfaceHolder::class.java).toList()
            }

            // Create CameraCharacteristics object
            val cameraConfiguration = CameraConfiguration(

                // Camera service id
                cameraServiceId,

                // Image sizes
                availableResolutions,
                cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION),

                // Iso, Shutter speed, aperture
                cameraCharacteristics.get(CameraCharacteristics.SENSOR_INFO_SENSITIVITY_RANGE),
                cameraCharacteristics.get(CameraCharacteristics.SENSOR_INFO_EXPOSURE_TIME_RANGE),
                cameraCharacteristics.get(CameraCharacteristics.LENS_INFO_AVAILABLE_APERTURES),
            )

            // Add camera configuration to array
            deviceCamerasConfiguration = deviceCamerasConfiguration.plus(cameraConfiguration)

            // Next id
            cameraServiceId += 1
        }

        return deviceCamerasConfiguration
    }


    private fun setupCameraController() {

        // Get camera manager
        cameraManager = mainActivity.getSystemService(Context.CAMERA_SERVICE) as CameraManager
        // Get device cameras array
        val deviceCamerasIdArray = cameraManager!!.cameraIdList

        // Reset camera services array
        cameraServicesArray = emptyArray()

        for (deviceCameraIndex in deviceCamerasIdArray) {

            // Log
            Log.i(LOG_TAG, "CameraController, " +
                    "Setup CameraService with id : $deviceCameraIndex")

            // Create new camera device
            val newCameraService = cameraManager?.let {
                    CameraService(mainActivity, it, deviceCameraIndex)
            }

            // Add camera device to array
            if (newCameraService != null) {
                cameraServicesArray = cameraServicesArray.plus(newCameraService)
            }
        }
    }
}



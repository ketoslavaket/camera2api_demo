package com.ketoslavaket.camera2api_demo.data


import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.graphics.ImageFormat
import android.graphics.SurfaceTexture
import android.hardware.camera2.CameraCaptureSession
import android.hardware.camera2.CameraDevice
import android.hardware.camera2.CameraManager
import android.hardware.camera2.CaptureRequest
import android.media.Image
import android.media.ImageReader
import android.os.Handler
import android.util.Log
import android.view.Surface
import androidx.core.content.ContextCompat


class CameraService(
    private var mainActivity: Activity,
    private var cameraManager: CameraManager,
    private var cameraDeviceId: String
) {


    // Settings


    companion object {
        private const val LOG_TAG = "CameraService"
    }


    // Variables


    // Camera
    private var cameraDevice: CameraDevice? = null
    private var captureSession: CameraCaptureSession? = null
    // Capture
    private var isEnableRepeatingCapture = false
    private var currentCaptureOptions = CaptureOptions()
    private var onNewFrameCapturedCallback: (image: Image) -> Unit = {}
    // Capture target
    private var imageReader = ImageReader.newInstance(
        1000, 1000, ImageFormat.JPEG, 1)
    private var captureTargetSurface = Surface(SurfaceTexture(0))


    // Public


    fun getCameraDeviceId(): String {
        return cameraDeviceId
    }


    fun openCamera(
        isForRepeatingCapture: Boolean,
        captureOptions: CaptureOptions,
        newOnNewFrameCapturedCallback: (image: Image) -> Unit
    ) {

        // Assign variables
        isEnableRepeatingCapture = isForRepeatingCapture
        currentCaptureOptions = captureOptions
        onNewFrameCapturedCallback = newOnNewFrameCapturedCallback

        // Create capture target to capture frames
        createImageReader()
        captureTargetSurface = imageReader.surface

        if (ContextCompat.checkSelfPermission(mainActivity, Manifest.permission.CAMERA) ==
            PackageManager.PERMISSION_GRANTED
        ) {
            // Log
            Log.i(LOG_TAG, "CameraService, Try to open camera device")

            // Try to open camera, send execute signal
            cameraManager.openCamera(cameraDeviceId, cameraCallback, null)

        } else {
            // Log
            Log.e(LOG_TAG, "EXC: CameraService, openCamera, permissions")
        }
    }


    fun updateCaptureSession(captureOptions: CaptureOptions) {

        // Assign variables
        currentCaptureOptions = captureOptions

        // Rebuild capture session
        if (isCaptureSessionAvailable()) {
            try {
                buildCaptureSession()
            } catch (e: java.lang.IllegalStateException) {
                Log.w("MyLogs", "EXC while updateCaptureSession")
            }
        }
    }


    fun closeCamera() {
        // Log
        Log.i(LOG_TAG, "CameraService, Close camera device")

        // Close camera
        cameraDevice?.close()
        cameraDevice = null
        captureSession = null
    }


    // Private


    private fun createImageReader() {
        // Choose an appropriate size for the ImageReader based on your requirements
        val imageSize = currentCaptureOptions.getResolution()

        // Create ImageReader
        imageReader = ImageReader.newInstance(
            imageSize.width,
            imageSize.height,
            ImageFormat.JPEG, // Use the format that suits your needs
            1 // Max images in the reader's queue
        )

        // Set up listener for each captured frame
        imageReader.setOnImageAvailableListener(
            { reader ->
                val image: Image? = reader?.acquireLatestImage()
                image?.let {
                    onNewFrameCapturedCallback(it)
                    it.close()
                }
            },
            Handler(mainActivity.mainLooper) // You can change this handler as needed
        )
    }


    private fun buildCaptureSession() {

        if (cameraDevice == null) { return }

        // Create capture request
        val captureRequestBuilder =
            cameraDevice!!.createCaptureRequest(CameraDevice.TEMPLATE_MANUAL)

        // Configure builder
        captureRequestBuilder.apply {

            // Default value options

            // Control mode
            set(CaptureRequest.CONTROL_MODE, CaptureRequest.CONTROL_MODE_AUTO)

            // Black level
            set(CaptureRequest.BLACK_LEVEL_LOCK, false)

            // Color aberration fix
            set(CaptureRequest.COLOR_CORRECTION_ABERRATION_MODE,
                CaptureRequest.COLOR_CORRECTION_ABERRATION_MODE_HIGH_QUALITY)

            // Anti-banding mode
            set(CaptureRequest.CONTROL_AE_ANTIBANDING_MODE,
                CaptureRequest.CONTROL_AE_ANTIBANDING_MODE_AUTO)

            // Disable auto exposure
            set(CaptureRequest.CONTROL_AE_PRECAPTURE_TRIGGER,
                CaptureRequest.CONTROL_AE_PRECAPTURE_TRIGGER_IDLE)
            set(CaptureRequest.CONTROL_AE_MODE, CaptureRequest.CONTROL_AE_MODE_OFF)

            // Focus
            set(CaptureRequest.CONTROL_AF_TRIGGER, CaptureRequest.CONTROL_AF_TRIGGER_IDLE)
            set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_AUTO)

            // White balance
            set(CaptureRequest.CONTROL_AWB_LOCK, false)
            set(CaptureRequest.CONTROL_AWB_MODE, CaptureRequest.CONTROL_AWB_MODE_AUTO)

            // Controlled options

            // Iso, Shutter speed, Aperture
            set(CaptureRequest.SENSOR_SENSITIVITY, currentCaptureOptions.getIso())
            set(CaptureRequest.SENSOR_EXPOSURE_TIME, currentCaptureOptions.getShutterSpeed())
            set(CaptureRequest.LENS_APERTURE, currentCaptureOptions.getAperture())

            // Capture targets

            // Add the ImageReader surface to the targets
            addTarget(captureTargetSurface)
        }

        // Advanced check
        if (isCaptureSessionAvailable()) {
            // Start capture
            if (isEnableRepeatingCapture) {
                // With repeating
                captureSession?.setRepeatingRequest(captureRequestBuilder.build(), null, null)
            } else {
                // Single frame capture
                captureSession?.capture(captureRequestBuilder.build(), null, null)
            }
        }
    }


    private fun isCaptureSessionAvailable(): Boolean {
        // check is captureSession available
        return cameraDevice != null && captureSession != null
    }


    // Callback after camera device initialization
    private val cameraCallback = object : CameraDevice.StateCallback() {

        override fun onOpened(camera: CameraDevice) {
            // Set camera device
            cameraDevice = camera

            // Log
            Log.i(LOG_TAG, "CameraService, Opened camera device")

            // Create CaptureSession
            createCameraCaptureSession()
        }

        override fun onDisconnected(camera: CameraDevice) {
            // Log
            Log.i(LOG_TAG, "CameraService, Disconnected camera device")

            // Close camera device
            closeCamera()
        }

        override fun onError(camera: CameraDevice, error: Int) {
            // Log
            Log.e(LOG_TAG, "CameraService, Error while opening camera device, error: $error")

            // Close camera device
            closeCamera()
        }
    }


    // Create capture session for capture
    private fun createCameraCaptureSession() {

        // Create capture session
        @Suppress("DEPRECATION")
        cameraDevice?.createCaptureSession(mutableListOf(captureTargetSurface),
            object : CameraCaptureSession.StateCallback() {

                override fun onConfigured(session: CameraCaptureSession) {
                    // Assign variable
                    captureSession = session

                    // Log
                    Log.i(LOG_TAG, "CameraService, Camera capture session configured !")

                    // Start capture session
                    buildCaptureSession()
                }

                override fun onConfigureFailed(session: CameraCaptureSession) {
                    // Assign variable
                    captureSession = null

                    // Log
                    Log.e(LOG_TAG, "EXC: CameraService, "+
                            "Camera capture session configure error !")
                }

            }, null)
    }
}
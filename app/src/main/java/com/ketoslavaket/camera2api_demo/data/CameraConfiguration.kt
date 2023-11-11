package com.ketoslavaket.camera2api_demo.data

import android.util.Range
import android.util.Size

class CameraConfiguration (
    // Device id
    private val cameraServiceId: Int,
    // Image sizes
    private val newAvailableResolutions: List<Size>,
    private var newRotationRelativeDeviceNormal: Int?,
    // Iso, Shutter speed, Aperture
    private val newAvailableIsoRange: Range<Int>?,
    private val newAvailableShutterSpeedRange: Range<Long>?,
    private val newAvailableApertureSteps: FloatArray?,
) {


    // Variables


    // Image resolution
    private var availableResolutions: List<Size> = arrayListOf(Size(1000, 1000))
    private var rotationRelativeDeviceNormal: Float = 0.0F
    // Iso, Shutter speed, Aperture
    private var availableIsoRange: Range<Int> = Range(0, 14000)
    private var availableShutterSpeedRange: Range<Long> = Range(0, 16000000)
    private var availableApertureSteps: List<Float> = arrayListOf(0.0F)


    // Init


    init {

        // Resolution
        if (newAvailableResolutions.isNotEmpty()) {
            availableResolutions = newAvailableResolutions.sortedByDescending {
                it.width * it.height } }

        // Rotation
        if (newRotationRelativeDeviceNormal != null) {
            rotationRelativeDeviceNormal =
                ((newRotationRelativeDeviceNormal!! + 360) % 360).toFloat()
        }

        // Iso, Shutter speed, Aperture
        if (newAvailableIsoRange != null) { availableIsoRange = newAvailableIsoRange }
        if (newAvailableShutterSpeedRange != null)
            { availableShutterSpeedRange = newAvailableShutterSpeedRange }
        if (newAvailableApertureSteps != null && newAvailableApertureSteps.isNotEmpty())
            { availableApertureSteps = newAvailableApertureSteps.toList() }
    }


    // Public


    // Get parameters
    fun getCameraServiceId(): Int {return cameraServiceId}
    fun getAvailableResolutions(): List<Size> {return availableResolutions}
    fun getRotationRelativeDeviceNormal(): Float {return rotationRelativeDeviceNormal}
    fun getAvailableIsoRange(): Range<Int> {return availableIsoRange}
    fun getAvailableShutterSpeedRange(): Range<Long> { return availableShutterSpeedRange }
}
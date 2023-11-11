package com.ketoslavaket.camera2api_demo.data


import android.util.Size


class CaptureOptions {


    // Variables


    // Image size
    private var resolution: Size = Size(1920, 1920)

    // Iso, Shutter speed, Aperture
    private var iso = 400
    private var shutterSpeed: Long = 16000000
    private var aperture = 0F


    // Public


    // Resolution
    fun setResolution(newValue: Size) {resolution = newValue}
    fun getResolution(): Size {return resolution}


    // Iso, Shutter speed, Aperture
    fun setIso(newValue: Int) {iso = newValue}
    fun getIso(): Int {return iso}
    fun setShutterSpeed(newValue: Long) {shutterSpeed = newValue}
    fun getShutterSpeed(): Long {return shutterSpeed}
    fun setAperture(newValue: Float) {aperture = newValue}
    fun getAperture(): Float {return aperture}
}
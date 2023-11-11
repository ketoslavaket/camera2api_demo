package com.ketoslavaket.camera2api_demo.data


import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import android.util.Log


class PermissionsController (private var mainActivity: Activity) {


    // Settings

    companion object {
        const val LOG_TAG = "myLogs"
    }
    private val permissionToRequest = arrayOf(
        Manifest.permission.CAMERA,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
    )
    private val permissionMinSdkVersion = arrayOf(
        25,
        25,
    )
    private val permissionMaxSdkVersion = arrayOf(
        999,
        32,
    )


    // Variables

    private var resultOfObtainingPermissions: ((result: Boolean) -> Unit)? = null


    // Public

    fun requestPermissions(callback: (result: Boolean) -> Unit) {
        // Log
        Log.i(LOG_TAG, "PermissionsController, Request permissions")

        // Assign variables
        resultOfObtainingPermissions = callback

        // Process
        checkAndRequestPermissionsFromUser()
    }


    // Permissions request callback
    fun requestPermissionsResultCallback(requestCode: Int, permissions: Array<String>,
                                         grantResults: IntArray) {
        Log.i(LOG_TAG, "PermissionsController, process permissions request callback")
        onRequestPermissionsResult(requestCode, permissions, grantResults)
    }


    // Private

    // Check permissions, continue with success or request permissions
    private fun checkAndRequestPermissionsFromUser() {

        // Check is all permissions granted
        var isAllPermissionsGranted = true
        for (permission in permissionToRequest) {
            if (ContextCompat.checkSelfPermission(mainActivity, permission)
                != PackageManager.PERMISSION_GRANTED) { isAllPermissionsGranted = false } }

        if (!isAllPermissionsGranted)
        {
            // If permissions not granted, request permissions
            Log.i(LOG_TAG, "PermissionsController, Request permissions from user")
            ActivityCompat.requestPermissions(mainActivity, permissionToRequest, 1)
        } else {
            // If all permissions already granted
            Log.i(LOG_TAG, "PermissionsController, All permissions already granted")
            resultOfObtainingPermissions?.let { it(true) }
        }
    }


    // Process permission request result
    private fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>,
        grantResults: IntArray)
    {
        // Check request code
        if (requestCode != 1) { return }

        // Get info
        val sdkVersion = android.os.Build.VERSION.SDK_INT

        // Check is all permissions granted
        var isAllPermissionsGranted = true
        var i = 0
        while (i < permissions.size) {

            // Get info
            val permission = permissions[i]
            val grantResult = grantResults[i]
            var isItAnException = false

            // Process SDK version exceptions

            // Find permission index in array
            var permissionIndex = -1
            var i2 = 0
            while (i2 < permissionToRequest.size){
                if (permission == permissionToRequest[i2]) {
                    permissionIndex = i2
                }
                i2++
            }

            // Check is it an exception
            if (sdkVersion < permissionMinSdkVersion[permissionIndex] ||
                sdkVersion > permissionMaxSdkVersion[permissionIndex]) {
                isItAnException = true
            }

            // Check permission status

            if ((grantResult != PackageManager.PERMISSION_GRANTED) && !isItAnException) {
                isAllPermissionsGranted = false
            }

            i += 1
        }

        if (isAllPermissionsGranted) {
            // All permissions granted
            Log.i(LOG_TAG, "PermissionsController, All permissions is granted")
            resultOfObtainingPermissions?.let { it(true) }
        } else {
            // Permissions not granted
            Log.w(LOG_TAG, "PermissionsController, Permissions is not granted")
            resultOfObtainingPermissions?.let { it(false) }
        }
    }
}
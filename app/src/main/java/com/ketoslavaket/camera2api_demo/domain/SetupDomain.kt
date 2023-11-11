package com.ketoslavaket.camera2api_demo.domain


import com.ketoslavaket.camera2api_demo.data.PermissionsController
import com.ketoslavaket.camera2api_demo.presentation.ModelData
import com.ketoslavaket.camera2api_demo.presentation.UiEventHandler

import android.app.Activity
import android.util.Log


class SetupDomain (private val mainActivity: Activity, private val modelData: ModelData,
                   private val uiEventHandler: UiEventHandler,
                   private val permissionsController: PermissionsController) {


    // Settings


    companion object {
        const val LOG_TAG = "myLogs"
    }


    // Variables


    private var setupCallback: ((result: Boolean) -> Unit)? = null


    // Public


    fun startSetup(callback: (result: Boolean) -> Unit) {
        // Assign variables
        setupCallback = callback

        // Log
        Log.i(LOG_TAG, "SetupState, Start setup")

        // Setup
        requestPermissions()
    }


    // Private


    // Request permissions, use permissionsController
    private fun requestPermissions() {
        permissionsController.requestPermissions { result ->
            // Process permissions request result
            if (result) {
                finishSetup()
            } else {
                Log.e(LOG_TAG, "WRITE: SetupState, requestPermissions(), false")
            }
        }
    }


    private fun finishSetup() {
        // Log
        Log.i(LOG_TAG, "SetupState, Finish setup")

        // Return result
        setupCallback?.let { it(true) }
    }
}
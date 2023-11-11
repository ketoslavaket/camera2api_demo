package com.ketoslavaket.camera2api_demo


import com.ketoslavaket.camera2api_demo.domain.MainAppDomain
import com.ketoslavaket.camera2api_demo.presentation.ModelData
import com.ketoslavaket.camera2api_demo.presentation.UiEventHandler
import com.ketoslavaket.camera2api_demo.ui.UserInterface

import android.os.Bundle
import android.view.KeyEvent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent


class MainActivity : ComponentActivity() {


    // Variables

    private var userInterface: UserInterface? = null
    private var modelData: ModelData? = null
    private var uiEventHandler: UiEventHandler? = null
    private var mainAppDomain: MainAppDomain? = null


    // Enter point

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Create binders components
        modelData = ModelData()
        uiEventHandler = UiEventHandler(modelData!!)

        // Create base components
        userInterface = UserInterface(this, modelData!!, uiEventHandler!!)
        mainAppDomain = MainAppDomain(this, modelData!!, uiEventHandler!!)

        // Setup interface
        setContent{  // Setup user interface
            userInterface!!.SetupUserInterface()
        }

        // Start app
        mainAppDomain!!.start()
    }


    // Private

    private fun hideSystemUi() {
        // actionBar?.hide()
    }


    // activity state callbacks
    override fun onPause() {
        super.onPause()
        // Process in domainController
        mainAppDomain?.onNewActivityState("onPause")
    }
    override fun onResume() {
        super.onResume()
        // Interface
        hideSystemUi()
        // Process in domainController
        mainAppDomain?.onNewActivityState("OnResume")
    }


    // Permissions request callback
    @Deprecated("Deprecated in Java (Today don't have a Kotlin solve)")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>,
                                            grantResults: IntArray) {
        @Suppress("DEPRECATION")
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        // Process in domainController
        mainAppDomain?.requestPermissionsResultCallback(requestCode, permissions, grantResults)
    }


    // Buttons event handler
    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        return when (keyCode) {
            KeyEvent.KEYCODE_VOLUME_DOWN -> { true /* handle (empty) */ }
            KeyEvent.KEYCODE_VOLUME_UP -> { true /* handle (empty) */ }
            else -> super.onKeyDown(keyCode, event)
        }
    }
}
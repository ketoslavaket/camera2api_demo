package com.ketoslavaket.camera2api_demo.ui


import com.ketoslavaket.camera2api_demo.presentation.ModelData
import com.ketoslavaket.camera2api_demo.presentation.UiEventHandler

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp


class DialogBoxes (private var mainActivity: Activity, private var modelData: ModelData,
                   private var uiEventHandler: UiEventHandler, private val uiScale: Float,
                   private var generalComponents: GeneralComponents) {

    @Composable
    fun SelectorPage() {

        // Get selector options
        val selectorOptions = modelData.selectorOptions.collectAsState().value

        Column (
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .height(uiScale.dp * 12.5F)
                    .width(uiScale.dp * 65)
                    .background(Color(0x99333333))
            ) {
                Row (
                    modifier = Modifier
                        .matchParentSize()
                        .padding(all = uiScale.dp * 2)
                ) {
                    generalComponents.TextMain(
                        text = modelData.selectorLabel.collectAsState().value,
                        containerModifier = Modifier
                            .fillMaxSize())
                }
            }
            Box(
                modifier = Modifier
                    .height(uiScale.dp * 50)
                    .width(uiScale.dp * 65)
                    .background(Color(0x991A1A1A))
            ) {
                LazyColumn (
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .matchParentSize()
                ) {
                    items(selectorOptions.size) {blockIndex ->
                        SelectorOptionBlock(blockIndex)
                    }
                }
            }
        }
    }


    @Composable
    fun SelectorOptionBlock(blockIndex: Int) {
        Box(
            modifier = Modifier
                .height(uiScale.dp * 10)
                .width(uiScale.dp * 65)
                .background(Color(0x991A1A1A))
                .clickable {
                    modelData.selectorOptionSelected(blockIndex)
                }
        ) {
            Row(
                modifier = Modifier
                    .matchParentSize()
            ){

                // Calculate selector point color
                var selectorPointColor = Color(0x80FFFFFF)
                if (modelData.selectorDefaultOption.collectAsState().value == blockIndex)
                { selectorPointColor = Color(0xFFFFFFFF)
                }

                Box(
                    modifier = Modifier
                        .height(uiScale.dp * 10)
                        .width(uiScale.dp * 10)
                ) {
                    Column (
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .fillMaxSize()
                    ) {
                        Box(
                            modifier = Modifier
                                .height(uiScale.dp * 4)
                                .width(uiScale.dp * 4)
                                .background(selectorPointColor)
                        )
                    }
                }
                generalComponents.TextMain(
                    text = modelData.selectorOptions.collectAsState().value[blockIndex],
                    containerModifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}



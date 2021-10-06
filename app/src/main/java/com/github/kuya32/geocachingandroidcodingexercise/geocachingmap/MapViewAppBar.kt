package com.github.kuya32.geocachingandroidcodingexercise.geocachingmap

import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

@Composable
fun MapViewAppBar(viewModel: MapViewViewModel) {
    TopAppBar(
        elevation = 4.dp,
        title = {
            Text(text = "Geocaching")
        },
        backgroundColor = MaterialTheme.colors.primary,
        actions = {
            IconButton(
                onClick = {
                    if (!viewModel.isNavigationRequested.value) {
                        viewModel.updatedNavigationRequest(true)
                    } else {
                        viewModel.updatedNavigationRequest(false)
                    }

                }
            ) {
                Icon(imageVector = Icons.Filled.Navigation, contentDescription = "Navigate Icon")
            }

            IconButton(
                onClick = {
                    if (viewModel.isNavigationRequested.value) {
                        viewModel.updatedCalculationRequest(true)
                    } else {
                        println("Must enable navigation first, before calculating distance.")
                    }

                }
            ) {
                Icon(
                    imageVector = Icons.Filled.Calculate,
                    contentDescription = "Calculate Distance Icon"
                )
            }
        }
    )
}
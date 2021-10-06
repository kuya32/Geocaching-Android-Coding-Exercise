package com.github.kuya32.geocachingandroidcodingexercise.geocachingmap

import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.github.kuya32.geocachingandroidcodingexercise.MainActivity
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.maps.model.LatLng

@ExperimentalPermissionsApi
@Composable
fun MapViewScreen(
    viewModel: MapViewViewModel = hiltViewModel(),
    activity: MainActivity
) {
    Scaffold(
        topBar = { MapViewAppBar(viewModel) }
    ) {
        viewModel.getLocationPermission(LocalContext.current)
        val destination = LatLng(viewModel.userCurrentLat.value, viewModel.userCurrentLng.value)
        LoadMapView(viewModel = viewModel, destination = destination, activity)
    }
}
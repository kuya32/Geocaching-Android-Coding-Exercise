package com.example.geocachingandroidcodingexercise.geocachingMap


import android.annotation.SuppressLint
import android.os.Bundle
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.NavController
import com.google.android.libraries.maps.CameraUpdateFactory
import com.google.android.libraries.maps.MapView
import com.google.android.libraries.maps.model.BitmapDescriptorFactory
import com.google.android.libraries.maps.model.LatLng
import com.google.android.libraries.maps.model.MarkerOptions
import com.google.maps.android.ktx.awaitMap
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.IllegalStateException
import com.example.geocachingandroidcodingexercise.R


@Composable
fun MapViewScreen(
    navController: NavController,
    viewModel: MapViewViewModel = hiltViewModel()
) {
    Scaffold(
        topBar = { MapViewAppBar() },
        floatingActionButtonPosition = FabPosition.Center,
        floatingActionButton = { FloatingActionButton(
            onClick = { /*TODO*/ },
        ) {
            Image(
                imageVector = Icons.Filled.Add,
                contentDescription = "Add Icon",
                colorFilter = ColorFilter.tint(Color.White)
            )
        }
        }
    ) {
        viewModel.getLocationPermission(LocalContext.current)
        val destination = LatLng(viewModel.userCurrentLat.value, viewModel.userCurrentLng.value)
        LoadMapView(viewModel = viewModel, destination = destination)
    }
}

@Composable
fun MapViewAppBar() {
    TopAppBar(
        elevation = 4.dp,
        title = {
            Text(text = "Geocaching")
        },
        backgroundColor = MaterialTheme.colors.primary,
        actions = {
            IconButton(onClick = { /*TODO*/ }) {
                Icon(imageVector = Icons.Filled.Navigation, contentDescription = "Navigate Icon")
            }
            IconButton(onClick = { /*TODO*/ }) {
                Icon(imageVector = Icons.Filled.Calculate, contentDescription = "Calculate Distance Icon")
            }
        }
    )
}

@SuppressLint("MissingPermission")
@Composable
fun LoadMapView(viewModel: MapViewViewModel, destination: LatLng) {
    val mapView = rememberMapViewLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        AndroidView( {mapView} ) {
            CoroutineScope(Dispatchers.Main).launch {
                val map = mapView.awaitMap()
                map.clear()
                map.uiSettings.isZoomControlsEnabled = true
                map.isMyLocationEnabled = true
                
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(destination, 18f))
            }
        }
    }
}

@Composable
fun rememberMapViewLifecycle(): MapView {
    val context = LocalContext.current

    val mapView = remember {
        MapView(context).apply { id = R.id.map_frame }
    }
    val lifeCycleObserver = rememberMapLifecycleObserver(mapView = mapView)
    val lifeCycle = LocalLifecycleOwner.current.lifecycle
    DisposableEffect(lifeCycle) {
        lifeCycle.addObserver(lifeCycleObserver)
        onDispose {
            lifeCycle.removeObserver(lifeCycleObserver)
        }
    }

    return mapView
}

@Composable
fun rememberMapLifecycleObserver(mapView: MapView): LifecycleEventObserver = remember(mapView) {
    LifecycleEventObserver { _, event ->
        when (event) {
            Lifecycle.Event.ON_CREATE -> mapView.onCreate(Bundle())
            Lifecycle.Event.ON_START -> mapView.onStart()
            Lifecycle.Event.ON_RESUME -> mapView.onResume()
            Lifecycle.Event.ON_PAUSE -> mapView.onPause()
            Lifecycle.Event.ON_STOP -> mapView.onStop()
            Lifecycle.Event.ON_DESTROY -> mapView.onDestroy()
            else -> throw IllegalStateException()
        }
    }
}



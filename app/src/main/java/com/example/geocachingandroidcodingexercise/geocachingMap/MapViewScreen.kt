package com.example.geocachingandroidcodingexercise.geocachingMap


import android.annotation.SuppressLint
import android.os.Bundle
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.android.volley.toolbox.Volley
import com.example.geocachingandroidcodingexercise.BuildConfig
import com.example.geocachingandroidcodingexercise.MainActivity
import com.google.android.libraries.maps.CameraUpdateFactory
import com.google.android.libraries.maps.MapView
import com.google.android.libraries.maps.model.MarkerOptions
import com.google.maps.android.ktx.awaitMap
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.IllegalStateException
import com.example.geocachingandroidcodingexercise.R
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.maps.model.LatLng
import org.json.JSONObject

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

@ExperimentalPermissionsApi
@SuppressLint("MissingPermission")
@Composable
fun LoadMapView(viewModel: MapViewViewModel, destination: LatLng, activity: MainActivity) {
    val mapView = rememberMapViewLifecycle()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        val openDialog = remember {
            mutableStateOf(true)
        }
        AndroidView({ mapView }) {
            CoroutineScope(Dispatchers.Main).launch {
                val map = mapView.awaitMap()
                map.uiSettings.isZoomControlsEnabled = true
                map.isMyLocationEnabled = true

                if (viewModel.isNewLocationPined.value && viewModel.isNavigationRequested.value) {

                    val directionRequest = viewModel.directionsRequestToPolyline(
                        LatLng(viewModel.pinedLat.value, viewModel.pinedLng.value),
                        destination,
                        BuildConfig.GeocachingApiKey,
                        map
                    )

                    val requestQueue = Volley.newRequestQueue(activity)
                    requestQueue.add(directionRequest)

                } else if (viewModel.isNewLocationPined.value && !viewModel.isNavigationRequested.value) {
                    map.clear()

                    val originalPinnedLocation = com.google.android.libraries.maps.model.LatLng(
                        viewModel.pinedLat.value,
                        viewModel.pinedLng.value
                    )
                    map.addMarker(
                        MarkerOptions()
                            .position(originalPinnedLocation)
                            .title("Pinned Location")
                    )
                }
            }
        }
        if (viewModel.isNewLocationPined.value &&
            viewModel.isNavigationRequested.value &&
            viewModel.isCalculationRequested.value
        ) {
            CoroutineScope(Dispatchers.Main).launch {
                val distance: String? = viewModel.getData(
                    LatLng(viewModel.pinedLat.value, viewModel.pinedLng.value),
                    destination,
                    BuildConfig.GeocachingApiKey,
                    activity
                )
                val accumulatedDistanceString: MutableList<List<String>> = ArrayList()
                val jsonResponse = JSONObject(distance)
                val routes = jsonResponse.getJSONArray("routes")
                val legs = routes.getJSONObject(0).getJSONArray("legs")
                val steps = legs.getJSONObject(0).getJSONArray("steps")
                for (i in 0 until steps.length()) {
                    val length = steps.getJSONObject(i).getJSONObject("distance").getString("text")
                    accumulatedDistanceString.add(listOf(length))
                }
                viewModel.updateCalculatedDistance(accumulatedDistanceString[0][0])

            }
            AlertDialog(
                onDismissRequest = {
                    openDialog.value = false
                },
                title = {
                    Text(
                        text = "Distance:",
                        color = Color.Blue,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.Center)
                    )
                },
                text = {
                    Text(
                        text = viewModel.calculatedDistance.value,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.Center)
                    )
                },
                confirmButton = { },
                dismissButton = {
                    Button(
                        onClick = {
                            viewModel.updatedCalculationRequest(false)
                            openDialog.value = false
                        }
                    ) {
                        Text(text = "Cancel")
                    }
                }
            )
        }
        FloatingActionButton(
            modifier = Modifier
                .wrapContentSize()
                .padding(top = 64.dp, end = 4.dp)
                .align(Alignment.TopEnd),
            contentColor = Color.White,
            shape = MaterialTheme.shapes.small.copy(CornerSize(90)),
            content = {
                Image(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "Add Icon",
                    colorFilter = ColorFilter.tint(Color.White)
                )
            },
            onClick = {
                CoroutineScope(Dispatchers.Main).launch {
                    val map = mapView.awaitMap()
                    map.clear()
                    val pinnedLocation =
                        com.google.android.libraries.maps.model.LatLng(
                            viewModel.userCurrentLat.value,
                            viewModel.userCurrentLng.value
                        )

                    map.addMarker(
                        MarkerOptions()
                            .position(pinnedLocation)
                            .title("Pinned Location")
                    )

                    map.animateCamera(CameraUpdateFactory.newLatLngZoom(pinnedLocation, 18f))

                    viewModel.updatedPinedLocation(true)
                    viewModel.getPinedLocation(destination)
                }
            }
        )
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



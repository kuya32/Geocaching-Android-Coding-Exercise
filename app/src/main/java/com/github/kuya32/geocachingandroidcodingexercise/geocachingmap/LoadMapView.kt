package com.github.kuya32.geocachingandroidcodingexercise.geocachingmap

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
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
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.android.volley.toolbox.Volley
import com.github.kuya32.geocachingandroidcodingexercise.BuildConfig
import com.github.kuya32.geocachingandroidcodingexercise.MainActivity
import com.github.kuya32.geocachingandroidcodingexercise.R
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.android.libraries.maps.CameraUpdateFactory
import com.google.android.libraries.maps.MapView
import com.google.android.libraries.maps.model.MarkerOptions
import com.google.maps.android.ktx.awaitMap
import com.google.maps.model.LatLng
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.lang.IllegalStateException

@ExperimentalPermissionsApi
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
                /* Another instance dealing with location permissions. Also this concerns one of the optional task of
                 centering the map on the user location. Instead of using line 82 which auto generates the functionality for me,
                 I could've made another menu item. The onClick functionality would set a boolean variable in my viewModel to
                 true and I would use the map.animatedCamera to zoom to the user's current location. This is something I could
                 implement to improve the application in the future. */
                if (ActivityCompat.checkSelfPermission(
                        activity,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        activity,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {

                    return@launch
                }

                // Creates instance of GoogleMap from this MapView
                val map = mapView.awaitMap()
                map.uiSettings.isZoomControlsEnabled = true
                map.isMyLocationEnabled = true

                // Creates directional poly lines on the map when the navigation menu item is clicked
                if (viewModel.isNewLocationPined.value && viewModel.isNavigationRequested.value) {

                    val directionRequest = viewModel.directionsRequestToPolyline(
                        LatLng(viewModel.pinedLat.value, viewModel.pinedLng.value),
//                        LatLng(37.4326, -122.0880), <- Used these coordinates to show navigation functionality works.
                        destination,
                        BuildConfig.GeocachingApiKey,
                        map
                    )

                    val requestQueue = Volley.newRequestQueue(activity)
                    requestQueue.add(directionRequest)

                    // When the navigation menu item is clicked again, the directional poly lines are removed, but the pinned location stays on the map
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
            // Grabs distance data from Direction API response
            CoroutineScope(Dispatchers.Main).launch {
                val distance: String? = viewModel.getData(
                    LatLng(viewModel.pinedLat.value, viewModel.pinedLng.value),
//                    LatLng(37.4326, -122.0880), <- Used these coordinates to show navigation functionality works.
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

            // Alerts user of the distance from the pinned location
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
                /* The total distance is giving in feet, but I could incorporate a function that calculates and changes
                   the distance in miles. Something to implement to further enhance the app. */
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

        // When the button is clicked, a marker is shown on the map where the user is located.
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
                    viewModel.setPinedLocation(destination)
                }
            }
        )
    }
}

/* The function is creating my MapView. The function also binds the observer composable to the lifecycle of this composable.
   So whatever happens to this composable the observer composable follows.
   Link: https://github.com/kahdichienja/jetMap/blob/main/app/src/main/java/com/kchienja/jetmap/MainActivity.kt */
@Composable
private fun rememberMapViewLifecycle(): MapView {
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

/* This function is listening to the lifecycle events and telling the mapView to follow accordingly.
   For example, when the container is created, the MapView is told that it is created and should also be created.
   Link: https://github.com/kahdichienja/jetMap/blob/main/app/src/main/java/com/kchienja/jetmap/MainActivity.kt */
@Composable
private fun rememberMapLifecycleObserver(mapView: MapView): LifecycleEventObserver =
    remember(mapView) {
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
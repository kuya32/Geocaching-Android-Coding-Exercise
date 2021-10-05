package com.example.geocachingandroidcodingexercise.geocachingMap

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Looper
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.google.android.gms.location.*
import com.google.android.libraries.maps.GoogleMap
import com.google.android.libraries.maps.MapView
import com.google.android.libraries.maps.model.PolylineOptions
import com.google.maps.android.PolyUtil
import com.google.maps.model.LatLng
import org.json.JSONObject


class MapViewViewModel : ViewModel() {
    private lateinit var locationCallback: LocationCallback

    var locationPermissionGranted = mutableStateOf(false)

    var isNewLocationPined = mutableStateOf(false)

    var isNavigationRequested = mutableStateOf(false)

    private var _userCurrentLat = mutableStateOf(0.0)
    var userCurrentLat: MutableState<Double> = _userCurrentLat

    private var _userCurrentLng = mutableStateOf(0.0)
    var userCurrentLng: MutableState<Double> = _userCurrentLng

    val userCurrentLocation = LatLng(userCurrentLat.value, userCurrentLng.value)

    private var _pinedLat = mutableStateOf(0.0)
    var pinedLat: MutableState<Double> = _pinedLat

    private var _pinedLng = mutableStateOf(0.0)
    var pinedLng: MutableState<Double> = _pinedLng

    val pinnedLocation = LatLng(pinedLat.value, pinedLng.value)

    private fun getUserCurrentCoordinates(latLng: LatLng) {
        _userCurrentLat.value = latLng.lat
        _userCurrentLng.value = latLng.lng
    }

    fun getPinedLocation(latLng: LatLng) {
        _pinedLat.value = latLng.lat
        _pinedLng.value = latLng.lng
    }

    fun updatedNavigationRequest(request: Boolean) {
        isNavigationRequested.value = request
    }

    fun updatedPinedLocation(status: Boolean) {
        isNewLocationPined.value = status
    }

    private fun permissionIsGranted(setGranted: Boolean) {
        locationPermissionGranted.value = setGranted
    }

    fun getLocationPermission(context: Context) {
        if (
            ContextCompat.checkSelfPermission(
                context.applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(
                context.applicationContext,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            permissionIsGranted(true)
            getDeviceLocation(context)
        } else {
            Log.d("Location Permission", "Location permission not granted")
        }
    }

    @SuppressLint("MissingPermission")
    fun getDeviceLocation(context: Context) {
        val fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)

        try {
            startLocationUpdates(fusedLocationProviderClient)
            if (locationPermissionGranted.value) {
                val locationResult = fusedLocationProviderClient.lastLocation

                locationResult.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val lastKnownLocation = task.result

                        if (lastKnownLocation != null) {
                            getUserCurrentCoordinates(
                                LatLng(lastKnownLocation.latitude, lastKnownLocation.longitude)
                            )
                        }
                    } else {
                        Log.d("User Location", "Current user location is null")
                    }
                }
            }
        } catch (e: SecurityException) {
            Log.d("Exception", "Exception: ${e.message.toString()}")
        }
    }

    @SuppressLint("MissingPermission")
    fun startLocationUpdates(fusedLocationProviderClient: FusedLocationProviderClient) {
        val locationRequest = LocationRequest.create().apply {
            interval = 1000
            fastestInterval = 500
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                super.onLocationResult(result)
                result.locations.let { locations ->
                    for (location in locations) {
                        getUserCurrentCoordinates(LatLng(location.latitude, location.longitude))
                    }
                }
            }
        }

        fusedLocationProviderClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
    }

    fun directionsRequestToPolyline(origin: LatLng, destination: LatLng, apiKey: String, map: GoogleMap): StringRequest {
        val path: MutableList<List<com.google.android.libraries.maps.model.LatLng>> = ArrayList()
        val urlDirection = "https://maps.googleapis.com/maps/api/directions/json?origin=${origin}&destination=${destination}&key=${apiKey}"
        println(urlDirection)
        val directionRequest = object: StringRequest(Request.Method.GET, urlDirection, Response.Listener<String> { response ->
            val jsonResponse = JSONObject(response)
            println(jsonResponse.toString())

            val routes = jsonResponse.getJSONArray("routes")
            val legs = routes.getJSONObject(0).getJSONArray("legs")
            val steps = legs.getJSONObject(0).getJSONArray("steps")
            for (i in 0 until steps.length()) {
                val points = steps.getJSONObject(i).getJSONObject("polyline").getString("points")
                path.add(PolyUtil.decode(points))
            }
            for (i in 0 until path.size) {
                map.addPolyline(PolylineOptions().addAll(path[i]).color(Color.Blue.hashCode()))
            }

        }, Response.ErrorListener { _ -> }) {}

        return directionRequest
    }
}
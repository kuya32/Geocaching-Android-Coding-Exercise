package com.example.geocachingandroidcodingexercise.geocachingMap

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Looper
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import com.google.android.gms.location.*
import com.google.android.libraries.maps.model.LatLng


class MapViewViewModel: ViewModel() {
    private lateinit var locationCallback: LocationCallback

    var locationPermissionGranted = mutableStateOf(false)

    private var _userCurrentLat = mutableStateOf(0.0)
    var userCurrentLat: MutableState<Double> = _userCurrentLat

    private var _userCurrentLng = mutableStateOf(0.0)
    var userCurrentLng: MutableState<Double> = _userCurrentLng

    val userCurrentLocation = LatLng(userCurrentLat.value, userCurrentLng.value)

    fun getLocationPermission(context: Context) {
        if (
            ContextCompat.checkSelfPermission(context.applicationContext, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(context.applicationContext, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
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
            interval = 10000
            fastestInterval = 5000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        locationCallback = object: LocationCallback() {
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

    private fun getUserCurrentCoordinates(latLng: LatLng) {
        _userCurrentLat.value = latLng.latitude
        _userCurrentLng.value = latLng.longitude
    }

    private fun permissionIsGranted(setGranted: Boolean) {
        locationPermissionGranted.value = setGranted
    }
}
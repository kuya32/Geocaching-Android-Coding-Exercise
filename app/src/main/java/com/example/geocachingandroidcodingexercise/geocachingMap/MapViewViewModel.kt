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
import com.google.maps.model.LatLng
import com.google.android.libraries.maps.MapView
import com.google.android.libraries.maps.model.PolylineOptions
import com.google.maps.DirectionsApi
import com.google.maps.GeoApiContext
import com.google.maps.android.PolyUtil
import com.google.maps.model.DirectionsResult
import com.google.maps.model.TravelMode
import org.joda.time.DateTime
import java.util.concurrent.TimeUnit


class MapViewViewModel : ViewModel() {
    private lateinit var locationCallback: LocationCallback

    var locationPermissionGranted = mutableStateOf(false)

    var isNewLocationPined = mutableStateOf(false)

    private var _userCurrentLat = mutableStateOf(0.0)
    var userCurrentLat: MutableState<Double> = _userCurrentLat

    private var _userCurrentLng = mutableStateOf(0.0)
    var userCurrentLng: MutableState<Double> = _userCurrentLng

    val userCurrentLocation = LatLng(userCurrentLat.value, userCurrentLng.value)

    private var _pinedLat = mutableStateOf(0.0)
    var pinedLat: MutableState<Double> = _pinedLat

    private var _pinedLng = mutableStateOf(0.0)
    var pinedLng: MutableState<Double> = _pinedLng

    private fun getUserCurrentCoordinates(latLng: LatLng) {
        _userCurrentLat.value = latLng.lat
        _userCurrentLng.value = latLng.lng
    }

    private fun getPinedLocation(latLng: LatLng) {
        _pinedLat.value = latLng.lat
        _pinedLng.value = latLng.lng
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

    private fun getGeoContext(): GeoApiContext {
        val geoApiContext = GeoApiContext()
        return geoApiContext.setQueryRateLimit(3)
            .setApiKey("AIzaSyCbwpB26j4oNzGH1Rwkuqyamk8dOjej0cA")
            .setConnectTimeout(1, TimeUnit.SECONDS)
            .setReadTimeout(1, TimeUnit.SECONDS)
            .setWriteTimeout(1, TimeUnit.SECONDS)
    }

    fun getDirectionResult(origin: LatLng, destination: LatLng): DirectionsResult {
        val timeNow = DateTime.now()

        return DirectionsApi.newRequest(getGeoContext())
            .mode(TravelMode.DRIVING)
            .origin(origin)
            .destination(destination)
            .departureTime(timeNow)
            .await()
    }


    fun addPolyline(result: DirectionsResult) {
        val decodedPath = PolyUtil.decode(result.routes[0].overviewPolyline.encodedPath)
        println(decodedPath)
    }
}
package com.github.kuya32.geocachingandroidcodingexercise

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.github.kuya32.geocachingandroidcodingexercise.geocachingmap.MapViewScreen
import com.github.kuya32.geocachingandroidcodingexercise.geocachingmap.MapViewViewModel
import com.github.kuya32.geocachingandroidcodingexercise.geocachingpermissions.LocationPermissionScreen
import com.github.kuya32.geocachingandroidcodingexercise.ui.theme.GeocachingAndroidCodingExerciseTheme
import com.github.kuya32.geocachingandroidcodingexercise.geocachingsplash.SplashScreen
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import dagger.hilt.android.AndroidEntryPoint

@ExperimentalPermissionsApi
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GeocachingAndroidCodingExerciseTheme {
                val navController = rememberNavController()
                val viewModel = MapViewViewModel()

                // Helps navigate through the multiple composable within the application
                NavHost(
                    navController = navController,
                    startDestination = "geocachingSplashScreen"
                ) {
                    composable("geocachingSplashScreen") {
                        SplashScreen(navController = navController)
                    }
                    composable("geocachingPermissionScreen") {
                        LocationPermissionScreen(navController = navController)
                    }
                    composable("geocachingMapViewScreen") {
                        MapViewScreen(viewModel, this@MainActivity)
                    }
                }
            }
        }
    }
}

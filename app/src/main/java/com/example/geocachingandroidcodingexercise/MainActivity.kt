package com.example.geocachingandroidcodingexercise

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.geocachingandroidcodingexercise.ui.theme.GeocachingAndroidCodingExerciseTheme
import com.example.geocachingandroidcodingexercise.geocachingSplash.SplashScreen
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
                NavHost(
                    navController = navController,
                    startDestination = "geocachingSplashScreen"
                ) {
                    composable("geocachingSplashScreen") {
                        SplashScreen(navController = navController)
                    }
                    composable("geocachingMapView") {

                    }
                }
            }
        }
    }
}

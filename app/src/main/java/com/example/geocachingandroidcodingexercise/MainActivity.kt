package com.example.geocachingandroidcodingexercise

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.geocachingandroidcodingexercise.ui.theme.GeocachingAndroidCodingExerciseTheme
import com.example.geocachingandroidcodingexercise.ui.theme.geocachingSplash.GeocachingSplashScreen
import dagger.hilt.android.AndroidEntryPoint

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
                        GeocachingSplashScreen(navController = navController)
                    }
                    composable("geocachingMapView") {

                    }
                }
            }
        }
    }
}

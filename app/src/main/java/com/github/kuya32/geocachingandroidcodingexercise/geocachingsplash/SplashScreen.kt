package com.github.kuya32.geocachingandroidcodingexercise.geocachingsplash

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.github.kuya32.geocachingandroidcodingexercise.R
import com.google.accompanist.permissions.ExperimentalPermissionsApi

// Application splash screen with Geocaching logo
@ExperimentalPermissionsApi
@Composable
fun SplashScreen(
    navController: NavController,
    viewModel: SplashViewModel = hiltViewModel()
) {
    Surface(
        color = Color.White,
        modifier = Modifier.fillMaxSize()
    ) {
        Column {
            Image(
                painter = painterResource(id = R.drawable.ic_geocaching_logo),
                contentDescription = "Geocaching Green Logo",
                modifier = Modifier
                    .fillMaxSize()
                    .align(CenterHorizontally)
            )
        }
        LaunchedEffect(key1 = Unit, block = {
            try {
                // Delays three seconds to simulate a fake splash screen and then navigates to the permission screen
                viewModel.startTimer(3000L) {
                    navController.navigate("geocachingPermissionScreen")
                }
            } catch (ex: Exception) {
                Log.d("Timer", "Timer and navigation failed")
            }
        })
    }
}
package com.example.geocachingandroidcodingexercise.ui.theme.geocachingSplash

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.geocachingandroidcodingexercise.R

@Composable
fun GeocachingSplashScreen(
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
    }
}
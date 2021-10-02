package com.example.geocachingandroidcodingexercise.geocachingMap

import android.Manifest
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState

@ExperimentalPermissionsApi
@Composable
fun RequiredLocationPermissionScreen() {
    val multiplePermissionState = rememberMultiplePermissionsState(
        permissions = listOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    )
}

@Composable
fun PermissionRationale() {
    Text(text = "It looks like you have turned off permission required for this feature. " +
            "It can be enabled under Application Settings.")
}

@Composable
fun PermissionGrantedButton() {
    Button(onClick = { /*TODO*/ }) {
        Text(text = "Grant permissions")
    }
}

@Composable
fun PermissionDeniedButton(
    navController: NavController,
    context: Context = LocalContext.current
) {
    Button(
        onClick = {
            context.startActivity(
                Intent(
                    Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                    Uri.fromParts("package", "com.example.geocachingandroidcodingexercise", null)
                )
            )
        }
    ) {
        Text(text = )
    }
}
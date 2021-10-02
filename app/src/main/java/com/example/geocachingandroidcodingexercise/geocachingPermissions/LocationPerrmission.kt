package com.example.geocachingandroidcodingexercise.geocachingPermissions

import android.Manifest
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState

@ExperimentalPermissionsApi
@Composable
fun RequiredLocationPermissionScreen(
    navController: NavController
) {
    val multiplePermissionState = rememberMultiplePermissionsState(
        permissions = listOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    )
    Surface {
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.padding(16.dp)
        ) {
            when {
                multiplePermissionState.allPermissionsGranted -> {
                    navController.navigate("geocachingMapView")
                }
                else -> {
                    if (multiplePermissionState.shouldShowRationale) {
                        PermissionsDialog(
                            title = "Permissions Alert!",
                            description = "It looks like you have turned off permission required for this feature. " +
                                    "It can be enabled under Application Settings.",
                            modifier = Modifier
                                .fillMaxWidth()
                                .align(Alignment.CenterHorizontally)
                        )
                    }

                    Button(onClick = { multiplePermissionState.launchMultiplePermissionRequest() }) {
                        Text(text = "Click me!")
                    }
                }
            }
        }
    }
}

@Composable
fun PermissionsDialog(
    title: String,
    description: String,
    modifier: Modifier
) {
    val openDialog =  remember {
        mutableStateOf(true)
    }
    if (openDialog.value) {
        AlertDialog(
            onDismissRequest = {
                openDialog.value = false
            },
            title = {
                Text(
                    text = title,
                    color = Color.Red,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    modifier = modifier
                )
            },
            text = {
                Text(
                    text = description,
                    textAlign = TextAlign.Center,
                    modifier = modifier
                )
            },
            confirmButton = {
            },
            dismissButton = {
                PermissionsGrantedButton()
            }
        )
    }
}

@Composable
fun PermissionsGrantedButton() {
    
}

@Composable
fun PermissionsDeniedButton(
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

    }
}
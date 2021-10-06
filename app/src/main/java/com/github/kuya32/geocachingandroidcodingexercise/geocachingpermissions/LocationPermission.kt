package com.github.kuya32.geocachingandroidcodingexercise.geocachingpermissions

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
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState

/* Asks the user for permission to use their location. Granted permission sends the user to the map
 view while denied permission will prompt the user the need for location permission for the application
  to work. Used the Accompanist permissions API which is still within the experimental phase.
  Link: https://google.github.io/accompanist/permissions/ */
@ExperimentalPermissionsApi
@Composable
fun LocationPermissionScreen(
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
            val openDialog = remember {
                mutableStateOf(true)
            }
            when {
                // When the location permission for the application is granted, the user is navigated to the map view screen.
                multiplePermissionState.allPermissionsGranted -> {
                    navController.navigate("geocachingMapViewScreen")
                }
                // If the location permission has not been granted/requested, the application will prompt the user about permission with a dialog.
                !multiplePermissionState.permissionRequested -> {
                    if (openDialog.value) {
                        AlertDialog(
                            onDismissRequest = {
                                openDialog.value = false
                            },
                            title = {
                                Text(
                                    text = "Location Permission Required!",
                                    color = Color.Red,
                                    textAlign = TextAlign.Center,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .align(Alignment.CenterHorizontally)
                                )
                            },
                            text = {
                                Text(
                                    text = "User location is important for this app. Please grant the permission.",
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .align(Alignment.CenterHorizontally)
                                )
                            },
                            confirmButton = {
                                Button(
                                    onClick = {
                                        openDialog.value = false
                                        multiplePermissionState.launchMultiplePermissionRequest()
                                    }
                                ) {
                                    Text(text = "Yup!")
                                }
                            }
                        )
                    }
                }
                /* If the location permission has been denied, the user will be directed to the application settings and
                 grant permissions to continue using the app. */
                !multiplePermissionState.allPermissionsGranted -> {
                    AlertDialog(
                        onDismissRequest = {
                            openDialog.value = false
                        },
                        title = {
                            Text(
                                text = "Location Permission Required!",
                                color = Color.Red,
                                textAlign = TextAlign.Center,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .align(Alignment.CenterHorizontally)
                            )
                        },
                        text = {
                            Text(
                                text = "Location permission denied. Please, grant us access in application settings to continue.",
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .align(Alignment.CenterHorizontally)
                            )
                        },
                        confirmButton = {
                            OpenSettingsButton()
                        }
                    )
                }
            }
        }
    }
}

// Let's the user change their location permissions in the application settings
@Composable
private fun OpenSettingsButton(
    context: Context = LocalContext.current
) {
    Button(
        onClick = {
            context.startActivity(
                Intent(
                    Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                    Uri.fromParts("package", "com.github.example.geocachingandroidcodingexercise", null)
                )
            )
        }
    ) {
        Text(text = "Open Settings")
    }
}
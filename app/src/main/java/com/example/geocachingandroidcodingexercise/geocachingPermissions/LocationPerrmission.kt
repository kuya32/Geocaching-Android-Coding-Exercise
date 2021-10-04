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
import androidx.compose.runtime.saveable.rememberSaveable
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
            val openDialog =  remember {
                mutableStateOf(true)
            }
            when {
                multiplePermissionState.allPermissionsGranted -> {
                    navController.navigate("geocachingMapViewScreen")
                }
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

@Composable
fun OpenSettingsButton(
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
        Text(text = "Open Settings")
    }
}
package com.example.geocachingandroidcodingexercise.geocachingMap


import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.CenterFocusStrong
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController


@Composable
fun MapViewScreen(
    navController: NavController,
    viewModel: MapViewViewModel = hiltViewModel()
) {
    Scaffold(
        topBar = { MapViewAppBar() },
        floatingActionButtonPosition = FabPosition.End,
        floatingActionButton = { FloatingActionButton(
            onClick = { /*TODO*/ },
        ) {
            Image(
                imageVector = Icons.Filled.Add,
                contentDescription = "Add Icon",
                colorFilter = ColorFilter.tint(Color.White)
            )
        }
        }
    ) {
        Column() {
            
        }
    }
}

@Composable
fun MapViewAppBar() {
    TopAppBar(
        elevation = 4.dp,
        title = {
            Text(text = "Geocaching")
        },
        backgroundColor = MaterialTheme.colors.primary,
        actions = {
            IconButton(onClick = { /*TODO*/ }) {
                Icon(imageVector = Icons.Filled.Navigation, contentDescription = "Navigate Icon")
            }
            IconButton(onClick = { /*TODO*/ }) {
                Icon(imageVector = Icons.Filled.CenterFocusWeak, contentDescription = "Center Map Icon")
            }
            IconButton(onClick = { /*TODO*/ }) {
                Icon(imageVector = Icons.Filled.Calculate, contentDescription = "Calculate Distance Icon")
            }
        }
    )
}



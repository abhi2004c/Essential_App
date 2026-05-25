package com.example.subcountdown

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import com.example.subcountdown.calculator.CalculatorScreen
import com.example.subcountdown.checklist.ChecklistScreen
import com.example.subcountdown.clock.ClockScreen
import com.example.subcountdown.clock.ClockViewModel
import com.example.subcountdown.converter.CurrencyScreen
import com.example.subcountdown.core.ui.FeaturePlaceholder
import com.example.subcountdown.dashboard.DashboardScreen
import com.example.subcountdown.notes.NotesScreen
import com.example.subcountdown.stopwatch.StopwatchScreen
import com.example.subcountdown.subscriptions.SubscriptionScreen
import com.example.subcountdown.subscriptions.SubViewModel
import com.example.subcountdown.timer.TimerScreen
import com.example.subcountdown.timer.TimerViewModel
import com.example.subcountdown.ui.theme.SubscriptionCountDownTheme
import com.example.subcountdown.weather.WeatherDetailScreen
import com.example.subcountdown.weather.WeatherViewModel
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SubscriptionCountDownTheme {
                MainAppHost()
            }
        }
    }
}

@Composable
fun MainAppHost() {
    val navController = rememberNavController()
    val weatherViewModel: WeatherViewModel = viewModel()
    val subViewModel: SubViewModel = viewModel()
    val clockViewModel: ClockViewModel = viewModel()
    val timerViewModel: TimerViewModel = viewModel()
    
    val context = LocalContext.current
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val isGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true || 
                       permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true
        if (isGranted) {
            fusedLocationClient.getCurrentLocation(Priority.PRIORITY_BALANCED_POWER_ACCURACY, null)
                .addOnSuccessListener { loc ->
                    loc?.let { weatherViewModel.fetchByLoc(it.latitude, it.longitude) }
                }
        } else {
            weatherViewModel.fetchWeather("Madrid")
        }
    }

    LaunchedEffect(Unit) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.getCurrentLocation(Priority.PRIORITY_BALANCED_POWER_ACCURACY, null)
                .addOnSuccessListener { loc ->
                    if (loc != null) {
                        weatherViewModel.fetchByLoc(loc.latitude, loc.longitude)
                    } else {
                        weatherViewModel.fetchWeather("Madrid")
                    }
                }
        } else {
            locationPermissionLauncher.launch(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
            )
        }
    }

    Scaffold(
        bottomBar = { BottomNavigationBar(navController) },
        containerColor = Color(0xFF0A0A0A)
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "dashboard",
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xFF000000), Color(0xFF0A0A0A))
                    )
                )
        ) {
            composable("dashboard") {
                DashboardScreen(navController, weatherViewModel, subViewModel, timerViewModel)
            }
            composable("weather") {
                WeatherDetailScreen(weatherViewModel)
            }
            composable("subscriptions") {
                SubscriptionScreen(subViewModel)
            }
            composable("calculator") {
                CalculatorScreen()
            }
            composable("clock") {
                ClockScreen(clockViewModel)
            }
            composable("timer") {
                TimerScreen(timerViewModel)
            }
            composable("stopwatch") {
                StopwatchScreen()
            }
            composable("converter") {
                CurrencyScreen()
            }
            composable("checklist") {
                ChecklistScreen()
            }
            composable("notes") {
                NotesScreen()
            }
            composable("tools") {
                FeaturePlaceholder("All Utilities Hub")
            }
        }
    }
}

@Composable
fun BottomNavigationBar(navController: NavHostController) {
    val items = listOf(
        NavigationItem("Home", "dashboard", Icons.Default.Dashboard),
        NavigationItem("Weather", "weather", Icons.Default.Cloud),
        NavigationItem("Subs", "subscriptions", Icons.AutoMirrored.Filled.ReceiptLong),
        NavigationItem("Tools", "tools", Icons.Default.GridView)
    )
    NavigationBar(
        containerColor = Color.Black,
        tonalElevation = 0.dp
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route
        items.forEach { item ->
            NavigationBarItem(
                icon = { Icon(item.icon, contentDescription = item.title) },
                label = { Text(item.title, fontSize = 10.sp) },
                selected = currentRoute == item.route,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.startDestinationId)
                        launchSingleTop = true
                    }
                }
            )
        }
    }
}

data class NavigationItem(val title: String, val route: String, val icon: ImageVector)

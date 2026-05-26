package com.example.subcountdown.weather

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.subcountdown.core.models.ForecastItem
import com.example.subcountdown.core.models.WeatherResponse
import com.example.subcountdown.core.ui.GlassCard
import com.example.subcountdown.core.ui.PremiumTextField
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.cos
import kotlin.math.sin

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherDetailScreen(viewModel: WeatherViewModel) {
    var citySearch by remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current
    val scrollState = rememberScrollState()
    val weather = viewModel.weatherData

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF000000))
            .padding(horizontal = 16.dp)
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        
        PremiumTextField(
            value = citySearch,
            onValueChange = { citySearch = it },
            label = "Search city",
            trailingIcon = {
                IconButton(onClick = {
                    viewModel.fetchWeather(citySearch)
                    focusManager.clearFocus()
                }) {
                    Icon(Icons.Default.Search, contentDescription = null, tint = Color.White)
                }
            },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = {
                viewModel.fetchWeather(citySearch)
                focusManager.clearFocus()
            })
        )

        Spacer(modifier = Modifier.height(40.dp))

        Text(
            text = weather?.name ?: "Loading...",
            color = Color.White,
            fontSize = 28.sp,
            fontWeight = FontWeight.Medium
        )
        
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "${weather?.main?.temp?.toInt() ?: "--"}°",
                fontSize = 110.sp,
                color = Color.White,
                fontWeight = FontWeight.Light
            )
            AsyncImage(
                model = "https://openweathermap.org/img/wn/${weather?.weather?.firstOrNull()?.icon ?: "01d"}@4x.png",
                contentDescription = null,
                modifier = Modifier.size(120.dp)
            )
        }
        
        Text(
            text = weather?.weather?.firstOrNull()?.description?.replaceFirstChar { it.uppercase() } ?: "---",
            color = Color.White.copy(alpha = 0.8f),
            fontSize = 20.sp
        )
        
        Text(
            text = "H:${weather?.main?.temp_max?.toInt() ?: "--"}°  L:${weather?.main?.temp_min?.toInt() ?: "--"}°",
            color = Color.White.copy(alpha = 0.6f),
            fontSize = 16.sp
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Sunrise & Sunset Animation Card
        if (weather != null) {
            SunriseSunsetCard(weather)
        }

        Spacer(modifier = Modifier.height(24.dp))

        SamsungCard(title = "Hourly Forecast") {
            LazyRow(
                modifier = Modifier.padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                val hourlyData = viewModel.forecastData?.list?.take(8) ?: emptyList()
                items(hourlyData) { item ->
                    HourlyItem(item)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        SamsungCard(title = "Daily Forecast") {
            val dailyData = viewModel.forecastData?.list?.filter { it.dtTxt.contains("12:00:00") } ?: emptyList()
            Column(modifier = Modifier.padding(vertical = 8.dp)) {
                dailyData.forEach { item ->
                    DailyRow(item)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(modifier = Modifier.fillMaxWidth()) {
            SamsungDetailCard(Modifier.weight(1f), "Feels Like", "${weather?.main?.feelsLike?.toInt()}°", Icons.Default.Thermostat)
            Spacer(modifier = Modifier.width(12.dp))
            SamsungDetailCard(Modifier.weight(1f), "Humidity", "${weather?.main?.humidity}%", Icons.Default.WaterDrop)
        }
        Spacer(modifier = Modifier.height(12.dp))
        Row(modifier = Modifier.fillMaxWidth()) {
            SamsungDetailCard(Modifier.weight(1f), "Wind", "${weather?.wind?.speed} m/s", Icons.Default.Air)
            Spacer(modifier = Modifier.width(12.dp))
            SamsungDetailCard(Modifier.weight(1f), "Visibility", "${(weather?.visibility ?: 0) / 1000} km", Icons.Default.Visibility)
        }
        
        Spacer(modifier = Modifier.height(100.dp))
    }
}

@Composable
fun SunriseSunsetCard(weather: WeatherResponse) {
    Card(
        modifier = Modifier.fillMaxWidth().height(180.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A1A))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Sunrise & Sunset", color = Color.Gray, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))
            
            Box(modifier = Modifier.fillMaxSize()) {
                val sunrise = weather.sys?.sunrise ?: 0L
                val sunset = weather.sys?.sunset ?: 0L
                val current = System.currentTimeMillis() / 1000
                
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val width = size.width
                    val height = size.height - 20.dp.toPx()
                    
                    // Path for the arc
                    val path = Path().apply {
                        moveTo(0f, height)
                        quadraticTo(width / 2, -height / 2, width, height)
                    }
                    
                    drawPath(
                        path = path,
                        color = Color.White.copy(alpha = 0.1f),
                        style = Stroke(width = 2.dp.toPx(), pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f)))
                    )

                    // Draw Sun position
                    if (current in sunrise..sunset) {
                        val progress = (current - sunrise).toFloat() / (sunset - sunrise).toFloat()
                        // Simplified arc mapping
                        val angle = Math.PI + (progress * Math.PI)
                        val x = (width / 2) + (width / 2) * cos(angle).toFloat()
                        val y = height + (height) * sin(angle).toFloat()
                        
                        drawCircle(
                            color = Color(0xFFFFD600),
                            radius = 8.dp.toPx(),
                            center = androidx.compose.ui.geometry.Offset(x, y)
                        )
                    }
                    
                    // Horizon line
                    drawLine(
                        color = Color.Gray.copy(alpha = 0.3f),
                        start = androidx.compose.ui.geometry.Offset(0f, height),
                        end = androidx.compose.ui.geometry.Offset(width, height),
                        strokeWidth = 1.dp.toPx()
                    )
                }
                
                Row(
                    modifier = Modifier.fillMaxWidth().align(Alignment.BottomCenter),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(horizontalAlignment = Alignment.Start) {
                        Text(formatTime(sunrise), color = Color.White, fontWeight = FontWeight.Bold)
                        Text("Sunrise", color = Color.Gray, fontSize = 12.sp)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(formatTime(sunset), color = Color.White, fontWeight = FontWeight.Bold)
                        Text("Sunset", color = Color.Gray, fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun SamsungCard(title: String, content: @Composable () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A1A))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = title, color = Color.Gray, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            content()
        }
    }
}

@Composable
fun SamsungDetailCard(modifier: Modifier, label: String, value: String, icon: ImageVector) {
    Card(
        modifier = modifier.height(110.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A1A))
    ) {
        Column(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.SpaceBetween) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = label, color = Color.Gray, fontSize = 12.sp)
            }
            Text(text = value, color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun HourlyItem(item: ForecastItem) {
    val time = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(item.dt * 1000))
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = time, color = Color.Gray, fontSize = 12.sp)
        AsyncImage(
            model = "https://openweathermap.org/img/wn/${item.weather.firstOrNull()?.icon ?: "01d"}@2x.png",
            contentDescription = null,
            modifier = Modifier.size(40.dp)
        )
        Text(text = "${item.main.temp.toInt()}°", color = Color.White, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun DailyRow(item: ForecastItem) {
    val date = SimpleDateFormat("EEE", Locale.getDefault()).format(Date(item.dt * 1000))
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = date, color = Color.White, modifier = Modifier.width(50.dp))
        AsyncImage(
            model = "https://openweathermap.org/img/wn/${item.weather.firstOrNull()?.icon ?: "01d"}@2x.png",
            contentDescription = null,
            modifier = Modifier.size(30.dp)
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = "${item.main.temp_max?.toInt()}°", color = Color.White, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "${item.main.temp_min?.toInt()}°", color = Color.Gray)
        }
    }
}

fun formatTime(timestamp: Long): String {
    val date = Date(timestamp * 1000)
    return SimpleDateFormat("HH:mm", Locale.getDefault()).format(date)
}

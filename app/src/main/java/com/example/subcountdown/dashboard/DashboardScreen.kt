package com.example.subcountdown.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Notes
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.subcountdown.core.ui.GlassCard
import com.example.subcountdown.subscriptions.SubViewModel
import com.example.subcountdown.timer.TimerViewModel
import com.example.subcountdown.timer.formatTimer
import com.example.subcountdown.weather.WeatherViewModel

@Composable
fun DashboardScreen(
    navController: NavHostController,
    weatherVM: WeatherViewModel,
    subVM: SubViewModel,
    timerVM: TimerViewModel
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        item {
            Column {
                Text(
                    text = "Essential",
                    color = Color.White,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Utilities",
                    color = Color(0xFF3F51B5),
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Active Timer Widget (Shows only if running)
        if (timerVM.isRunning) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth().clickable { navController.navigate("timer") },
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE91E63)),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Timer, contentDescription = null, tint = Color.White)
                            Spacer(Modifier.width(12.dp))
                            Text("Active Timer", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                        Text(
                            text = formatTimer(timerVM.timeSeconds),
                            color = Color.White,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }
            }
        }
        
        item {
            GlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .clickable { navController.navigate("weather") }
            ) {
                Row(
                    modifier = Modifier.padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = weatherVM.weatherData?.name ?: "Fetching Location...",
                            color = Color.White,
                            fontSize = 18.sp
                        )
                        Text(
                            text = "${weatherVM.weatherData?.main?.temp?.toInt() ?: "--"}°C",
                            color = Color.White,
                            fontSize = 48.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = weatherVM.weatherData?.weather?.firstOrNull()?.description?.uppercase()
                                ?: "---",
                            color = Color.Gray,
                            fontSize = 12.sp
                        )
                    }
                    AsyncImage(
                        model = "https://openweathermap.org/img/wn/${
                            weatherVM.weatherData?.weather?.firstOrNull()?.icon ?: "01d"
                        }@4x.png",
                        contentDescription = null,
                        modifier = Modifier.size(90.dp)
                    )
                }
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Subscriptions",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Box(
                        modifier = Modifier
                            .background(Color(0xFF3F51B5), CircleShape)
                            .size(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = subVM.subscriptions.size.toString(),
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Text(
                    text = "View All",
                    color = Color(0xFF3F51B5),
                    modifier = Modifier.clickable { navController.navigate("subscriptions") }
                )
            }
        }

        item {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                items(subVM.subscriptions) { sub ->
                    GlassCard(
                        modifier = Modifier
                            .width(150.dp)
                            .height(110.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                                Text(sub.name, color = Color.White, fontWeight = FontWeight.Bold, maxLines = 1)
                                Box(modifier = Modifier.size(8.dp).background(sub.color, CircleShape))
                            }
                            Text(
                                text = "$${sub.price}",
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                            Text(
                                text = "${sub.daysLeft} days left",
                                color = Color.Gray,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }
        }

        item {
            Text(
                text = "Daily Tools",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }

        item {
            val tools = listOf(
                ToolItem("Clock", Icons.Default.Schedule, Color(0xFF673AB7), "clock"),
                ToolItem("Timer", Icons.Default.Timer, Color(0xFFE91E63), "timer"),
                ToolItem("Notes", Icons.AutoMirrored.Filled.Notes, Color(0xFFFF9800), "notes"),
                ToolItem("Calculator", Icons.Default.Calculate, Color(0xFF4CAF50), "calculator")
            )
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.height(240.dp)
            ) {
                items(tools) { tool -> 
                    ToolCard(tool) {
                        navController.navigate(tool.route)
                    }
                }
            }
        }
    }
}

@Composable
fun ToolCard(tool: ToolItem, onClick: () -> Unit) {
    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .height(110.dp)
            .clickable { onClick() }
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = tool.icon,
                contentDescription = null,
                tint = tool.color,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = tool.name,
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

data class ToolItem(val name: String, val icon: ImageVector, val color: Color, val route: String)

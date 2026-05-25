package com.example.subcountdown.clock

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.subcountdown.core.ui.GlassCard
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ClockScreen(viewModel: ClockViewModel) {
    var showAddDialog by remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = Color(0xFF3F51B5),
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add City")
            }
        },
        containerColor = Color.Black
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            Text("World Clock", color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(24.dp))
            
            RealGlobeCard()
            
            Spacer(modifier = Modifier.height(24.dp))

            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(viewModel.clocks) { clock ->
                    WorldClockItem(clock) { viewModel.removeCity(clock) }
                }
            }
        }

        if (showAddDialog) {
            AddCityDialog(
                onDismiss = { showAddDialog = false },
                onAdd = { name -> 
                    viewModel.addCityByName(name)
                    showAddDialog = false
                }
            )
        }
    }
}

@Composable
fun RealGlobeCard() {
    val infiniteTransition = rememberInfiniteTransition()
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(20000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    Card(
        modifier = Modifier.fillMaxWidth().height(220.dp),
        shape = RoundedCornerShape(32.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F0F0F))
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
            // Atmospheric Glow
            Box(
                modifier = Modifier
                    .size(160.dp)
                    .background(Brush.radialGradient(listOf(Color(0xFF2196F3).copy(alpha = 0.2f), Color.Transparent)), CircleShape)
            )

            Canvas(modifier = Modifier.size(140.dp)) {
                // Ocean with deep gradient
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0xFF1E88E5), Color(0xFF0D47A1)),
                        center = center,
                        radius = size.width / 2
                    )
                )
                
                // Detailed Landmass simulation
                withTransform({
                    rotate(rotation)
                }) {
                    // Asia/Europe block
                    drawPath(
                        path = androidx.compose.ui.graphics.Path().apply {
                            addOval(androidx.compose.ui.geometry.Rect(center.x - 50.dp.toPx(), center.y - 30.dp.toPx(), center.x + 20.dp.toPx(), center.y + 10.dp.toPx()))
                            addOval(androidx.compose.ui.geometry.Rect(center.x - 10.dp.toPx(), center.y - 10.dp.toPx(), center.x + 50.dp.toPx(), center.y + 40.dp.toPx()))
                        },
                        color = Color(0xFF4CAF50).copy(alpha = 0.8f)
                    )
                }

                // Shading for 3D look (Ambient Occlusion)
                drawCircle(
                    brush = Brush.radialGradient(
                        0.7f to Color.Transparent,
                        1.0f to Color.Black.copy(alpha = 0.4f),
                        center = center,
                        radius = size.width / 2
                    )
                )

                // Night shadow
                drawArc(
                    color = Color.Black.copy(alpha = 0.7f),
                    startAngle = 90f,
                    sweepAngle = 180f,
                    useCenter = true
                )

                // Surface Reflection / Atmosphere Rim
                drawCircle(
                    color = Color.White.copy(alpha = 0.2f),
                    style = Stroke(width = 2.dp.toPx())
                )
            }
        }
    }
}

@Composable
fun WorldClockItem(clock: WorldClock, onDelete: () -> Unit) {
    val formatter = SimpleDateFormat("HH:mm", Locale.getDefault()).apply {
        timeZone = TimeZone.getTimeZone(clock.timezoneId)
    }
    var time by remember { mutableStateOf(formatter.format(Date())) }
    
    LaunchedEffect(Unit) {
        while(true) {
            time = formatter.format(Date())
            kotlinx.coroutines.delay(1000)
        }
    }

    val hour = try { time.substringBefore(":").toInt() } catch (e: Exception) { 12 }
    val isNight = hour !in 6..18

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A1A))
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(clock.cityName, color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Text(clock.timezoneId.substringAfter("/").replace("_", " "), color = Color.Gray, fontSize = 13.sp)
            }
            
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = if (isNight) Icons.Default.NightsStay else Icons.Default.WbSunny,
                    contentDescription = null,
                    tint = if (isNight) Color(0xFF9FA8DA) else Color(0xFFFFD600),
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(time, color = Color.White, fontSize = 32.sp, fontWeight = FontWeight.Light)
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red.copy(alpha = 0.3f))
                }
            }
        }
    }
}

@Composable
fun AddCityDialog(onDismiss: () -> Unit, onAdd: (String, String) -> Unit) {
    var name by remember { mutableStateOf("") }
    var tz by remember { mutableStateOf("UTC") }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add City") },
        text = {
            Column {
                TextField(
                    value = name, 
                    onValueChange = { name = it }, 
                    label = { Text("City (e.g. Dubai)") },
                    colors = TextFieldDefaults.colors(focusedContainerColor = Color.Transparent, unfocusedContainerColor = Color.Transparent)
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = tz, 
                    onValueChange = { tz = it }, 
                    label = { Text("Timezone (e.g. Asia/Dubai)") },
                    colors = TextFieldDefaults.colors(focusedContainerColor = Color.Transparent, unfocusedContainerColor = Color.Transparent)
                )
            }
        },
        confirmButton = {
            Button(onClick = { if (name.isNotBlank()) onAdd(name, tz) }) { Text("Add") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

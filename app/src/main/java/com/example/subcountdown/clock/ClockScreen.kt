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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.subcountdown.core.ui.GlassCard
import com.example.subcountdown.core.ui.PremiumTextField
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

            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(viewModel.clocks) { clock ->
                    WorldClockItem(clock) { viewModel.removeCity(clock) }
                }
            }
        }

        if (showAddDialog) {
            TimeZoneSearchDialog(
                availableTimeZones = viewModel.allAvailableTimeZones,
                onDismiss = { showAddDialog = false },
                onSelect = { tzId -> 
                    viewModel.addClock(tzId)
                    showAddDialog = false
                }
            )
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
                Text(clock.timezoneId.replace("_", " "), color = Color.Gray, fontSize = 13.sp)
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
fun TimeZoneSearchDialog(
    availableTimeZones: List<String>,
    onDismiss: () -> Unit,
    onSelect: (String) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    val filteredTimeZones = remember(searchQuery) {
        availableTimeZones.filter { it.contains(searchQuery, ignoreCase = true) }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Color.Black
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                    }
                    Text(
                        "Choose City/Country",
                        color = Color.White,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                PremiumTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    label = "Search city or country...",
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray) }
                )

                Spacer(modifier = Modifier.height(16.dp))

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filteredTimeZones) { tzId ->
                        val parts = tzId.split("/")
                        val region = parts.first()
                        val city = parts.last().replace("_", " ")

                        Card(
                            onClick = { onSelect(tzId) },
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A1A)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(city, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Medium)
                                    Text(region, color = Color.Gray, fontSize = 14.sp)
                                }
                                Icon(Icons.Default.Add, contentDescription = null, tint = Color(0xFF3F51B5))
                            }
                        }
                    }
                }
            }
        }
    }
}

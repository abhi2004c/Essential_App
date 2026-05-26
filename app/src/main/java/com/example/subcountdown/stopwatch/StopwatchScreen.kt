package com.example.subcountdown.stopwatch

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun StopwatchScreen(viewModel: StopwatchViewModel = viewModel()) {
    Column(
        modifier = Modifier.fillMaxSize().background(Color.Black).padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text("Stopwatch", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(vertical = 16.dp))
        Spacer(modifier = Modifier.height(40.dp))
        Text(text = formatMillis(viewModel.timeMillis), fontSize = 80.sp, color = Color.White, fontWeight = FontWeight.Light, modifier = Modifier.padding(vertical = 32.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly, verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { viewModel.reset() }) {
                Icon(Icons.Default.Refresh, contentDescription = "Reset", tint = Color.Gray, modifier = Modifier.size(32.dp))
            }
            LargeFloatingActionButton(
                onClick = { if (viewModel.isRunning) viewModel.pause() else viewModel.start() },
                containerColor = if (viewModel.isRunning) Color(0xFFE57373) else Color(0xFF3F51B5),
                shape = CircleShape
            ) {
                Icon(imageVector = if (viewModel.isRunning) Icons.Default.Pause else Icons.Default.PlayArrow, contentDescription = null, tint = Color.White, modifier = Modifier.size(36.dp))
            }
            IconButton(onClick = { viewModel.recordLap() }, enabled = viewModel.isRunning) {
                Icon(Icons.Default.History, contentDescription = "Lap", tint = if(viewModel.isRunning) Color.White else Color.DarkGray, modifier = Modifier.size(32.dp))
            }
        }

        Spacer(modifier = Modifier.height(40.dp))

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            val lapList = viewModel.laps.toList()
            items(lapList.size) { index ->
                val lapTime = lapList[index]
                val lapNumber = lapList.size - index
                Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 8.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Lap $lapNumber", color = Color.Gray, fontSize = 16.sp)
                    Text(formatMillis(lapTime), color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Medium)
                }
                HorizontalDivider(color = Color.White.copy(alpha = 0.1f))
            }
        }
    }
}

fun formatMillis(millis: Long): String {
    val hundredths = (millis % 1000) / 10
    val seconds = (millis / 1000) % 60
    val minutes = (millis / (1000 * 60)) % 60
    val hours = millis / (1000 * 60 * 60)
    return if (hours > 0) "%02d:%02d:%02d.%02d".format(hours, minutes, seconds, hundredths)
    else "%02d:%02d.%02d".format(minutes, seconds, hundredths)
}

package com.example.subcountdown.timer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Stop
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
fun TimerScreen(viewModel: TimerViewModel = viewModel()) {
    Column(
        modifier = Modifier.fillMaxSize().background(Color.Black),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = formatTimer(viewModel.timeSeconds),
            fontSize = 100.sp,
            fontWeight = FontWeight.Light,
            color = Color.White
        )
        
        Spacer(modifier = Modifier.height(60.dp))
        
        Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
            OutlinedButton(onClick = { viewModel.addMinutes(1) }) { Text("+1 Min") }
            OutlinedButton(onClick = { viewModel.addMinutes(5) }) { Text("+5 Min") }
        }
        
        Spacer(modifier = Modifier.height(40.dp))
        
        Row(horizontalArrangement = Arrangement.spacedBy(32.dp), verticalAlignment = Alignment.CenterVertically) {
            // Reset Button
            IconButton(onClick = { viewModel.resetTimer() }) {
                Icon(Icons.Default.Refresh, contentDescription = "Reset", tint = Color.Gray, modifier = Modifier.size(32.dp))
            }

            // Start / Stop FAB
            LargeFloatingActionButton(
                onClick = { 
                    if (viewModel.isRunning) viewModel.stopTimer() else viewModel.startTimer() 
                },
                containerColor = if (viewModel.isRunning) Color.Red.copy(alpha = 0.8f) else Color(0xFF3F51B5),
                shape = MaterialTheme.shapes.extraLarge
            ) {
                Icon(
                    imageVector = if (viewModel.isRunning) Icons.Default.Stop else Icons.Default.PlayArrow,
                    contentDescription = null,
                    modifier = Modifier.size(40.dp),
                    tint = Color.White
                )
            }
            
            // Dummy spacer to balance icons
            Spacer(modifier = Modifier.width(32.dp))
        }
    }
}

fun formatTimer(seconds: Long): String {
    val h = seconds / 3600
    val m = (seconds % 3600) / 60
    val s = seconds % 60
    return if (h > 0) "%02d:%02d:%02d".format(h, m, s) else "%02d:%02d".format(m, s)
}

package com.example.subcountdown.calculator

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.*

@Composable
fun CalculatorScreen() {
    var expr by remember { mutableStateOf("") }
    var res by remember { mutableStateOf("0") }

    Column(
        modifier = Modifier.fillMaxSize().background(Color.Black).padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().weight(1f),
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.End
        ) {
            Text(expr, fontSize = 28.sp, color = Color.Gray, textAlign = TextAlign.End)
            Text(res, fontSize = 64.sp, color = Color.White, fontWeight = FontWeight.Light, textAlign = TextAlign.End)
        }

        Spacer(modifier = Modifier.height(16.dp))

        val keys = listOf(
            "sin", "cos", "tan", "÷",
            "log", "ln", "√", "×",
            "7", "8", "9", "−",
            "4", "5", "6", "+",
            "1", "2", "3", "=",
            "C", "0", ".", "DEL"
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(4),
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(keys) { key ->
                val color = when {
                    key == "=" -> Color(0xFF3F51B5)
                    key in listOf("÷", "×", "−", "+") -> Color(0xFF1A1A1A)
                    key in listOf("C", "DEL") -> Color(0xFF2C1A1A)
                    key in listOf("sin", "cos", "tan", "log", "ln", "√") -> Color(0xFF212121)
                    else -> Color.White.copy(alpha = 0.08f)
                }
                val textColor = when {
                    key == "=" -> Color.White
                    key in listOf("÷", "×", "−", "+") -> Color(0xFF3F51B5)
                    key in listOf("C", "DEL") -> Color(0xFFE57373)
                    else -> Color.White
                }
                
                Surface(
                    onClick = {
                        when (key) {
                            "C" -> { expr = ""; res = "0" }
                            "DEL" -> if (expr.isNotEmpty()) expr = expr.dropLast(1)
                            "=" -> if (expr.isNotEmpty()) res = evaluateMath(expr)
                            "sin" -> expr += "sin("
                            "cos" -> expr += "cos("
                            "tan" -> expr += "tan("
                            "log" -> expr += "log("
                            "ln" -> expr += "ln("
                            "√" -> expr += "√("
                            else -> {
                                val sym = when(key) { "÷" -> "/"; "×" -> "*"; "−" -> "-"; else -> key }
                                if (expr == "0" && sym.first().isDigit()) expr = sym else expr += sym
                            }
                        }
                    },
                    shape = CircleShape,
                    color = color,
                    modifier = Modifier.aspectRatio(1.2f)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(key, color = textColor, fontSize = 20.sp, fontWeight = FontWeight.Normal)
                    }
                }
            }
        }
    }
}

fun evaluateMath(expr: String): String {
    return try {
        // This is a placeholder for a real math parser. 
        // For now, it returns a fixed result or handles simple cases if possible.
        if (expr.contains("sin")) "0.0" 
        else "Result"
    } catch (e: Exception) {
        "Error"
    }
}

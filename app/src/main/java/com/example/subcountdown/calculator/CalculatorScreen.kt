package com.example.subcountdown.calculator

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CalculatorScreen() {
    var expression by remember { mutableStateOf("") }
    var result by remember { mutableStateOf("0") }
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF000000))
            .padding(horizontal = 16.dp)
            .verticalScroll(scrollState), // Allow scrolling if height is tight on tablets
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Display Area
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 600.dp)
                .heightIn(min = 150.dp, max = 250.dp)
                .padding(vertical = 24.dp),
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.End
        ) {
            Text(
                text = expression.ifEmpty { " " },
                fontSize = 24.sp,
                color = Color.Gray,
                textAlign = TextAlign.End,
                modifier = Modifier.fillMaxWidth(),
                maxLines = 2
            )
            Text(
                text = result,
                fontSize = 52.sp,
                color = Color.White,
                fontWeight = FontWeight.Light,
                textAlign = TextAlign.End,
                modifier = Modifier.fillMaxWidth(),
                lineHeight = 58.sp
            )
        }

        // Keypad Container
        Column(
            modifier = Modifier
                .widthIn(max = 600.dp)
                .fillMaxWidth()
                .padding(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Scientific Functions Grid
            val sciButtons = listOf(
                "sin", "cos", "tan", "log",
                "ln", "(", ")", "^",
                "sqrt", "π", "e", "C"
            )
            
            // Using a Row with wrapping or small grid for scientific
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                maxItemsInEachRow = 4
            ) {
                sciButtons.forEach { btn ->
                    SciButton(btn, modifier = Modifier.weight(1f)) {
                        when (btn) {
                            "C" -> { expression = ""; result = "0" }
                            "sqrt" -> expression += "sqrt("
                            else -> expression += btn
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Main Keypad
            val keypad = listOf(
                listOf("7", "8", "9", "÷"),
                listOf("4", "5", "6", "×"),
                listOf("1", "2", "3", "−"),
                listOf("0", ".", "DEL", "+"),
                listOf("=")
            )

            keypad.forEach { row ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    row.forEach { btn ->
                        val isOperator = btn in listOf("÷", "×", "−", "+", "=")
                        val isDEL = btn == "DEL"
                        
                        CalcButton(
                            text = btn,
                            modifier = Modifier.weight(1f),
                            isPrimary = btn == "=",
                            isOperator = isOperator,
                            isDEL = isDEL
                        ) {
                            when (btn) {
                                "=" -> if (expression.isNotEmpty()) result = evaluateExpression(expression)
                                "DEL" -> if (expression.isNotEmpty()) expression = expression.dropLast(1)
                                else -> {
                                    val char = when(btn) {
                                        "÷" -> "/"
                                        "×" -> "*"
                                        "−" -> "-"
                                        else -> btn
                                    }
                                    expression += char
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SciButton(text: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        color = Color.White.copy(alpha = 0.08f),
        modifier = modifier.height(44.dp)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(text = text, color = Color(0xFF3F51B5), fontSize = 14.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun CalcButton(
    text: String, 
    modifier: Modifier, 
    isPrimary: Boolean, 
    isOperator: Boolean, 
    isDEL: Boolean, 
    onClick: () -> Unit
) {
    val containerColor = when {
        isPrimary -> Color(0xFF3F51B5)
        isDEL -> Color(0xFF2C1A1A)
        isOperator -> Color(0xFF1A1A1A)
        else -> Color.White.copy(alpha = 0.08f)
    }
    
    val textColor = when {
        isPrimary -> Color.White
        isDEL -> Color(0xFFE57373)
        isOperator -> Color(0xFF3F51B5)
        else -> Color.White
    }

    Surface(
        onClick = onClick,
        shape = CircleShape,
        color = containerColor,
        modifier = modifier.then(
            if (text == "=") Modifier.height(64.dp) else Modifier.aspectRatio(1f)
        ).widthIn(max = 100.dp) // Prevent buttons from becoming too wide on tablets
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(text = text, color = textColor, fontSize = 24.sp, fontWeight = FontWeight.Normal)
        }
    }
}

fun evaluateExpression(expr: String): String {
    return try {
        val sanitized = expr
            .replace("π", Math.PI.toString())
            .replace("e", Math.E.toString())
            .replace("sqrt", "sqrt")
        val value = ExprParser(sanitized).parse()
        if (value == value.toLong().toDouble()) value.toLong().toString()
        else "%.8g".format(value).trimEnd('0').trimEnd('.')
    } catch (e: Exception) {
        "Error"
    }
}

private class ExprParser(private val input: String) {
    private var pos = 0
    fun parse(): Double {
        val result = parseExpr()
        if (pos < input.length) throw IllegalArgumentException()
        return result
    }
    private fun parseExpr(): Double {
        var result = parseTerm()
        while (pos < input.length) {
            when (input[pos]) {
                '+' -> { pos++; result += parseTerm() }
                '-' -> { pos++; result -= parseTerm() }
                else -> break
            }
        }
        return result
    }
    private fun parseTerm(): Double {
        var result = parsePower()
        while (pos < input.length) {
            when (input[pos]) {
                '*' -> { pos++; result *= parsePower() }
                '/' -> { pos++; val d = parsePower(); result = if (d == 0.0) Double.NaN else result / d }
                else -> break
            }
        }
        return result
    }
    private fun parsePower(): Double {
        val base = parseUnary()
        return if (pos < input.length && input[pos] == '^') {
            pos++; Math.pow(base, parseUnary())
        } else base
    }
    private fun parseUnary(): Double {
        if (pos < input.length && input[pos] == '-') { pos++; return -parsePrimary() }
        return parsePrimary()
    }
    private fun parsePrimary(): Double {
        if (pos < input.length && input[pos] == '(') {
            pos++
            val result = parseExpr()
            if (pos < input.length && input[pos] == ')') pos++
            return result
        }
        val funcs = listOf("sqrt", "sin", "cos", "tan", "log", "ln")
        for (fn in funcs) {
            if (input.startsWith(fn, pos)) {
                pos += fn.length
                if (pos < input.length && input[pos] == '(') pos++
                val arg = parseExpr()
                if (pos < input.length && input[pos] == ')') pos++
                return when (fn) {
                    "sqrt" -> Math.sqrt(arg)
                    "sin"  -> Math.sin(Math.toRadians(arg))
                    "cos"  -> Math.cos(Math.toRadians(arg))
                    "tan"  -> Math.tan(Math.toRadians(arg))
                    "log"  -> Math.log10(arg)
                    "ln"   -> Math.log(arg)
                    else   -> 0.0
                }
            }
        }
        val start = pos
        while (pos < input.length && (input[pos].isDigit() || input[pos] == '.')) pos++
        return input.substring(start, pos).toDouble()
    }
}

package com.example.subcountdown.calculator

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF000000))
            .padding(24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.End
        ) {
            Text(
                text = expression,
                fontSize = 32.sp,
                color = Color.Gray,
                textAlign = TextAlign.End,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text = result,
                fontSize = 64.sp,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.End,
                lineHeight = 70.sp
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        val sciButtons = listOf(
            "sin", "cos", "tan", "log",
            "ln", "(", ")", "^",
            "sqrt", "pi", "e", "C"
        )

        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(4),
                modifier = Modifier.height(140.dp).weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(sciButtons) { btn ->
                    ScientificButton(btn) {
                        when (btn) {
                            "C" -> { expression = ""; result = "0" }
                            "pi" -> expression += "π"
                            "e" -> expression += "e"
                            "sqrt" -> expression += "√("
                            else -> expression += "$btn("
                        }
                    }
                }
            }
        }

        val keypad = listOf(
            listOf("7", "8", "9", "/"),
            listOf("4", "5", "6", "*"),
            listOf("1", "2", "3", "-"),
            listOf("0", ".", "DEL", "+"),
            listOf("=")
        )

        keypad.forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                row.forEach { btn ->
                    val isEquals = btn == "="
                    val isOperator = btn in listOf("/", "*", "-", "+", "DEL")

                    MainButton(
                        text = btn,
                        modifier = if (isEquals) Modifier.weight(4f) else Modifier.weight(1f),
                        isPrimary = isEquals,
                        isOperator = isOperator
                    ) {
                        when (btn) {
                            "=" -> result = evaluateExpression(expression)
                            "DEL" -> if (expression.isNotEmpty()) expression = expression.dropLast(1)
                            else -> expression += btn
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ScientificButton(text: String, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        color = Color(0xFF1A1A1A),
        modifier = Modifier.height(36.dp)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(text = text, color = Color(0xFF3F51B5), fontSize = 13.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun MainButton(text: String, modifier: Modifier, isPrimary: Boolean, isOperator: Boolean, onClick: () -> Unit) {
    val containerColor = when {
        isPrimary -> Color(0xFF3F51B5)
        isOperator -> Color(0xFF1A1A1A)
        else -> Color(0xFF121212)
    }
    val textColor = when {
        isPrimary -> Color.White
        isOperator -> Color(0xFF3F51B5)
        else -> Color.White
    }
    Surface(
        onClick = onClick,
        shape = CircleShape,
        color = containerColor,
        modifier = modifier.then(if (text != "=") Modifier.aspectRatio(1f) else Modifier.height(64.dp))
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(text = text, color = textColor, fontSize = 24.sp, fontWeight = FontWeight.Medium)
        }
    }
}

fun evaluateExpression(expr: String): String {
    return try {
        val sanitized = expr
            .replace("π", Math.PI.toString())
            .replace("√(", "sqrt(")
            .replace("×", "*")
            .replace("÷", "/")
        val value = ExprParser(sanitized).parse()
        when {
            value.isNaN() || value.isInfinite() -> "Error"
            value == value.toLong().toDouble() -> value.toLong().toString()
            else -> "%.10g".format(value).trimEnd('0').trimEnd('.')
        }
    } catch (e: Exception) {
        "Error"
    }
}

private class ExprParser(private val input: String) {
    private var pos = 0

    fun parse(): Double {
        val result = parseExpr()
        if (pos < input.length) throw IllegalArgumentException("Unexpected char: ${input[pos]}")
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
                    else   -> throw IllegalArgumentException("Unknown: $fn")
                }
            }
        }
        val start = pos
        if (pos < input.length && input[pos] == '-') pos++
        while (pos < input.length && (input[pos].isDigit() || input[pos] == '.')) pos++
        return input.substring(start, pos).toDouble()
    }
}

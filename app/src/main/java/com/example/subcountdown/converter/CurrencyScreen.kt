package com.example.subcountdown.converter

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun CurrencyScreen(viewModel: CurrencyViewModel) {
    var amountText by remember { mutableStateOf("1") }
    var fromCurrency by remember { mutableStateOf("USD") }
    var toCurrency by remember { mutableStateOf("EUR") }
    var showDialog by remember { mutableStateOf<String?>(null) } // "from" or "to"

    val amount = amountText.toDoubleOrNull() ?: 0.0
    val result = viewModel.convert(amount, fromCurrency, toCurrency)

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Large Input Area
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A1A))
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                // From Row
                CurrencyRow(
                    label = "From",
                    selectedCurrency = fromCurrency,
                    onClick = { showDialog = "from" }
                )
                
                TextField(
                    value = amountText,
                    onValueChange = { amountText = it },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = LocalTextStyle.current.copy(
                        fontSize = 42.sp,
                        fontWeight = FontWeight.Light,
                        color = Color.White
                    ),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    )
                )

                HorizontalDivider(color = Color.Gray.copy(alpha = 0.2f), thickness = 1.dp, modifier = Modifier.padding(vertical = 16.dp))

                // Swap Icon
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    IconButton(
                        onClick = {
                            val temp = fromCurrency
                            fromCurrency = toCurrency
                            toCurrency = temp
                        },
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF3F51B5))
                    ) {
                        Icon(Icons.Default.SwapVert, contentDescription = "Swap", tint = Color.White)
                    }
                }

                // To Row
                CurrencyRow(
                    label = "To",
                    selectedCurrency = toCurrency,
                    onClick = { showDialog = "to" }
                )

                Text(
                    text = "%.2f".format(result),
                    fontSize = 42.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF3F51B5),
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }

        if (viewModel.isLoading) {
            CircularProgressIndicator(color = Color(0xFF3F51B5), modifier = Modifier.padding(top = 16.dp))
        }
    }

    if (showDialog != null) {
        CurrencySelectionDialog(
            currencies = viewModel.rates.keys.toList().sorted(),
            onDismiss = { showDialog = null },
            onSelect = { 
                if (showDialog == "from") fromCurrency = it else toCurrency = it
                showDialog = null
            }
        )
    }
}

@Composable
fun CurrencyRow(label: String, selectedCurrency: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, color = Color.Gray, fontSize = 14.sp)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(selectedCurrency, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = Color.Gray)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CurrencySelectionDialog(currencies: List<String>, onDismiss: () -> Unit, onSelect: (String) -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Select Currency") },
        text = {
            Box(modifier = Modifier.height(400.dp)) {
                LazyColumn {
                    items(currencies) { currency ->
                        ListItem(
                            headlineContent = { Text(currency) },
                            modifier = Modifier.clickable { onSelect(currency) }
                        )
                    }
                }
            }
        },
        confirmButton = { TextButton(onClick = onDismiss) { Text("Close") } }
    )
}

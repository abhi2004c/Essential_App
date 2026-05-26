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
import androidx.compose.ui.window.DialogProperties
import com.example.subcountdown.core.ui.PremiumTextField

@Composable
fun CurrencyScreen(viewModel: CurrencyViewModel) {
    var amountText by remember { mutableStateOf("1") }
    var fromCurrency by remember { mutableStateOf("USD") }
    var toCurrency by remember { mutableStateOf("EUR") }
    var showDialog by remember { mutableStateOf<String?>(null) }

    val amount = amountText.toDoubleOrNull() ?: 0.0
    val result = viewModel.convert(amount, fromCurrency, toCurrency)

    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        if (viewModel.favoriteCurrencies.isNotEmpty()) {
            Row(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                viewModel.favoriteCurrencies.take(5).forEach { fav ->
                    SuggestionChip(onClick = { toCurrency = fav }, label = { Text(fav, color = Color.White) })
                }
            }
        }

        Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(28.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A1A))) {
            Column(modifier = Modifier.padding(24.dp)) {
                CurrencyRow(label = "From", selectedCurrency = fromCurrency, onClick = { showDialog = "from" })
                TextField(
                    value = amountText, onValueChange = { amountText = it },
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = LocalTextStyle.current.copy(fontSize = 32.sp, color = Color.White),
                    colors = TextFieldDefaults.colors(focusedContainerColor = Color.Transparent, unfocusedContainerColor = Color.Transparent, focusedTextColor = Color.White, unfocusedTextColor = Color.White)
                )
                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = Color.Gray.copy(alpha = 0.2f))
                CurrencyRow(label = "To", selectedCurrency = toCurrency, onClick = { showDialog = "to" })
                Text(text = "%.2f".format(result), fontSize = 32.sp, fontWeight = FontWeight.Bold, color = Color(0xFF3F51B5))
            }
        }

        if (viewModel.isLoading) CircularProgressIndicator(modifier = Modifier.padding(top = 16.dp))
    }

    if (showDialog != null) {
        CurrencySelectionDialog(
            viewModel = viewModel,
            onDismiss = { showDialog = null },
            onSelect = { if (showDialog == "from") fromCurrency = it else toCurrency = it; showDialog = null }
        )
    }
}

@Composable
fun CurrencyRow(label: String, selectedCurrency: String, onClick: () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth().clickable { onClick() }.padding(vertical = 8.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, color = Color.Gray)
        Text(selectedCurrency, color = Color.White, fontWeight = FontWeight.Bold)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CurrencySelectionDialog(viewModel: CurrencyViewModel, onDismiss: () -> Unit, onSelect: (String) -> Unit) {
    var query by remember { mutableStateOf("") }
    val rates = viewModel.rates
    val list = rates.keys.filter { it.contains(query, ignoreCase = true) }.sorted()

    AlertDialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
        modifier = Modifier.padding(16.dp),
        content = {
            Surface(modifier = Modifier.fillMaxSize(), color = Color.Black, shape = RoundedCornerShape(32.dp)) {
                Column(modifier = Modifier.padding(20.dp)) {
                    PremiumTextField(value = query, onValueChange = { query = it }, label = "Search Currency")
                    Spacer(modifier = Modifier.height(16.dp))
                    LazyColumn(modifier = Modifier.height(400.dp)) {
                        items(list) { cur ->
                            val isFav = viewModel.favoriteCurrencies.contains(cur)
                            ListItem(
                                headlineContent = { Text(cur, color = Color.White) },
                                trailingContent = {
                                    IconButton(onClick = { viewModel.toggleFavorite(cur) }) {
                                        Icon(if (isFav) Icons.Default.Star else Icons.Default.StarBorder, contentDescription = null, tint = Color.Yellow)
                                    }
                                },
                                modifier = Modifier.clickable { onSelect(cur) },
                                colors = ListItemDefaults.colors(containerColor = Color.Transparent)
                            )
                        }
                    }
                    Button(onClick = onDismiss, modifier = Modifier.fillMaxWidth()) { Text("Close") }
                }
            }
        }
    )
}

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
import com.example.subcountdown.core.ui.GlassCard
import com.example.subcountdown.core.ui.PremiumTextField

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConverterScreen(
    unitVM: ConverterViewModel = viewModel(),
    currencyVM: CurrencyViewModel = viewModel()
) {
    var mode by remember { mutableStateOf("Unit") } // "Unit" or "Currency"
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(16.dp)
    ) {
        Text(
            "Converter Hub",
            color = Color.White,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 16.dp)
        )

        // Mode Selector
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            FilterChip(
                selected = mode == "Unit",
                onClick = { mode = "Unit" },
                label = { Text("Units") },
                modifier = Modifier.weight(1f),
                colors = FilterChipDefaults.filterChipColors(selectedContainerColor = Color(0xFF3F51B5), selectedLabelColor = Color.White)
            )
            FilterChip(
                selected = mode == "Currency",
                onClick = { mode = "Currency" },
                label = { Text("Currency") },
                modifier = Modifier.weight(1f),
                colors = FilterChipDefaults.filterChipColors(selectedContainerColor = Color(0xFF3F51B5), selectedLabelColor = Color.White)
            )
        }

        if (mode == "Unit") {
            UnitConverterContent(unitVM)
        } else {
            CurrencyScreen(currencyVM)
        }
    }
}

@Composable
fun UnitConverterContent(viewModel: ConverterViewModel) {
    var selectedBaseUnit by remember { mutableStateOf(viewModel.lengthUnits[0]) }
    val watchList = remember { mutableStateListOf<ConversionUnit>() }
    var showCategoryMenu by remember { mutableStateOf(false) }
    var showUnitMenu by remember { mutableStateOf(false) }

    // Initialize watchList if empty
    LaunchedEffect(viewModel.selectedCategory) {
        watchList.clear()
        val units = viewModel.getUnits()
        if (units.isNotEmpty()) {
            selectedBaseUnit = units[0]
            watchList.addAll(units.drop(1).take(3))
        }
    }

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        // Category Selector
        Box {
            GlassCard(modifier = Modifier.fillMaxWidth().clickable { showCategoryMenu = true }) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(viewModel.selectedCategory.title, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Icon(Icons.Default.Category, contentDescription = null, tint = Color(0xFF3F51B5))
                }
            }
            DropdownMenu(expanded = showCategoryMenu, onDismissRequest = { showCategoryMenu = false }, modifier = Modifier.background(Color(0xFF1A1A1A))) {
                ConverterCategory.entries.forEach { cat ->
                    if (cat != ConverterCategory.TEMPERATURE) {
                        DropdownMenuItem(text = { Text(cat.title, color = Color.White) }, onClick = { viewModel.selectedCategory = cat; showCategoryMenu = false })
                    }
                }
            }
        }

        // Input Area
        PremiumTextField(
            value = viewModel.inputValue,
            onValueChange = { viewModel.inputValue = it },
            label = "Input Value",
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            trailingIcon = {
                TextButton(onClick = { showUnitMenu = true }) {
                    Text(selectedBaseUnit.name.split(" ").last(), color = Color(0xFF3F51B5), fontWeight = FontWeight.Bold)
                }
            }
        )
        
        DropdownMenu(expanded = showUnitMenu, onDismissRequest = { showUnitMenu = false }, modifier = Modifier.background(Color(0xFF1A1A1A))) {
            viewModel.getUnits().forEach { unit ->
                DropdownMenuItem(text = { Text(unit.name, color = Color.White) }, onClick = { selectedBaseUnit = unit; showUnitMenu = false })
            }
        }

        Text("Target Units (Multiple)", color = Color.Gray, fontSize = 14.sp, fontWeight = FontWeight.Bold)

        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.weight(1f)) {
            items(watchList) { unit ->
                val baseValue = (viewModel.inputValue.toDoubleOrNull() ?: 0.0) * selectedBaseUnit.ratioToBase
                val convertedValue = baseValue / unit.ratioToBase
                
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(unit.name, color = Color.Gray, fontSize = 12.sp)
                            Text("%.4f".format(convertedValue), color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                        }
                        IconButton(onClick = { watchList.remove(unit) }) {
                            Icon(Icons.Default.RemoveCircleOutline, contentDescription = "Remove", tint = Color.Red.copy(alpha = 0.5f))
                        }
                    }
                }
            }
            item {
                var showAddMenu by remember { mutableStateOf(false) }
                Box {
                    Button(
                        onClick = { showAddMenu = true },
                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.05f))
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, tint = Color(0xFF3F51B5))
                        Spacer(Modifier.width(8.dp))
                        Text("Add Target Unit", color = Color.White)
                    }
                    DropdownMenu(expanded = showAddMenu, onDismissRequest = { showAddMenu = false }, modifier = Modifier.background(Color(0xFF1A1A1A))) {
                        viewModel.getUnits().filter { (it != selectedBaseUnit) && (!watchList.contains(it)) }.forEach { unit ->
                            DropdownMenuItem(text = { Text(unit.name, color = Color.White) }, onClick = { watchList.add(unit); showAddMenu = false })
                        }
                    }
                }
            }
        }
    }
}

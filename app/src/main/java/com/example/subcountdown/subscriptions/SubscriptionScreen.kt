package com.example.subcountdown.subscriptions

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.subcountdown.core.ui.GlassCard

@Composable
fun SubscriptionScreen(viewModel: SubViewModel) {
    var showAddDialog by remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = Color(0xFF3F51B5),
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Subscription")
            }
        },
        containerColor = Color.Black
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            HeaderSection(viewModel)
            
            Spacer(modifier = Modifier.height(24.dp))

            LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                items(viewModel.subscriptions) { sub ->
                    SubscriptionItemCard(sub) {
                        viewModel.removeSubscription(sub)
                    }
                }
            }
        }

        if (showAddDialog) {
            AddSubscriptionDialog(
                onDismiss = { showAddDialog = false },
                onAdd = { newSub ->
                    viewModel.addSubscription(newSub)
                    showAddDialog = false
                }
            )
        }
    }
}

@Composable
fun HeaderSection(viewModel: SubViewModel) {
    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text("Monthly Spending", color = Color.Gray, fontSize = 14.sp)
            Text(
                text = "$${"%.2f".format(viewModel.getTotalMonthlyCost())}",
                color = Color.White,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "${viewModel.subscriptions.size} Active Subscriptions",
                color = Color(0xFF3F51B5),
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun SubscriptionItemCard(sub: Subscription, onDelete: () -> Unit) {
    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            SubscriptionIcon(sub)
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(sub.name, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Text(sub.packageName, color = Color.Gray, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "${sub.daysLeft}d left",
                        color = if (sub.daysLeft < 7) Color(0xFFE57373) else Color.Gray,
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(modifier = Modifier.size(3.dp).background(Color.DarkGray, CircleShape))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = sub.billingCycle, color = Color.Gray, fontSize = 12.sp)
                }
            }
            
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "$${sub.price}",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.White.copy(alpha = 0.2f), modifier = Modifier.size(20.dp))
                }
            }
        }
    }
}

@Composable
fun SubscriptionIcon(sub: Subscription) {
    val cleanName = sub.name.lowercase().trim().replace(" ", "")
    val logoUrl = "https://www.google.com/s2/favicons?sz=128&domain=$cleanName.com"
    var isSuccess by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .size(50.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(sub.color.copy(alpha = 0.1f)),
        contentAlignment = Alignment.Center
    ) {
        if (!isSuccess) {
            Text(sub.name.take(1), color = sub.color, fontWeight = FontWeight.Bold, fontSize = 20.sp)
        }
        AsyncImage(
            model = logoUrl,
            contentDescription = null,
            onSuccess = { isSuccess = true },
            modifier = Modifier.fillMaxSize().padding(8.dp),
            contentScale = ContentScale.Fit
        )
    }
}

data class PredefinedPlan(val name: String, val price: Double, val cycle: String)
data class PredefinedApp(val name: String, val color: Color, val plans: List<PredefinedPlan>)

val popularApps = listOf(
    PredefinedApp("Netflix", Color(0xFFE50914), listOf(
        PredefinedPlan("Basic (Ads)", 6.99, "Monthly"),
        PredefinedPlan("Standard", 15.49, "Monthly"),
        PredefinedPlan("Premium", 22.99, "Monthly")
    )),
    PredefinedApp("Spotify", Color(0xFF1DB954), listOf(
        PredefinedPlan("Individual", 11.99, "Monthly"),
        PredefinedPlan("Duo", 16.99, "Monthly"),
        PredefinedPlan("Family", 19.99, "Monthly"),
        PredefinedPlan("Student", 5.99, "Monthly")
    )),
    PredefinedApp("YouTube", Color(0xFFFF0000), listOf(
        PredefinedPlan("Premium Individual", 13.99, "Monthly"),
        PredefinedPlan("Premium Family", 22.99, "Monthly"),
        PredefinedPlan("Premium Student", 7.99, "Monthly")
    )),
    PredefinedApp("Disney+", Color(0xFF113CCF), listOf(
        PredefinedPlan("Standard (Ads)", 7.99, "Monthly"),
        PredefinedPlan("Standard", 10.99, "Monthly"),
        PredefinedPlan("Premium", 13.99, "Monthly"),
        PredefinedPlan("Annual Premium", 139.99, "Yearly")
    )),
    PredefinedApp("Amazon Prime", Color(0xFF00A8E1), listOf(
        PredefinedPlan("Monthly", 14.99, "Monthly"),
        PredefinedPlan("Annual", 139.00, "Yearly"),
        PredefinedPlan("Student Monthly", 7.49, "Monthly")
    )),
    PredefinedApp("Apple Music", Color(0xFFFA243C), listOf(
        PredefinedPlan("Individual", 10.99, "Monthly"),
        PredefinedPlan("Family", 16.99, "Monthly"),
        PredefinedPlan("Student", 5.99, "Monthly")
    )),
    PredefinedApp("ChatGPT Plus", Color(0xFF10A37F), listOf(
        PredefinedPlan("Plus", 20.00, "Monthly")
    ))
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddSubscriptionDialog(onDismiss: () -> Unit, onAdd: (Subscription) -> Unit) {
    var selectedApp by remember { mutableStateOf<PredefinedApp?>(null) }
    var customName by remember { mutableStateOf("") }
    var selectedPlan by remember { mutableStateOf<PredefinedPlan?>(null) }
    var customPkg by remember { mutableStateOf("") }
    var customPrice by remember { mutableStateOf("") }
    var customDays by remember { mutableStateOf("30") }
    var customCycle by remember { mutableStateOf("Monthly") }
    
    var appExpanded by remember { mutableStateOf(false) }
    var planExpanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("New Subscription") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                // App Selection
                ExposedDropdownMenuBox(
                    expanded = appExpanded,
                    onExpandedChange = { appExpanded = it }
                ) {
                    TextField(
                        value = selectedApp?.name ?: customName,
                        onValueChange = { 
                            customName = it
                            selectedApp = null
                            selectedPlan = null
                        },
                        label = { Text("App Name") },
                        modifier = Modifier.fillMaxWidth().menuAnchor(type = ExposedDropdownMenuAnchorType.PrimaryEditable, enabled = true),
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = appExpanded) },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent
                        )
                    )
                    ExposedDropdownMenu(
                        expanded = appExpanded,
                        onDismissRequest = { appExpanded = false }
                    ) {
                        popularApps.forEach { app ->
                            DropdownMenuItem(
                                text = { Text(app.name) },
                                onClick = {
                                    selectedApp = app
                                    customName = app.name
                                    selectedPlan = null
                                    appExpanded = false
                                }
                            )
                        }
                    }
                }

                if (selectedApp != null) {
                    // Plan Selection for Predefined App
                    ExposedDropdownMenuBox(
                        expanded = planExpanded,
                        onExpandedChange = { planExpanded = it }
                    ) {
                        TextField(
                            value = selectedPlan?.name ?: "",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Select Plan") },
                            modifier = Modifier.fillMaxWidth().menuAnchor(type = ExposedDropdownMenuAnchorType.PrimaryNotEditable, enabled = true),
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = planExpanded) },
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent
                            )
                        )
                        ExposedDropdownMenu(
                            expanded = planExpanded,
                            onDismissRequest = { planExpanded = false }
                        ) {
                            selectedApp!!.plans.forEach { plan ->
                                DropdownMenuItem(
                                    text = { Text("${plan.name} - $${plan.price}") },
                                    onClick = {
                                        selectedPlan = plan
                                        planExpanded = false
                                    }
                                )
                            }
                        }
                    }
                } else {
                    // Custom Plan Entry
                    TextField(
                        value = customPkg, 
                        onValueChange = { customPkg = it }, 
                        label = { Text("Package Name") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent
                        )
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    TextField(
                        value = if (selectedPlan != null) selectedPlan!!.price.toString() else customPrice,
                        onValueChange = { if (selectedPlan == null) customPrice = it },
                        readOnly = selectedPlan != null,
                        label = { Text("Price") },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent
                        )
                    )
                    TextField(
                        value = customDays, 
                        onValueChange = { customDays = it }, 
                        label = { Text("Days Left") },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent
                        )
                    )
                }
                
                if (selectedPlan == null) {
                    Text("Billing Cycle", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilterChip(
                            selected = customCycle == "Monthly",
                            onClick = { customCycle = "Monthly" },
                            label = { Text("Monthly") }
                        )
                        FilterChip(
                            selected = customCycle == "Yearly",
                            onClick = { customCycle = "Yearly" },
                            label = { Text("Yearly") }
                        )
                    }
                } else {
                    Text("Billing Cycle: ${selectedPlan!!.cycle}", fontSize = 14.sp, color = Color.Gray)
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                val finalName = selectedApp?.name ?: customName
                val finalPrice = selectedPlan?.price ?: customPrice.toDoubleOrNull() ?: 0.0
                val finalPkg = selectedPlan?.name ?: customPkg
                val finalCycle = selectedPlan?.cycle ?: customCycle

                if (finalName.isNotBlank()) {
                    onAdd(Subscription(
                        name = finalName,
                        packageName = finalPkg,
                        price = finalPrice,
                        billingCycle = finalCycle,
                        daysLeft = customDays.toIntOrNull() ?: 30,
                        color = selectedApp?.color ?: Color(0xFF3F51B5)
                    ))
                }
            }) { Text("Add") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

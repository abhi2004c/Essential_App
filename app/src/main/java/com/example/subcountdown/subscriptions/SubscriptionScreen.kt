package com.example.subcountdown.subscriptions

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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.example.subcountdown.core.ui.GlassCard
import com.example.subcountdown.core.ui.PremiumTextField

@Composable
fun SubscriptionScreen(viewModel: SubViewModel) {
    var showAddDialog by remember { mutableStateOf(false) }
    var subscriptionToEdit by remember { mutableStateOf<Subscription?>(null) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = Color(0xFF3F51B5),
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add")
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
                    SubscriptionItemCard(
                        sub = sub,
                        onDelete = { viewModel.removeSubscription(sub.id) },
                        onEdit = { subscriptionToEdit = sub }
                    )
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

        if (subscriptionToEdit != null) {
            AddSubscriptionDialog(
                editingSubscription = subscriptionToEdit,
                onDismiss = { subscriptionToEdit = null },
                onAdd = { updatedSub ->
                    viewModel.updateSubscription(subscriptionToEdit!!.id, updatedSub)
                    subscriptionToEdit = null
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
            Text("$${"%.2f".format(viewModel.getTotalMonthlyCost())}", color = Color.White, fontSize = 32.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Text("${viewModel.subscriptions.size} Active", color = Color(0xFF3F51B5), fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
fun SubscriptionItemCard(sub: Subscription, onDelete: () -> Unit, onEdit: () -> Unit) {
    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(16.dp).clickable { onEdit() },
            verticalAlignment = Alignment.CenterVertically
        ) {
            SubscriptionIcon(sub)
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(sub.name, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Text(sub.packageName, color = Color.Gray, fontSize = 14.sp)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text("$${sub.price}", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = null, tint = Color.White.copy(alpha = 0.2f), modifier = Modifier.size(20.dp))
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
    Box(modifier = Modifier.size(48.dp).clip(RoundedCornerShape(12.dp)).background(sub.color.copy(alpha = 0.1f)), contentAlignment = Alignment.Center) {
        if (!isSuccess) Text(sub.name.take(1), color = sub.color, fontWeight = FontWeight.Bold)
        AsyncImage(model = logoUrl, contentDescription = null, onSuccess = { isSuccess = true }, modifier = Modifier.fillMaxSize().padding(8.dp))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddSubscriptionDialog(editingSubscription: Subscription? = null, onDismiss: () -> Unit, onAdd: (Subscription) -> Unit) {
    var name by remember { mutableStateOf(editingSubscription?.name ?: "") }
    var price by remember { mutableStateOf(editingSubscription?.price?.toString() ?: "") }
    var pkg by remember { mutableStateOf(editingSubscription?.packageName ?: "Premium") }
    var cycle by remember { mutableStateOf(editingSubscription?.billingCycle ?: "Monthly") }
    var daysLeft by remember { mutableStateOf(editingSubscription?.daysLeft?.toString() ?: "30") }

    AlertDialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
        modifier = Modifier.padding(16.dp),
        content = {
            Surface(modifier = Modifier.fillMaxWidth().wrapContentHeight(), shape = RoundedCornerShape(32.dp), color = Color(0xFF1A1A1A)) {
                Column(modifier = Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text(if (editingSubscription != null) "Edit" else "Add", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    PremiumTextField(value = name, onValueChange = { name = it }, label = "App Name")
                    PremiumTextField(value = pkg, onValueChange = { pkg = it }, label = "Package")
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Box(modifier = Modifier.weight(1f)) {
                            PremiumTextField(value = price, onValueChange = { price = it }, label = "Price", keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal))
                        }
                        Box(modifier = Modifier.weight(1f)) {
                            PremiumTextField(value = daysLeft, onValueChange = { daysLeft = it }, label = "Days Left", keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                        }
                    }
                    
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        FilterChip(selected = cycle == "Monthly", onClick = { cycle = "Monthly" }, label = { Text("Monthly") })
                        FilterChip(selected = cycle == "Yearly", onClick = { cycle = "Yearly" }, label = { Text("Yearly") })
                    }

                    Button(
                        onClick = {
                            if (name.isNotBlank()) {
                                onAdd(Subscription(
                                    id = editingSubscription?.id ?: System.currentTimeMillis(),
                                    name = name, packageName = pkg, price = price.toDoubleOrNull() ?: 0.0,
                                    billingCycle = cycle, daysLeft = daysLeft.toIntOrNull() ?: 30,
                                    color = Color(0xFF3F51B5)
                                ))
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        shape = RoundedCornerShape(16.dp)
                    ) { Text("Save") }
                }
            }
        }
    )
}

package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Store
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.ai.GeneratedProductListing
import com.example.data.model.Seller
import com.example.ui.theme.GhanaGoldAccent
import com.example.ui.theme.GhanaGreenPrimary
import com.example.ui.theme.VerifiedBadgeColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiVendorStockingDialog(
    sellers: List<Seller>,
    initialSellerId: String?,
    isGenerating: Boolean,
    previewListing: GeneratedProductListing?,
    errorMessage: String?,
    onDismiss: () -> Unit,
    onGenerate: (vendorRawInput: String) -> Unit,
    onStockProduct: (
        title: String,
        categoryId: String,
        priceGhs: Double,
        stockCount: Int,
        description: String,
        highlights: List<String>,
        badge: String
    ) -> Unit
) {
    var rawInput by remember {
        mutableStateOf("Shenzhen Supplier Lot #849: Active Noise Cancelling TWS Bluetooth 5.4 Earbuds, 45h playtime with charging case, IPX5 sweatproof, dual ENC mic for clear calling.")
    }
    var selectedSellerId by remember {
        mutableStateOf(initialSellerId ?: sellers.firstOrNull()?.id ?: "")
    }
    var sellerDropdownExpanded by remember { mutableStateOf(false) }

    // Editable preview states
    var editableTitle by remember(previewListing) { mutableStateOf(previewListing?.title ?: "") }
    var editablePrice by remember(previewListing) { mutableDoubleStateOf(previewListing?.priceGhs ?: 150.0) }
    var editableStock by remember(previewListing) { mutableIntStateOf(previewListing?.suggestedStock ?: 20) }
    var editableDescription by remember(previewListing) { mutableStateOf(previewListing?.description ?: "") }

    val sampleVendorPrompts = listOf(
        "🎧 Tech Gadget" to "Alibaba Supplier: 20000mAh Magnetic Wireless Fast Charging Power Bank with LED Display and dual 22.5W USB-A & Type-C output, aluminum shell.",
        "👗 Fashion Lot" to "Dubai Wholesale Market: Premium Kente & Silk Printed Maxi Kimono Robes with waist sash belt, batch of 35pcs, breathable lightweight fabric.",
        "🍯 Farm Produce" to "Volta Beekeepers Collective: Grade-A Pure Raw Honey 1000ml sealed leakproof plastic bottles, harvested from Afadjato forest reserves.",
        "📦 Home Decor" to "Accra Arts Wholesale: Handcrafted Solid Iroko & Mahogany Round Serving Platters (Set of 3), polished with food-grade organic beeswax."
    )

    val currentSeller = sellers.firstOrNull { it.id == selectedSellerId } ?: sellers.firstOrNull()

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .clip(RoundedCornerShape(22.dp))
                .testTag("ai_stocking_dialog"),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(GhanaGoldAccent),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.AutoAwesome,
                                contentDescription = null,
                                tint = Color.Black,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "AI Vendor Stocking",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = " 🇬🇭",
                                    style = MaterialTheme.typography.titleMedium
                                )
                            }
                            Text(
                                text = "Powered by Gemini 3.8 Flash • Instant Listing",
                                style = MaterialTheme.typography.bodySmall,
                                color = GhanaGreenPrimary,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("close_ai_stocking_dialog")
                    ) {
                        Icon(Icons.Filled.Close, contentDescription = "Close")
                    }
                }

                // Seller Store Selector
                ExposedDropdownMenuBox(
                    expanded = sellerDropdownExpanded,
                    onExpandedChange = { sellerDropdownExpanded = it }
                ) {
                    OutlinedTextField(
                        value = "Stock into: ${currentSeller?.businessName ?: "Select Merchant"}",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Destination Store / Merchant") },
                        leadingIcon = { Icon(Icons.Filled.Store, contentDescription = null, tint = GhanaGreenPrimary) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = sellerDropdownExpanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        shape = RoundedCornerShape(12.dp)
                    )
                    ExposedDropdownMenu(
                        expanded = sellerDropdownExpanded,
                        onDismissRequest = { sellerDropdownExpanded = false }
                    ) {
                        sellers.forEach { s ->
                            DropdownMenuItem(
                                text = {
                                    Column {
                                        Text(s.businessName, fontWeight = FontWeight.Bold)
                                        Text("${s.region} • ${s.category}", style = MaterialTheme.typography.bodySmall)
                                    }
                                },
                                onClick = {
                                    selectedSellerId = s.id
                                    sellerDropdownExpanded = false
                                }
                            )
                        }
                    }
                }

                // Sample Vendor Templates
                Text(
                    text = "Quick Sample Vendor Manifests:",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    sampleVendorPrompts.forEach { (label, manifest) ->
                        Surface(
                            modifier = Modifier
                                .clickable { rawInput = manifest }
                                .testTag("sample_manifest_${label.take(5)}"),
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                        ) {
                            Text(
                                text = label,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }
                }

                // Vendor raw input area
                OutlinedTextField(
                    value = rawInput,
                    onValueChange = { rawInput = it },
                    label = { Text("Vendor Manifest / Product Specs / Online URL") },
                    placeholder = { Text("Paste vendor product description, Alibaba/Amazon listing specs, or supplier invoice...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(105.dp)
                        .testTag("vendor_manifest_input"),
                    shape = RoundedCornerShape(12.dp)
                )

                // Generate Button
                Button(
                    onClick = { onGenerate(rawInput.trim()) },
                    enabled = rawInput.isNotBlank() && !isGenerating,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("ai_generate_listing_btn"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = GhanaGreenPrimary)
                ) {
                    if (isGenerating) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Gemini AI Analyzing Vendor Stock...", color = Color.White)
                    } else {
                        Icon(Icons.Filled.AutoAwesome, contentDescription = null, tint = GhanaGoldAccent, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Generate Marketplace Listing with AI", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }

                if (errorMessage != null) {
                    Text(
                        text = errorMessage,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                // Generated Preview & Stocking Section
                if (previewListing != null) {
                    HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("ai_listing_preview_card"),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = GhanaGoldAccent
                                ) {
                                    Text(
                                        text = previewListing.badge,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.Black,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }

                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = VerifiedBadgeColor.copy(alpha = 0.12f)
                                ) {
                                    Text(
                                        text = "Category: ${previewListing.categoryId.replace('_', ' ').capitalize()}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = VerifiedBadgeColor,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }

                            // Title input
                            OutlinedTextField(
                                value = editableTitle,
                                onValueChange = { editableTitle = it },
                                label = { Text("Generated Title (Ghana Market Optimized)") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("ai_preview_title_input"),
                                shape = RoundedCornerShape(10.dp)
                            )

                            // Price & Stock row
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedTextField(
                                    value = if (editablePrice > 0) editablePrice.toString() else "",
                                    onValueChange = { editablePrice = it.toDoubleOrNull() ?: 0.0 },
                                    label = { Text("Price in GH₵ *") },
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("ai_preview_price_input"),
                                    singleLine = true,
                                    shape = RoundedCornerShape(10.dp)
                                )

                                OutlinedTextField(
                                    value = if (editableStock > 0) editableStock.toString() else "",
                                    onValueChange = { editableStock = it.toIntOrNull() ?: 0 },
                                    label = { Text("Stock Units *") },
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("ai_preview_stock_input"),
                                    singleLine = true,
                                    shape = RoundedCornerShape(10.dp)
                                )
                            }

                            // Description input
                            OutlinedTextField(
                                value = editableDescription,
                                onValueChange = { editableDescription = it },
                                label = { Text("Customer Sales Description") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(100.dp),
                                shape = RoundedCornerShape(10.dp)
                            )

                            // Restock Advice
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = GhanaGreenPrimary.copy(alpha = 0.08f),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Inventory,
                                        contentDescription = null,
                                        tint = GhanaGreenPrimary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Text(
                                        text = "AI Stock Advice: ${previewListing.vendorRestockAdvice}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = GhanaGreenPrimary,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }

                            // Stock Action Button
                            Button(
                                onClick = {
                                    onStockProduct(
                                        editableTitle.ifBlank { previewListing.title },
                                        previewListing.categoryId,
                                        if (editablePrice > 0) editablePrice else previewListing.priceGhs,
                                        if (editableStock > 0) editableStock else previewListing.suggestedStock,
                                        editableDescription.ifBlank { previewListing.description },
                                        previewListing.highlights,
                                        previewListing.badge
                                    )
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("confirm_stock_marketplace_btn"),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = GhanaGoldAccent)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.CheckCircle,
                                    contentDescription = null,
                                    tint = Color.Black,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Stock to Marketplace (GH₵ ${String.format("%,.2f", editablePrice)})",
                                    color = Color.Black,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

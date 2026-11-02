package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Checkroom
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.data.model.Product
import com.example.data.model.Seller
import com.example.ui.theme.GhanaGoldAccent
import com.example.ui.theme.GhanaGreenContainer
import com.example.ui.theme.GhanaGreenPrimary
import com.example.ui.theme.GhanaRedTerracotta

@Composable
fun getCategoryIcon(categoryId: String): ImageVector {
    return when (categoryId) {
        "fashion_beauty" -> Icons.Filled.Checkroom
        "phones_electronics" -> Icons.Filled.Smartphone
        "home_living" -> Icons.Filled.Home
        "food_groceries" -> Icons.Filled.Restaurant
        "music_entertainment" -> Icons.Filled.MusicNote
        "digital_products" -> Icons.Filled.Description
        "art_handmade" -> Icons.Filled.Palette
        "business_services" -> Icons.Filled.Work
        "education_learning" -> Icons.Filled.School
        else -> Icons.Filled.ShoppingBag
    }
}

@Composable
fun ProductCard(
    product: Product,
    seller: Seller?,
    onProductClick: () -> Unit,
    onAddToCart: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onProductClick() }
            .testTag("product_card_${product.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            // Visual header representation
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                GhanaGreenPrimary.copy(alpha = 0.12f),
                                GhanaGreenContainer.copy(alpha = 0.35f)
                            )
                        )
                    )
                    .padding(10.dp)
            ) {
                // Category Icon banner
                Icon(
                    imageVector = getCategoryIcon(product.categoryId),
                    contentDescription = null,
                    tint = GhanaGreenPrimary.copy(alpha = 0.7f),
                    modifier = Modifier
                        .size(44.dp)
                        .align(Alignment.Center)
                )

                // Top Badge (if any)
                if (product.badge.isNotBlank()) {
                    Surface(
                        modifier = Modifier.align(Alignment.TopStart),
                        shape = RoundedCornerShape(6.dp),
                        color = if (product.badge.contains("Heritage") || product.badge.contains("Handmade"))
                            GhanaGoldAccent else GhanaGreenPrimary
                    ) {
                        Text(
                            text = product.badge,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                // Average Rating Badge (CRITICAL requirement: display average ratings on product listings)
                RatingSummaryBadge(
                    rating = product.rating,
                    reviewCount = product.reviewCount,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .testTag("product_rating_badge_${product.id}")
                )
            }

            // Card Body
            Column(
                modifier = Modifier.padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // Title
                Text(
                    text = product.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                // Seller name & location
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.LocationOn,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(13.dp)
                    )
                    Text(
                        text = "${seller?.businessName ?: "Local Seller"} • ${product.location}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.height(2.dp))

                // Price and Add to cart
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "GH₵ " + String.format("%,.2f", product.priceGhs),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = GhanaGreenPrimary
                        )
                        Text(
                            text = if (product.isService) "Service Fee" else "In Stock",
                            style = MaterialTheme.typography.labelSmall,
                            color = if (product.inStock) GhanaGreenPrimary else GhanaRedTerracotta,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    FilledTonalIconButton(
                        onClick = onAddToCart,
                        modifier = Modifier.testTag("add_cart_btn_${product.id}"),
                        colors = IconButtonDefaults.filledTonalIconButtonColors(
                            containerColor = GhanaGreenPrimary,
                            contentColor = Color.White
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Filled.AddShoppingCart,
                            contentDescription = "Add to Cart",
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}

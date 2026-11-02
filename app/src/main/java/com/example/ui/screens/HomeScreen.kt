package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Checkroom
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Work
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.Category
import com.example.data.model.Product
import com.example.data.model.Seller
import com.example.ui.components.ProductCard
import com.example.ui.components.RatingSummaryBadge
import com.example.ui.components.getCategoryIcon
import com.example.ui.theme.GhanaGoldAccent
import com.example.ui.theme.GhanaGoldContainer
import com.example.ui.theme.GhanaGreenContainer
import com.example.ui.theme.GhanaGreenPrimary
import com.example.ui.theme.GhanaRedTerracotta

data class CategoryChipModel(
    val id: String?,
    val label: String,
    val icon: ImageVector
)

@Composable
fun HomeScreen(
    products: List<Product>,
    sellers: List<Seller>,
    categories: List<Category>,
    searchQuery: String,
    selectedRegion: String,
    selectedCategoryId: String?,
    selectedTypeFilter: String,
    onSearchChange: (String) -> Unit,
    onRegionChange: (String) -> Unit,
    onCategoryFilterChange: (String?) -> Unit,
    onTypeFilterChange: (String) -> Unit,
    onCategoryClick: (Category) -> Unit,
    onProductClick: (Product) -> Unit,
    onSellerClick: (Seller) -> Unit,
    onAddToCart: (Product) -> Unit,
    onExploreCategoriesClick: () -> Unit,
    onBecomeSellerClick: () -> Unit,
    onAiStockingClick: () -> Unit
) {
    val sellerMap = remember(sellers) { sellers.associateBy { it.id } }
    var regionMenuExpanded by remember { mutableStateOf(false) }

    val regions = listOf(
        "All Ghana",
        "Greater Accra",
        "Ashanti (Kumasi)",
        "Volta",
        "Western",
        "Northern (Tamale)",
        "Central",
        "Eastern"
    )

    val filterTypes = listOf("All", "Products", "Services", "Top Rated 4.8+★")

    val categoryChips = remember {
        listOf(
            CategoryChipModel(null, "All", Icons.Filled.Storefront),
            CategoryChipModel("fashion_beauty", "Fashion", Icons.Filled.Checkroom),
            CategoryChipModel("phones_electronics", "Electronics", Icons.Filled.Smartphone),
            CategoryChipModel("home_living", "Home", Icons.Filled.Home),
            CategoryChipModel("food_groceries", "Food", Icons.Filled.Restaurant),
            CategoryChipModel("art_handmade", "Handmade", Icons.Filled.Palette),
            CategoryChipModel("digital_products", "Digital", Icons.Filled.Description),
            CategoryChipModel("music_entertainment", "Music", Icons.Filled.MusicNote),
            CategoryChipModel("business_services", "Services", Icons.Filled.Work),
            CategoryChipModel("education_learning", "Education", Icons.Filled.School)
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("home_screen_lazy_column"),
        contentPadding = PaddingValues(bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Hero Banner with Ghanaian Vibe
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .testTag("hero_banner_card"),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(230.dp)
                ) {
                    // Try to load generated hero banner, fallback to vibrant gradient
                    Image(
                        painter = painterResource(id = R.drawable.asigame_hero_banner_1792765050858),
                        contentDescription = "Asigame Ghana Marketplace",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )

                    // Scrim gradient for maximum readability
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Black.copy(alpha = 0.25f),
                                        Color.Black.copy(alpha = 0.85f)
                                    )
                                )
                            )
                    )

                    // Hero Content
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.Bottom
                    ) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = GhanaGoldAccent
                        ) {
                            Text(
                                text = "🇬🇭 ASIGAME MARKETPLACE",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.Black,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "Your Market. Your Business. Your Opportunity.",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )

                        Text(
                            text = "Discover products, explore local businesses, find useful services and connect with sellers across Ghana—all in one place.",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.9f),
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Button(
                                onClick = onExploreCategoriesClick,
                                modifier = Modifier.testTag("hero_explore_categories_btn"),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = GhanaGoldAccent)
                            ) {
                                Text(
                                    text = "EXPLORE CATEGORIES",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black
                                )
                            }

                            OutlinedButton(
                                onClick = onBecomeSellerClick,
                                modifier = Modifier.testTag("hero_become_seller_btn"),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                                border = androidx.compose.foundation.BorderStroke(1.5.dp, Color.White)
                            ) {
                                Text(
                                    text = "BECOME A SELLER",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }

        // 2. Search & Region Filter Bar
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = onSearchChange,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("home_search_input"),
                        placeholder = { Text("Search products, services, sellers...") },
                        leadingIcon = {
                            Icon(Icons.Filled.Search, contentDescription = "Search", tint = GhanaGreenPrimary)
                        },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { onSearchChange("") }) {
                                    Icon(Icons.Filled.Close, contentDescription = "Clear")
                                }
                            }
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GhanaGreenPrimary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                        )
                    )

                    // Region Selector Button
                    Box {
                        Surface(
                            modifier = Modifier
                                .clickable { regionMenuExpanded = true }
                                .testTag("region_filter_btn"),
                            shape = RoundedCornerShape(14.dp),
                            color = GhanaGreenPrimary.copy(alpha = 0.12f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, GhanaGreenPrimary.copy(alpha = 0.4f))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 14.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = selectedRegion.replace(" (Kumasi)", "").replace(" (Tamale)", ""),
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = GhanaGreenPrimary
                                )
                                Icon(
                                    imageVector = Icons.Filled.ExpandMore,
                                    contentDescription = "Select Region",
                                    tint = GhanaGreenPrimary,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }

                        DropdownMenu(
                            expanded = regionMenuExpanded,
                            onDismissRequest = { regionMenuExpanded = false }
                        ) {
                            regions.forEach { reg ->
                                DropdownMenuItem(
                                    text = { Text(reg) },
                                    onClick = {
                                        onRegionChange(reg)
                                        regionMenuExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                // Horizontal Scrollable List of Category Filter Chips (e.g., 'Fashion', 'Electronics', 'Home')
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .testTag("category_filter_chips_row"),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    categoryChips.forEach { chip ->
                        val isSelected = (chip.id == selectedCategoryId) || (chip.id == null && selectedCategoryId == null)
                        FilterChip(
                            selected = isSelected,
                            onClick = { onCategoryFilterChange(chip.id) },
                            leadingIcon = {
                                Icon(
                                    imageVector = if (isSelected && chip.id != null) Icons.Filled.Check else chip.icon,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp),
                                    tint = if (isSelected) Color.White else GhanaGreenPrimary
                                )
                            },
                            label = {
                                Text(
                                    text = chip.label,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                )
                            },
                            modifier = Modifier.testTag("category_filter_chip_${chip.label.replace(' ', '_')}"),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = GhanaGreenPrimary,
                                selectedLabelColor = Color.White,
                                selectedLeadingIconColor = Color.White
                            )
                        )
                    }
                }

                // Secondary Type Filter chips: All, Products, Services, Top Rated 4.8+★
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    filterTypes.forEach { filter ->
                        val isSelected = selectedTypeFilter == filter
                        FilterChip(
                            selected = isSelected,
                            onClick = { onTypeFilterChange(filter) },
                            label = { Text(filter) },
                            modifier = Modifier.testTag("filter_chip_$filter"),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = GhanaGoldAccent,
                                selectedLabelColor = Color.Black
                            )
                        )
                    }
                }
            }
        }

        // 2b. AI Assisted Vendor Stocking Banner
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .clickable { onAiStockingClick() }
                    .testTag("home_ai_vendor_stocking_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = GhanaGoldAccent.copy(alpha = 0.12f)
                ),
                border = androidx.compose.foundation.BorderStroke(1.dp, GhanaGoldAccent.copy(alpha = 0.4f))
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
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

                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "AI Vendor Stocking",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Text(" 🇬🇭", style = MaterialTheme.typography.bodySmall)
                        }
                        Text(
                            text = "Import supplier manifests or online vendor links to auto-generate titles, GH₵ prices, and stock instantly.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Button(
                        onClick = onAiStockingClick,
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = GhanaGreenPrimary)
                    ) {
                        Text("Stock AI", color = Color.White, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // 3. Category Highlights Row
        item {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "🛍️ Explore Categories",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "See All",
                        style = MaterialTheme.typography.labelMedium,
                        color = GhanaGreenPrimary,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .clickable { onExploreCategoriesClick() }
                            .padding(4.dp)
                    )
                }

                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(categories) { category ->
                        Surface(
                            modifier = Modifier
                                .clickable { onCategoryClick(category) }
                                .testTag("cat_chip_${category.id}"),
                            shape = RoundedCornerShape(14.dp),
                            color = MaterialTheme.colorScheme.surface,
                            tonalElevation = 2.dp,
                            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(GhanaGreenPrimary.copy(alpha = 0.12f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = getCategoryIcon(category.id),
                                        contentDescription = null,
                                        tint = GhanaGreenPrimary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Text(
                                    text = category.title,
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }
            }
        }

        // 4. "Support Ghanaian Businesses" Spotlight Carousel (With Seller Ratings!)
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(GhanaGreenPrimary.copy(alpha = 0.05f))
                    .padding(vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "🇬🇭 Support Ghanaian Businesses",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = GhanaGreenPrimary
                        )
                        Text(
                            text = "Verified entrepreneurs & artisans across Ghana",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(sellers) { seller ->
                        Card(
                            modifier = Modifier
                                .width(240.dp)
                                .clickable { onSellerClick(seller) }
                                .testTag("seller_spotlight_${seller.id}"),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(14.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = seller.businessName,
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        modifier = Modifier.weight(1f)
                                    )
                                    // Average rating on seller card
                                    RatingSummaryBadge(
                                        rating = seller.rating,
                                        reviewCount = seller.reviewCount
                                    )
                                }

                                Text(
                                    text = "${seller.region} • ${seller.category}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                Text(
                                    text = seller.bio,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )

                                Button(
                                    onClick = { onSellerClick(seller) },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = GhanaGreenPrimary)
                                ) {
                                    Text("Visit Store", style = MaterialTheme.typography.labelMedium, color = Color.White)
                                }
                            }
                        }
                    }
                }
            }
        }

        // 5. Featured Products & Services Header
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Featured Products & Services",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${products.size} offerings in Ghana Cedis (GH₵)",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // 6. Products List (displaying average ratings, GH₵ prices, locations)
        if (products.isEmpty()) {
            item {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Search,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(40.dp)
                        )
                        Text(
                            text = "No items found matching your filters",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Try clearing your search query or picking 'All Ghana'",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        } else {
            items(products) { product ->
                Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                    ProductCard(
                        product = product,
                        seller = sellerMap[product.sellerId],
                        onProductClick = { onProductClick(product) },
                        onAddToCart = { onAddToCart(product) }
                    )
                }
            }
        }

        // 7. "Why Asigame Marketplace?" Educational & Value Proposition Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = GhanaGoldContainer.copy(alpha = 0.4f)
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "🤝 Why Asigame Marketplace?",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    val points = listOf(
                        "One Marketplace" to "Discover multiple categories of products and services in one place.",
                        "Supporting Local Enterprise" to "Help Ghanaian entrepreneurs and small businesses reach potential customers.",
                        "Built for Mobile" to "A simple browsing experience designed with mobile phone users in mind.",
                        "Verified Reviews" to "Authentic customer reviews and ratings to build trust in Ghanaian commerce.",
                        "Growing Together" to "Expanding categories and features with payment in Ghana cedis (GH₵)."
                    )

                    points.forEach { (heading, desc) ->
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Text(
                                text = "•",
                                fontWeight = FontWeight.Bold,
                                color = GhanaGoldAccent,
                                fontSize = 16.sp
                            )
                            Column {
                                Text(
                                    text = heading,
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = desc,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Button(
                        onClick = onBecomeSellerClick,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("why_asigame_sell_btn"),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = GhanaGreenPrimary)
                    ) {
                        Text("Sell on Asigame Marketplace 🇬🇭", color = Color.White)
                    }
                }
            }
        }
    }
}

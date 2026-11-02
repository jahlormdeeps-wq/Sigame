package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddBusiness
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Store
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.model.ReviewTargetType
import com.example.ui.components.AiVendorStockingDialog
import com.example.ui.components.BecomeSellerDialog
import com.example.ui.components.CheckoutDialog
import com.example.ui.components.EnquiryDialog
import com.example.ui.components.WriteReviewDialog
import com.example.ui.screens.CategoriesScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.OrdersAndCartScreen
import com.example.ui.screens.ProductDetailScreen
import com.example.ui.screens.SellerDetailScreen
import com.example.ui.screens.SellersScreen
import com.example.ui.theme.GhanaGoldAccent
import com.example.ui.theme.GhanaGreenPrimary
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.MarketViewModel
import com.example.ui.viewmodel.NavigationTab
import kotlinx.coroutines.flow.collectLatest

import com.example.util.configureAppCheck

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        configureAppCheck(this, intent)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                AsigameApp()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AsigameApp(
    viewModel: MarketViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val filteredProducts by viewModel.filteredProducts.collectAsState()
    val allProducts by viewModel.products.collectAsState()
    val sellers by viewModel.sellers.collectAsState()
    val reviews by viewModel.reviews.collectAsState()
    val cart by viewModel.cart.collectAsState()
    val orders by viewModel.orders.collectAsState()
    val categories = viewModel.categories

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.snackbarMessage.collectLatest { msg ->
            snackbarHostState.showSnackbar(msg)
        }
    }

    // Predictive back / stack back handler
    BackHandler(
        enabled = uiState.selectedProduct != null ||
                uiState.selectedSeller != null ||
                uiState.selectedCategory != null ||
                uiState.currentTab != NavigationTab.HOME ||
                uiState.showWriteReviewDialog ||
                uiState.showBecomeSellerDialog ||
                uiState.showEnquiryDialog ||
                uiState.showCheckoutSheet
    ) {
        viewModel.handleBack()
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            if (uiState.selectedProduct == null && uiState.selectedSeller == null) {
                TopAppBar(
                    title = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.testTag("app_brand_header")
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(CircleShape)
                                    .background(GhanaGoldAccent),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "★",
                                    color = Color.Black,
                                    fontWeight = FontWeight.Black
                                )
                            }
                            Text(
                                text = " ASIGAME",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Black,
                                color = GhanaGreenPrimary
                            )
                            Text(
                                text = " 🇬🇭",
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                    },
                    actions = {
                        Button(
                            onClick = { viewModel.openAiStockingDialog() },
                            modifier = Modifier
                                .padding(end = 4.dp)
                                .testTag("topbar_ai_stock_btn"),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = GhanaGreenPrimary)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.AutoAwesome,
                                contentDescription = null,
                                tint = GhanaGoldAccent,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = " AI Stock",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.labelMedium
                            )
                        }

                        Button(
                            onClick = { viewModel.openBecomeSellerDialog() },
                            modifier = Modifier
                                .padding(end = 4.dp)
                                .testTag("topbar_become_seller_btn"),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = GhanaGoldAccent)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.AddBusiness,
                                contentDescription = null,
                                tint = Color.Black,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = " Sell",
                                color = Color.Black,
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.labelMedium
                            )
                        }

                        IconButton(
                            onClick = { viewModel.setTab(NavigationTab.ORDERS_CART) },
                            modifier = Modifier.testTag("topbar_cart_btn")
                        ) {
                            BadgedBox(
                                badge = {
                                    if (cart.isNotEmpty()) {
                                        Badge(
                                            containerColor = GhanaGreenPrimary,
                                            contentColor = Color.White
                                        ) {
                                            Text("${cart.sumOf { it.quantity }}")
                                        }
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.ShoppingBag,
                                    contentDescription = "Cart",
                                    tint = GhanaGreenPrimary
                                )
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                )
            }
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp
            ) {
                NavigationBarItem(
                    selected = uiState.currentTab == NavigationTab.HOME && uiState.selectedProduct == null && uiState.selectedSeller == null,
                    onClick = { viewModel.setTab(NavigationTab.HOME) },
                    icon = { Icon(Icons.Filled.Storefront, contentDescription = "Marketplace") },
                    label = { Text("Market") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = GhanaGreenPrimary,
                        selectedTextColor = GhanaGreenPrimary,
                        indicatorColor = GhanaGreenPrimary.copy(alpha = 0.15f)
                    ),
                    modifier = Modifier.testTag("nav_tab_home")
                )

                NavigationBarItem(
                    selected = uiState.currentTab == NavigationTab.CATEGORIES && uiState.selectedProduct == null && uiState.selectedSeller == null,
                    onClick = { viewModel.setTab(NavigationTab.CATEGORIES) },
                    icon = { Icon(Icons.Filled.Category, contentDescription = "Categories") },
                    label = { Text("Categories") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = GhanaGreenPrimary,
                        selectedTextColor = GhanaGreenPrimary,
                        indicatorColor = GhanaGreenPrimary.copy(alpha = 0.15f)
                    ),
                    modifier = Modifier.testTag("nav_tab_categories")
                )

                NavigationBarItem(
                    selected = uiState.currentTab == NavigationTab.SELLERS && uiState.selectedProduct == null && uiState.selectedSeller == null,
                    onClick = { viewModel.setTab(NavigationTab.SELLERS) },
                    icon = { Icon(Icons.Filled.Store, contentDescription = "Sellers") },
                    label = { Text("Sellers") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = GhanaGreenPrimary,
                        selectedTextColor = GhanaGreenPrimary,
                        indicatorColor = GhanaGreenPrimary.copy(alpha = 0.15f)
                    ),
                    modifier = Modifier.testTag("nav_tab_sellers")
                )

                NavigationBarItem(
                    selected = uiState.currentTab == NavigationTab.ORDERS_CART && uiState.selectedProduct == null && uiState.selectedSeller == null,
                    onClick = { viewModel.setTab(NavigationTab.ORDERS_CART) },
                    icon = {
                        BadgedBox(
                            badge = {
                                if (cart.isNotEmpty()) {
                                    Badge(
                                        containerColor = GhanaGreenPrimary,
                                        contentColor = Color.White
                                    ) {
                                        Text("${cart.sumOf { it.quantity }}")
                                    }
                                }
                            }
                        ) {
                            Icon(Icons.Filled.ShoppingBag, contentDescription = "My Asigame")
                        }
                    },
                    label = { Text("Orders & Cart") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = GhanaGreenPrimary,
                        selectedTextColor = GhanaGreenPrimary,
                        indicatorColor = GhanaGreenPrimary.copy(alpha = 0.15f)
                    ),
                    modifier = Modifier.testTag("nav_tab_orders_cart")
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when {
                uiState.selectedProduct != null -> {
                    val product = uiState.selectedProduct!!
                    val seller = viewModel.getSeller(product.sellerId)
                    ProductDetailScreen(
                        product = product,
                        seller = seller,
                        reviews = reviews,
                        hasPurchased = viewModel.hasPurchased(product.id),
                        onBack = { viewModel.selectProduct(null) },
                        onSellerClick = { targetSeller ->
                            viewModel.selectProduct(null)
                            viewModel.selectSeller(targetSeller)
                        },
                        onAddToCart = { prod, qty -> viewModel.addToCart(prod, qty) },
                        onEnquireClick = { targetSeller -> viewModel.openEnquiryDialog(targetSeller) },
                        onWriteReviewClick = {
                            viewModel.openWriteReviewDialog(
                                targetId = product.id,
                                targetType = ReviewTargetType.PRODUCT,
                                targetTitle = product.title
                            )
                        }
                    )
                }

                uiState.selectedSeller != null -> {
                    val seller = uiState.selectedSeller!!
                    SellerDetailScreen(
                        seller = seller,
                        products = allProducts,
                        reviews = reviews,
                        onBack = { viewModel.selectSeller(null) },
                        onProductClick = { prod -> viewModel.selectProduct(prod) },
                        onAddToCart = { prod -> viewModel.addToCart(prod) },
                        onEnquireClick = { viewModel.openEnquiryDialog(seller) },
                        onWriteReviewClick = {
                            viewModel.openWriteReviewDialog(
                                targetId = seller.id,
                                targetType = ReviewTargetType.SELLER,
                                targetTitle = seller.businessName
                            )
                        },
                        onAiStockingClick = { viewModel.openAiStockingDialog(seller.id) }
                    )
                }

                else -> {
                    when (uiState.currentTab) {
                        NavigationTab.HOME -> {
                            HomeScreen(
                                products = filteredProducts,
                                sellers = sellers,
                                categories = categories,
                                searchQuery = uiState.searchQuery,
                                selectedRegion = uiState.selectedRegion,
                                selectedCategoryId = uiState.selectedCategoryId,
                                selectedTypeFilter = uiState.selectedTypeFilter,
                                onSearchChange = { viewModel.setSearchQuery(it) },
                                onRegionChange = { viewModel.setRegion(it) },
                                onCategoryFilterChange = { catId -> viewModel.selectCategoryFilter(catId) },
                                onTypeFilterChange = { viewModel.setTypeFilter(it) },
                                onCategoryClick = { cat -> viewModel.selectCategory(cat) },
                                onProductClick = { prod -> viewModel.selectProduct(prod) },
                                onSellerClick = { seller -> viewModel.selectSeller(seller) },
                                onAddToCart = { prod -> viewModel.addToCart(prod) },
                                onExploreCategoriesClick = { viewModel.setTab(NavigationTab.CATEGORIES) },
                                onBecomeSellerClick = { viewModel.openBecomeSellerDialog() },
                                onAiStockingClick = { viewModel.openAiStockingDialog() }
                            )
                        }

                        NavigationTab.CATEGORIES -> {
                            CategoriesScreen(
                                categories = categories,
                                selectedCategory = uiState.selectedCategory,
                                products = allProducts,
                                sellers = sellers,
                                onSelectCategory = { cat -> viewModel.selectCategory(cat) },
                                onProductClick = { prod -> viewModel.selectProduct(prod) },
                                onAddToCart = { prod -> viewModel.addToCart(prod) }
                            )
                        }

                        NavigationTab.SELLERS -> {
                            SellersScreen(
                                sellers = sellers,
                                onSellerClick = { seller -> viewModel.selectSeller(seller) },
                                onEnquireClick = { seller -> viewModel.openEnquiryDialog(seller) },
                                onBecomeSellerClick = { viewModel.openBecomeSellerDialog() }
                            )
                        }

                        NavigationTab.ORDERS_CART -> {
                            OrdersAndCartScreen(
                                cartItems = cart,
                                orders = orders,
                                sellers = sellers,
                                reviews = reviews,
                                onUpdateQuantity = { id, q -> viewModel.updateCartQuantity(id, q) },
                                onRemoveItem = { id -> viewModel.removeFromCart(id) },
                                onProceedCheckout = { viewModel.openCheckout() },
                                onRateProduct = { prod ->
                                    viewModel.openWriteReviewDialog(
                                        targetId = prod.id,
                                        targetType = ReviewTargetType.PRODUCT,
                                        targetTitle = prod.title
                                    )
                                },
                                onRateSeller = { seller ->
                                    viewModel.openWriteReviewDialog(
                                        targetId = seller.id,
                                        targetType = ReviewTargetType.SELLER,
                                        targetTitle = seller.businessName
                                    )
                                },
                                onProductClick = { prod -> viewModel.selectProduct(prod) },
                                onExploreShopClick = { viewModel.setTab(NavigationTab.HOME) }
                            )
                        }
                    }
                }
            }

            // Write Review Dialog (Rate 1-5 Stars and Leave Written Review)
            if (uiState.showWriteReviewDialog && uiState.reviewTargetId != null) {
                val targetId = uiState.reviewTargetId!!
                val targetType = uiState.reviewTargetType ?: ReviewTargetType.PRODUCT
                val isVerified = if (targetType == ReviewTargetType.PRODUCT) {
                    viewModel.hasPurchased(targetId)
                } else {
                    true // If rating from order or store
                }

                WriteReviewDialog(
                    targetTitle = uiState.reviewTargetTitle ?: "Review",
                    targetType = targetType,
                    isVerifiedBuyer = isVerified,
                    onDismiss = { viewModel.closeWriteReviewDialog() },
                    onSubmit = { authorName, rating, comment ->
                        viewModel.submitReview(authorName, rating, comment)
                    }
                )
            }

            // Become a Seller Dialog
            if (uiState.showBecomeSellerDialog) {
                BecomeSellerDialog(
                    isSubmitted = uiState.sellerApplicationSuccess,
                    onDismiss = { viewModel.closeBecomeSellerDialog() },
                    onSubmit = { bName, oName, cat, reg, phone, email, desc ->
                        viewModel.submitSellerApplication(bName, oName, cat, reg, phone, email, desc)
                    }
                )
            }

            // Customer Enquiry Dialog
            if (uiState.showEnquiryDialog && uiState.enquirySeller != null) {
                EnquiryDialog(
                    seller = uiState.enquirySeller!!,
                    onDismiss = { viewModel.closeEnquiryDialog() },
                    onSubmit = { name, phone, sub, msg ->
                        viewModel.submitEnquiry(name, phone, sub, msg)
                    }
                )
            }

            // Checkout Dialog
            if (uiState.showCheckoutSheet) {
                val totalGhs = cart.sumOf { it.product.priceGhs * it.quantity }
                CheckoutDialog(
                    cartItems = cart,
                    totalGhs = totalGhs,
                    onDismiss = { viewModel.closeCheckout() },
                    onConfirmOrder = { name, phone, addr, payMethod ->
                        viewModel.placeOrder(name, phone, addr, payMethod)
                    }
                )
            }

            // AI Vendor Stocking Dialog (Gemini 3.8 Flash Assisted Listing & Stocking)
            if (uiState.showAiStockingDialog) {
                AiVendorStockingDialog(
                    sellers = sellers,
                    initialSellerId = uiState.aiStockingSellerId,
                    isGenerating = uiState.isGeneratingAiListing,
                    previewListing = uiState.generatedListingPreview,
                    errorMessage = uiState.aiStockingError,
                    onDismiss = { viewModel.closeAiStockingDialog() },
                    onGenerate = { vendorRaw -> viewModel.generateListingWithAi(vendorRaw) },
                    onStockProduct = { title, cat, price, stock, desc, high, badge ->
                        viewModel.stockProductToMarketplace(title, cat, price, stock, desc, high, badge)
                    }
                )
            }
        }
    }
}

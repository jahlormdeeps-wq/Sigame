package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.ai.AiProductService
import com.example.data.ai.GeneratedProductListing
import com.example.data.model.CartItem
import com.example.data.model.Category
import com.example.data.model.CustomerEnquiry
import com.example.data.model.Order
import com.example.data.model.Product
import com.example.data.model.Review
import com.example.data.model.ReviewTargetType
import com.example.data.model.Seller
import com.example.data.model.SellerApplication
import com.example.data.repository.MarketRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

enum class NavigationTab {
    HOME,
    CATEGORIES,
    SELLERS,
    ORDERS_CART
}

data class MarketUiState(
    val currentTab: NavigationTab = NavigationTab.HOME,
    val selectedProduct: Product? = null,
    val selectedSeller: Seller? = null,
    val selectedCategory: Category? = null,
    val selectedCategoryId: String? = null,
    val searchQuery: String = "",
    val selectedRegion: String = "All Ghana",
    val selectedTypeFilter: String = "All", // "All", "Products", "Services", "Top Rated 4.8+★"
    val showWriteReviewDialog: Boolean = false,
    val reviewTargetId: String? = null,
    val reviewTargetType: ReviewTargetType? = null,
    val reviewTargetTitle: String? = null,
    val showBecomeSellerDialog: Boolean = false,
    val showEnquiryDialog: Boolean = false,
    val enquirySeller: Seller? = null,
    val showCheckoutSheet: Boolean = false,
    val sellerApplicationSuccess: Boolean = false,
    // AI Stocking & Listing State
    val showAiStockingDialog: Boolean = false,
    val aiStockingSellerId: String? = null,
    val isGeneratingAiListing: Boolean = false,
    val generatedListingPreview: GeneratedProductListing? = null,
    val aiStockingError: String? = null
)

class MarketViewModel(
    private val repository: MarketRepository = MarketRepository(),
    private val aiProductService: AiProductService = AiProductService()
) : ViewModel() {

    private val _uiState = MutableStateFlow(MarketUiState())
    val uiState: StateFlow<MarketUiState> = _uiState.asStateFlow()

    val categories: List<Category> = repository.categories
    val products: StateFlow<List<Product>> = repository.products
    val sellers: StateFlow<List<Seller>> = repository.sellers
    val reviews: StateFlow<List<Review>> = repository.reviews
    val cart: StateFlow<List<CartItem>> = repository.cart
    val orders: StateFlow<List<Order>> = repository.orders
    val sellerApplications: StateFlow<List<SellerApplication>> = repository.sellerApplications

    private val _snackbarMessage = MutableSharedFlow<String>()
    val snackbarMessage: SharedFlow<String> = _snackbarMessage.asSharedFlow()

    // Filtered products based on search, region, category, and type
    val filteredProducts: StateFlow<List<Product>> = combine(
        products,
        sellers,
        uiState
    ) { allProducts, allSellers, state ->
        val sellerMap = allSellers.associateBy { it.id }
        allProducts.filter { product ->
            val matchesSearch = state.searchQuery.isBlank() ||
                    product.title.contains(state.searchQuery, ignoreCase = true) ||
                    product.description.contains(state.searchQuery, ignoreCase = true) ||
                    (sellerMap[product.sellerId]?.businessName?.contains(state.searchQuery, ignoreCase = true) == true)

            val matchesCategory = (state.selectedCategoryId == null || product.categoryId == state.selectedCategoryId) &&
                    (state.selectedCategory == null || product.categoryId == state.selectedCategory.id)

            val sellerRegion = sellerMap[product.sellerId]?.region ?: ""
            val matchesRegion = state.selectedRegion == "All Ghana" ||
                    sellerRegion.contains(state.selectedRegion.replace(" (Kumasi)", "").replace(" (Tamale)", ""), ignoreCase = true) ||
                    product.location.contains(state.selectedRegion.replace(" (Kumasi)", "").replace(" (Tamale)", ""), ignoreCase = true)

            val matchesType = when (state.selectedTypeFilter) {
                "Products" -> !product.isService
                "Services" -> product.isService
                "Top Rated 4.8+★" -> product.rating >= 4.8
                else -> true
            }

            matchesSearch && matchesCategory && matchesRegion && matchesType
        }
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun setTab(tab: NavigationTab) {
        _uiState.value = _uiState.value.copy(
            currentTab = tab,
            selectedProduct = null,
            selectedSeller = null
        )
    }

    fun setSearchQuery(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
    }

    fun setRegion(region: String) {
        _uiState.value = _uiState.value.copy(selectedRegion = region)
    }

    fun setTypeFilter(filter: String) {
        _uiState.value = _uiState.value.copy(selectedTypeFilter = filter)
    }

    fun selectCategory(category: Category?) {
        _uiState.value = _uiState.value.copy(
            selectedCategory = category,
            currentTab = if (category != null) NavigationTab.CATEGORIES else _uiState.value.currentTab
        )
    }

    fun selectCategoryFilter(categoryId: String?) {
        _uiState.value = _uiState.value.copy(
            selectedCategoryId = if (_uiState.value.selectedCategoryId == categoryId) null else categoryId
        )
    }

    fun selectProduct(product: Product?) {
        _uiState.value = _uiState.value.copy(selectedProduct = product)
    }

    fun selectSeller(seller: Seller?) {
        _uiState.value = _uiState.value.copy(selectedSeller = seller)
    }

    fun selectSellerById(sellerId: String) {
        val seller = sellers.value.firstOrNull { it.id == sellerId }
        _uiState.value = _uiState.value.copy(selectedSeller = seller)
    }

    fun openWriteReviewDialog(targetId: String, targetType: ReviewTargetType, targetTitle: String) {
        _uiState.value = _uiState.value.copy(
            showWriteReviewDialog = true,
            reviewTargetId = targetId,
            reviewTargetType = targetType,
            reviewTargetTitle = targetTitle
        )
    }

    fun closeWriteReviewDialog() {
        _uiState.value = _uiState.value.copy(
            showWriteReviewDialog = false,
            reviewTargetId = null,
            reviewTargetType = null,
            reviewTargetTitle = null
        )
    }

    fun submitReview(
        authorName: String,
        rating: Int,
        comment: String
    ) {
        val targetId = _uiState.value.reviewTargetId ?: return
        val targetType = _uiState.value.reviewTargetType ?: return

        val isVerified = if (targetType == ReviewTargetType.PRODUCT) {
            repository.hasPurchasedProduct(targetId)
        } else {
            repository.hasPurchasedFromSeller(targetId)
        }

        val newReview = Review(
            id = UUID.randomUUID().toString(),
            targetId = targetId,
            targetType = targetType,
            authorName = if (authorName.isBlank()) "Verified Customer" else authorName,
            rating = rating.coerceIn(1, 5),
            comment = comment.trim(),
            date = "Oct 2026",
            isVerifiedPurchase = isVerified,
            helpfulCount = 0
        )

        viewModelScope.launch {
            repository.addReview(newReview)
        }

        if (targetType == ReviewTargetType.PRODUCT) {
            val updated = repository.products.value.firstOrNull { it.id == targetId }
            _uiState.value = _uiState.value.copy(selectedProduct = updated)
        } else {
            val updated = repository.sellers.value.firstOrNull { it.id == targetId }
            _uiState.value = _uiState.value.copy(selectedSeller = updated)
        }

        closeWriteReviewDialog()
        showNotification("Review & rating submitted! Thank you for supporting Ghanaian sellers 🇬🇭")
    }

    fun addToCart(product: Product, quantity: Int = 1) {
        repository.addToCart(product, quantity)
        showNotification("Added ${product.title.take(24)}... to cart (GH₵ ${String.format("%.2f", product.priceGhs * quantity)})")
    }

    fun updateCartQuantity(productId: String, quantity: Int) {
        repository.updateCartQuantity(productId, quantity)
    }

    fun removeFromCart(productId: String) {
        repository.removeFromCart(productId)
    }

    fun openCheckout() {
        _uiState.value = _uiState.value.copy(showCheckoutSheet = true)
    }

    fun closeCheckout() {
        _uiState.value = _uiState.value.copy(showCheckoutSheet = false)
    }

    fun placeOrder(
        customerName: String,
        customerPhone: String,
        deliveryAddress: String,
        paymentMethod: String
    ) {
        viewModelScope.launch {
            val order = repository.placeOrder(
                customerName = customerName,
                customerPhone = customerPhone,
                deliveryAddress = deliveryAddress,
                paymentMethod = paymentMethod
            )
            closeCheckout()
            if (order != null) {
                showNotification("Order ${order.orderNumber} placed! You can now rate your purchase in My Orders 🇬🇭")
                _uiState.value = _uiState.value.copy(currentTab = NavigationTab.ORDERS_CART)
            }
        }
    }

    fun openBecomeSellerDialog() {
        _uiState.value = _uiState.value.copy(showBecomeSellerDialog = true, sellerApplicationSuccess = false)
    }

    fun closeBecomeSellerDialog() {
        _uiState.value = _uiState.value.copy(showBecomeSellerDialog = false)
    }

    fun submitSellerApplication(
        businessName: String,
        ownerName: String,
        category: String,
        region: String,
        phone: String,
        email: String,
        description: String
    ) {
        val app = SellerApplication(
            id = UUID.randomUUID().toString(),
            businessName = businessName,
            ownerName = ownerName,
            category = category,
            region = region,
            phone = phone,
            email = email,
            description = description,
            status = "Submitted"
        )
        viewModelScope.launch {
            repository.submitSellerApplication(app)
            _uiState.value = _uiState.value.copy(sellerApplicationSuccess = true)
            showNotification("Welcome to Asigame! Seller application received. We'll contact you within 24 hours.")
        }
    }

    fun openEnquiryDialog(seller: Seller) {
        _uiState.value = _uiState.value.copy(
            showEnquiryDialog = true,
            enquirySeller = seller
        )
    }

    fun closeEnquiryDialog() {
        _uiState.value = _uiState.value.copy(
            showEnquiryDialog = false,
            enquirySeller = null
        )
    }

    fun submitEnquiry(
        customerName: String,
        customerPhone: String,
        subject: String,
        message: String
    ) {
        val seller = _uiState.value.enquirySeller ?: return
        val enquiry = CustomerEnquiry(
            id = UUID.randomUUID().toString(),
            sellerId = seller.id,
            customerName = customerName,
            customerPhone = customerPhone,
            subject = subject,
            message = message,
            timestamp = "Just now"
        )
        viewModelScope.launch {
            repository.submitCustomerEnquiry(enquiry)
            closeEnquiryDialog()
            showNotification("Enquiry sent to ${seller.businessName}! They will respond on WhatsApp/phone.")
        }
    }

    // AI Assisted Product Listing & Online Vendor Stocking
    fun openAiStockingDialog(targetSellerId: String? = null) {
        val sellerId = targetSellerId ?: sellers.value.firstOrNull()?.id ?: "seller_tech"
        _uiState.value = _uiState.value.copy(
            showAiStockingDialog = true,
            aiStockingSellerId = sellerId,
            isGeneratingAiListing = false,
            generatedListingPreview = null,
            aiStockingError = null
        )
    }

    fun closeAiStockingDialog() {
        _uiState.value = _uiState.value.copy(
            showAiStockingDialog = false,
            isGeneratingAiListing = false,
            generatedListingPreview = null,
            aiStockingError = null
        )
    }

    fun generateListingWithAi(vendorRawInput: String) {
        val sellerId = _uiState.value.aiStockingSellerId ?: sellers.value.first().id
        val seller = getSeller(sellerId) ?: sellers.value.first()

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isGeneratingAiListing = true,
                aiStockingError = null
            )

            val result = aiProductService.generateListingFromVendor(
                vendorInput = vendorRawInput,
                vendorName = seller.businessName,
                sellerRegion = seller.region
            )

            result.onSuccess { listing ->
                _uiState.value = _uiState.value.copy(
                    isGeneratingAiListing = false,
                    generatedListingPreview = listing
                )
            }.onFailure { err ->
                _uiState.value = _uiState.value.copy(
                    isGeneratingAiListing = false,
                    aiStockingError = err.localizedMessage ?: "Failed to generate AI listing."
                )
            }
        }
    }

    fun stockProductToMarketplace(
        title: String,
        categoryId: String,
        priceGhs: Double,
        stockCount: Int,
        description: String,
        highlights: List<String>,
        badge: String
    ) {
        val sellerId = _uiState.value.aiStockingSellerId ?: sellers.value.first().id
        val seller = getSeller(sellerId) ?: sellers.value.first()

        val newProduct = Product(
            id = "ai_prod_" + UUID.randomUUID().toString().take(8),
            sellerId = seller.id,
            title = title,
            description = description,
            categoryId = categoryId,
            priceGhs = priceGhs,
            isService = false,
            rating = 5.0, // Brand new vendor listing initialized
            reviewCount = 1,
            inStock = stockCount > 0,
            location = seller.region,
            highlights = highlights,
            badge = badge.ifBlank { "Vendor Stocked" }
        )

        viewModelScope.launch {
            repository.stockNewProduct(newProduct)
            closeAiStockingDialog()
            showNotification("Stocked '${newProduct.title.take(24)}...' from online vendor into ${seller.businessName}!")

            // Open newly stocked product directly
            _uiState.value = _uiState.value.copy(
                selectedProduct = newProduct,
                currentTab = NavigationTab.HOME
            )
        }
    }

    fun getSeller(sellerId: String): Seller? {
        return sellers.value.firstOrNull { it.id == sellerId }
    }

    fun getReviewsFor(targetId: String): List<Review> {
        return repository.getReviewsForTarget(targetId)
    }

    fun hasPurchased(productId: String): Boolean {
        return repository.hasPurchasedProduct(productId)
    }

    fun showNotification(message: String) {
        viewModelScope.launch {
            _snackbarMessage.emit(message)
        }
    }

    fun handleBack(): Boolean {
        val state = _uiState.value
        return when {
            state.showAiStockingDialog -> {
                closeAiStockingDialog()
                true
            }
            state.showWriteReviewDialog -> {
                closeWriteReviewDialog()
                true
            }
            state.showBecomeSellerDialog -> {
                closeBecomeSellerDialog()
                true
            }
            state.showEnquiryDialog -> {
                closeEnquiryDialog()
                true
            }
            state.showCheckoutSheet -> {
                closeCheckoutSheet()
                true
            }
            state.selectedProduct != null -> {
                _uiState.value = state.copy(selectedProduct = null)
                true
            }
            state.selectedSeller != null -> {
                _uiState.value = state.copy(selectedSeller = null)
                true
            }
            state.selectedCategory != null -> {
                _uiState.value = state.copy(selectedCategory = null)
                true
            }
            state.selectedCategoryId != null -> {
                _uiState.value = state.copy(selectedCategoryId = null)
                true
            }
            state.currentTab != NavigationTab.HOME -> {
                _uiState.value = state.copy(currentTab = NavigationTab.HOME)
                true
            }
            else -> false
        }
    }

    private fun closeCheckoutSheet() {
        _uiState.value = _uiState.value.copy(showCheckoutSheet = false)
    }
}

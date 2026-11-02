package com.example.data.model

data class Category(
    val id: String,
    val title: String,
    val subtitle: String,
    val iconName: String,
    val itemCount: Int
)

data class Seller(
    val id: String,
    val businessName: String,
    val ownerName: String,
    val category: String,
    val region: String,
    val phone: String,
    val whatsapp: String,
    val email: String,
    val verified: Boolean,
    val rating: Double,
    val reviewCount: Int,
    val bio: String,
    val specialties: List<String>,
    val badge: String = "Verified Merchant"
)

data class Product(
    val id: String,
    val sellerId: String,
    val title: String,
    val description: String,
    val categoryId: String,
    val priceGhs: Double,
    val isService: Boolean,
    val rating: Double,
    val reviewCount: Int,
    val inStock: Boolean = true,
    val location: String,
    val highlights: List<String> = emptyList(),
    val badge: String = ""
)

enum class ReviewTargetType {
    PRODUCT,
    SELLER
}

data class Review(
    val id: String,
    val targetId: String,
    val targetType: ReviewTargetType,
    val authorName: String,
    val rating: Int, // 1 to 5
    val comment: String,
    val date: String,
    val isVerifiedPurchase: Boolean = true,
    val helpfulCount: Int = 0
)

data class CartItem(
    val product: Product,
    val quantity: Int = 1
)

enum class OrderStatus {
    COMPLETED,
    IN_TRANSIT,
    PROCESSING
}

data class Order(
    val id: String,
    val orderNumber: String,
    val date: String,
    val items: List<CartItem>,
    val totalGhs: Double,
    val customerName: String,
    val customerPhone: String,
    val deliveryAddress: String,
    val paymentMethod: String,
    val status: OrderStatus = OrderStatus.COMPLETED
)

data class SellerApplication(
    val id: String,
    val businessName: String,
    val ownerName: String,
    val category: String,
    val region: String,
    val phone: String,
    val email: String,
    val description: String,
    val status: String = "Submitted"
)

data class CustomerEnquiry(
    val id: String,
    val sellerId: String,
    val customerName: String,
    val customerPhone: String,
    val subject: String,
    val message: String,
    val timestamp: String
)

package com.example.data.repository

import android.content.Context
import com.example.R
import com.example.data.model.CartItem
import com.example.data.model.Category
import com.example.data.model.CustomerEnquiry
import com.example.data.model.Order
import com.example.data.model.OrderStatus
import com.example.data.model.Product
import com.example.data.model.Review
import com.example.data.model.ReviewTargetType
import com.example.data.model.Seller
import com.example.data.model.SellerApplication
import com.example.util.OperationType
import com.example.util.handleFirestoreError
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import java.util.UUID

class MarketRepository(val db: FirebaseFirestore? = null) {

    constructor(context: Context) : this(
        FirebaseFirestore.getInstance(
            context.applicationContext.getString(R.string.firestore_database_id)
        )
    )

    private val auth get() = Firebase.auth

    fun requireUserId(): String {
        return auth.currentUser?.uid
            ?: throw IllegalStateException("User must be signed in with Google before accessing Firestore.")
    }

    val categories: List<Category> = listOf(
        Category(
            id = "fashion_beauty",
            title = "Fashion & Beauty",
            subtitle = "Clothing, shoes, bags & cosmetics",
            iconName = "Checkroom",
            itemCount = 28
        ),
        Category(
            id = "phones_electronics",
            title = "Phones & Electronics",
            subtitle = "Smartphones, gadgets & computers",
            iconName = "Smartphone",
            itemCount = 35
        ),
        Category(
            id = "home_living",
            title = "Home & Living",
            subtitle = "Furniture, decor & kitchen essentials",
            iconName = "Home",
            itemCount = 22
        ),
        Category(
            id = "food_groceries",
            title = "Food & Groceries",
            subtitle = "Fresh produce, spices & packaged goods",
            iconName = "Restaurant",
            itemCount = 40
        ),
        Category(
            id = "music_entertainment",
            title = "Music & Entertainment",
            subtitle = "Instruments, beats & cultural works",
            iconName = "MusicNote",
            itemCount = 16
        ),
        Category(
            id = "digital_products",
            title = "Digital Products",
            subtitle = "E-books, templates & creative tools",
            iconName = "Description",
            itemCount = 19
        ),
        Category(
            id = "art_handmade",
            title = "Art & Handmade",
            subtitle = "Wood carvings, pottery & beadwork",
            iconName = "Palette",
            itemCount = 24
        ),
        Category(
            id = "business_services",
            title = "Business Services",
            subtitle = "Design, advertising, repairs & consulting",
            iconName = "Work",
            itemCount = 18
        ),
        Category(
            id = "education_learning",
            title = "Education & Learning",
            subtitle = "Training courses, guides & tutoring",
            iconName = "School",
            itemCount = 15
        )
    )

    private val initialSellers = listOf(
        Seller(
            id = "seller_kente",
            businessName = "Akroma Kente Heritage",
            ownerName = "Nana Akwasi Mensah",
            category = "Fashion & Beauty",
            region = "Ashanti (Bonwire)",
            phone = "+233 24 411 2233",
            whatsapp = "+233 24 411 2233",
            email = "info@akromakente.gh",
            verified = true,
            rating = 4.9,
            reviewCount = 38,
            bio = "Authentic royal Bonwire kente weavers preserving 4 generations of Ghanaian weaving excellence. Custom orders for weddings, festivals, and international shipping.",
            specialties = listOf("Handwoven Kente", "Ceremonial Stoles", "Custom Weaving"),
            badge = "Master Artisan"
        ),
        Seller(
            id = "seller_tech",
            businessName = "GoldCoast Tech Hub",
            ownerName = "Kwame Asante",
            category = "Phones & Electronics",
            region = "Greater Accra (Circle)",
            phone = "+233 20 890 4455",
            whatsapp = "+233 20 890 4455",
            email = "sales@goldcoasttech.com",
            verified = true,
            rating = 4.7,
            reviewCount = 52,
            bio = "Accra's premier hub for genuine smartphones, power backup solutions, and tested gadgets with official warranty and nationwide doorstep delivery.",
            specialties = listOf("Smartphones", "Solar Generators", "Accessories"),
            badge = "Verified Tech Dealer"
        ),
        Seller(
            id = "seller_food",
            businessName = "Ama's Fresh Kitchen & Spices",
            ownerName = "Ama Osei Tutu",
            category = "Food & Groceries",
            region = "Greater Accra (Makola)",
            phone = "+233 55 332 9988",
            whatsapp = "+233 55 332 9988",
            email = "orders@amafreshkitchen.com",
            verified = true,
            rating = 4.8,
            reviewCount = 44,
            bio = "Bringing the authentic flavors of Ghana to your kitchen. Artisanal black pepper shito, pure cocoa products, and freshly packed farm spices.",
            specialties = listOf("Artisanal Shito", "Ghana Cocoa Powder", "Organic Spices"),
            badge = "Top Food Seller"
        ),
        Seller(
            id = "seller_craft",
            businessName = "Volta Craft Collective",
            ownerName = "Mawuli Adzaho",
            category = "Art & Handmade",
            region = "Volta (Ho)",
            phone = "+233 24 670 1234",
            whatsapp = "+233 24 670 1234",
            email = "contact@voltacrafts.org",
            verified = true,
            rating = 4.9,
            reviewCount = 29,
            bio = "Community cooperative of over 30 female weavers and wood sculptors in the Volta and Upper East regions creating heirloom Bolga baskets and Adinkra carvings.",
            specialties = listOf("Bolgatanga Baskets", "Adinkra Wood Carvings", "Recycled Glass Beads"),
            badge = "Fair Trade Certified"
        ),
        Seller(
            id = "seller_agency",
            businessName = "Kojo Creative Studios",
            ownerName = "Kojo Boateng",
            category = "Business Services",
            region = "Greater Accra (East Legon)",
            phone = "+233 26 555 8899",
            whatsapp = "+233 26 555 8899",
            email = "hello@kojocreative.design",
            verified = true,
            rating = 4.8,
            reviewCount = 21,
            bio = "Helping Ghanaian SMEs and startups build world-class brands with logo design, business registration collateral, social media management, and web design.",
            specialties = listOf("Brand Identity", "Social Media Marketing", "Packaging Design"),
            badge = "Creative Agency"
        ),
        Seller(
            id = "seller_edu",
            businessName = "Sankofa Learning Academy",
            ownerName = "Dr. Kofi Nyarko",
            category = "Education & Learning",
            region = "Ashanti (Kumasi)",
            phone = "+233 27 778 9900",
            whatsapp = "+233 27 778 9900",
            email = "admissions@sankofa-academy.gh",
            verified = true,
            rating = 4.9,
            reviewCount = 33,
            bio = "Preserving Ghanaian heritage and empowering youth through Twi & Fante language courses, coding bootcamps, and entrepreneurship masterclasses.",
            specialties = listOf("Twi Language Course", "Coding for Teens", "Export Masterclass"),
            badge = "Certified Educator"
        )
    )

    private val initialProducts = listOf(
        Product(
            id = "prod_kente_ceremonial",
            sellerId = "seller_kente",
            title = "Original Bonwire Handwoven Kente (3-Piece Cloth)",
            description = "Handwoven authentic Ghanaian silk and cotton kente cloth featuring royal 'Adwiniasa' patterns. Perfect for weddings, graduations, and state ceremonies.",
            categoryId = "fashion_beauty",
            priceGhs = 1450.0,
            isService = false,
            rating = 4.9,
            reviewCount = 18,
            inStock = true,
            location = "Bonwire, Ashanti",
            highlights = listOf("100% Handwoven Silk & Cotton", "Includes 3 Full Pieces", "Certificate of Authenticity"),
            badge = "Authentic Heritage"
        ),
        Product(
            id = "prod_shea_butter",
            sellerId = "seller_kente",
            title = "Northern Ghana Raw Organic Shea Butter & Cocoa Oil",
            description = "Unrefined, grade-A pure shea butter whipped with natural Ghanaian cocoa butter for deep skin nourishment and natural glowing complexion.",
            categoryId = "fashion_beauty",
            priceGhs = 85.0,
            isService = false,
            rating = 4.8,
            reviewCount = 26,
            inStock = true,
            location = "Accra & Tamale",
            highlights = listOf("100% Natural & Vegan", "Rich in Vitamins A & E", "500ml Glass Jar"),
            badge = "Best Seller"
        ),
        Product(
            id = "prod_tecno_phone",
            sellerId = "seller_tech",
            title = "Tecno Camon 30 Pro 5G (12GB RAM, 512GB)",
            description = "High-performance smartphone with Sony 50MP OIS camera, 70W ultra-fast flash charging, and dual 5G SIM capability. 1-year official Carlcare warranty included.",
            categoryId = "phones_electronics",
            priceGhs = 3850.0,
            isService = false,
            rating = 4.7,
            reviewCount = 22,
            inStock = true,
            location = "Circle, Accra",
            highlights = listOf("1-Year Carlcare Warranty", "Dual SIM 5G", "70W Fast Charger in Box"),
            badge = "Official Warranty"
        ),
        Product(
            id = "prod_solar_backup",
            sellerId = "seller_tech",
            title = "Asigame Solar Power Generator 1200W (Dumsor Backup)",
            description = "Reliable lithium portable power station for uninterrupted power during grid cuts. Powers TVs, laptops, fans, WiFi routers, and phones for up to 14 hours.",
            categoryId = "phones_electronics",
            priceGhs = 4900.0,
            isService = false,
            rating = 4.9,
            reviewCount = 19,
            inStock = true,
            location = "Circle, Accra",
            highlights = listOf("1200W Pure Sine Wave", "Solar Panel Compatible", "Fast 2-hour AC Recharge"),
            badge = "Popular Choice"
        ),
        Product(
            id = "prod_shito_sauce",
            sellerId = "seller_food",
            title = "Ama's Hot Smoked Fish & Shrimp Shito (450g Trio Pack)",
            description = "Traditional slow-cooked Ghanaian black pepper sauce made with smoked herring, dried shrimp, ginger, garlic, and hot chili. Ready to eat with rice, waakye, or kenkey.",
            categoryId = "food_groceries",
            priceGhs = 120.0,
            isService = false,
            rating = 5.0,
            reviewCount = 31,
            inStock = true,
            location = "Makola, Accra",
            highlights = listOf("3 Jars (Mild, Medium, Extra Hot)", "No Artificial Preservatives", "Sealed Freshness"),
            badge = "Customer Favorite"
        ),
        Product(
            id = "prod_cocoa_powder",
            sellerId = "seller_food",
            title = "Golden Harvest 100% Pure Natural Ghanaian Cocoa Powder",
            description = "Unsweetened, rich antioxidant-packed Ghanaian cocoa powder milled from premium single-origin beans. Perfect for healthy hot beverages and baking.",
            categoryId = "food_groceries",
            priceGhs = 95.0,
            isService = false,
            rating = 4.8,
            reviewCount = 15,
            inStock = true,
            location = "Tema / Makola",
            highlights = listOf("1kg Resealable Pouch", "High Flavonoid Content", "100% Single Origin"),
            badge = "Organic"
        ),
        Product(
            id = "prod_bolga_basket",
            sellerId = "seller_craft",
            title = "Bolgatanga Handwoven Elephant Grass Baskets (Set of 2)",
            description = "Eco-friendly, resilient handwoven shopping and home decor baskets crafted with wild savannah elephant grass and durable leather-wrapped handles.",
            categoryId = "home_living",
            priceGhs = 280.0,
            isService = false,
            rating = 4.9,
            reviewCount = 14,
            inStock = true,
            location = "Bolgatanga / Ho",
            highlights = listOf("Genuine Goatskin Leather Trim", "Washable & Durable", "Direct Fair-Trade Artisan"),
            badge = "Handmade in Ghana"
        ),
        Product(
            id = "prod_gye_nyame",
            sellerId = "seller_craft",
            title = "Gye Nyame Hand-Carved Royal Mahogany Wall Plaque",
            description = "Masterfully chiseled Adinkra symbol representing the supremacy and omnipotence of God ('Except God'). Finished in warm mahogany polish.",
            categoryId = "art_handmade",
            priceGhs = 320.0,
            isService = false,
            rating = 4.9,
            reviewCount = 11,
            inStock = true,
            location = "Kumasi / Ho",
            highlights = listOf("Solid Reclaimed Mahogany", "16 x 12 Inches", "Ready to Hang Hook"),
            badge = "Artisan Crafted"
        ),
        Product(
            id = "prod_branding_service",
            sellerId = "seller_agency",
            title = "Complete Brand Identity & Logo Design for SMEs",
            description = "Professional branding package including 3 unique logo concepts, brand style guide, business card layout, social media banners, and print-ready files.",
            categoryId = "business_services",
            priceGhs = 1200.0,
            isService = true,
            rating = 4.8,
            reviewCount = 9,
            inStock = true,
            location = "Accra (Nationwide Remote)",
            highlights = listOf("Turnaround in 5 Days", "Unlimited Revisions", "Full Commercial Rights"),
            badge = "Top Service"
        ),
        Product(
            id = "prod_invoice_template",
            sellerId = "seller_agency",
            title = "Ghana SME Invoicing & VAT/NHIL Bookkeeping Kit",
            description = "Ready-to-use digital Excel & Google Sheets template customized with Ghana GRA VAT, NHIL, GETFund, and COVID levy calculations with automatic receipt generation.",
            categoryId = "digital_products",
            priceGhs = 150.0,
            isService = false,
            rating = 4.7,
            reviewCount = 12,
            inStock = true,
            location = "Instant Digital Download",
            highlights = listOf("Instant Download Link", "Compliant with GRA Tax Rates", "Video Tutorial Included"),
            badge = "Instant Access"
        ),
        Product(
            id = "prod_twi_course",
            sellerId = "seller_edu",
            title = "Twi & Fante Spoken Language Mastery (Audiobook & PDF)",
            description = "Comprehensive beginner-to-advanced audio course teaching everyday conversation, market bargaining expressions, business etiquette, and cultural greetings.",
            categoryId = "education_learning",
            priceGhs = 220.0,
            isService = false,
            rating = 4.9,
            reviewCount = 17,
            inStock = true,
            location = "Online / Kumasi",
            highlights = listOf("24 Audio Lessons", "Market Slang & Proverbs", "Lifetime Access"),
            badge = "Top Rated"
        ),
        Product(
            id = "prod_djembe_drum",
            sellerId = "seller_craft",
            title = "Traditional Ghanaian Handcrafted Tweneboa Wood Djembe Drum",
            description = "Carved from acoustic tweneboa cedar wood, mounted with goat hide parchment and braided nylon tuning cords. Tuned by Accra master drummers.",
            categoryId = "music_entertainment",
            priceGhs = 650.0,
            isService = false,
            rating = 4.8,
            reviewCount = 8,
            inStock = true,
            location = "Arts Centre, Accra",
            highlights = listOf("Tweneboa Cedar Wood", "Natural Goat Parchment", "Deep Bass Resonance"),
            badge = "Master Tuned"
        )
    )

    private val initialReviews = listOf(
        Review(
            id = "rev_1",
            targetId = "prod_kente_ceremonial",
            targetType = ReviewTargetType.PRODUCT,
            authorName = "Kwabena Mensah",
            rating = 5,
            comment = "Outstanding craftsmanship! The weight of the silk is heavy and genuine Bonwire weave. Wore it for my traditional marriage in Kumasi and received countless compliments.",
            date = "Oct 18, 2026",
            isVerifiedPurchase = true,
            helpfulCount = 12
        ),
        Review(
            id = "rev_2",
            targetId = "prod_kente_ceremonial",
            targetType = ReviewTargetType.PRODUCT,
            authorName = "Abena Serwaa Boateng",
            rating = 5,
            comment = "Delivered safely to Takoradi within 48 hours. Beautiful colors, crisp edges, and smells authentic. Worth every Ghana cedi!",
            date = "Oct 12, 2026",
            isVerifiedPurchase = true,
            helpfulCount = 7
        ),
        Review(
            id = "rev_3",
            targetId = "seller_kente",
            targetType = ReviewTargetType.SELLER,
            authorName = "Kwabena Mensah",
            rating = 5,
            comment = "Nana Akwasi was very communicative on WhatsApp throughout the custom weaving process. Excellent customer service from a true heritage seller.",
            date = "Oct 18, 2026",
            isVerifiedPurchase = true,
            helpfulCount = 9
        ),
        Review(
            id = "rev_4",
            targetId = "prod_shito_sauce",
            targetType = ReviewTargetType.PRODUCT,
            authorName = "Kofi Asante",
            rating = 5,
            comment = "This is the best shito I have tasted since my grandma's kitchen in Cape Coast. The smoked fish bits are chunky and the heat is perfectly balanced.",
            date = "Oct 15, 2026",
            isVerifiedPurchase = true,
            helpfulCount = 14
        ),
        Review(
            id = "rev_5",
            targetId = "seller_food",
            targetType = ReviewTargetType.SELLER,
            authorName = "Esi Amponsah",
            rating = 5,
            comment = "Packaging was vacuum sealed and tamper-evident. Ama was sweet and threw in a complimentary sachet of dried ginger flakes. Will order regularly!",
            date = "Oct 10, 2026",
            isVerifiedPurchase = true,
            helpfulCount = 6
        ),
        Review(
            id = "rev_6",
            targetId = "prod_solar_backup",
            targetType = ReviewTargetType.PRODUCT,
            authorName = "Yaw Osei Prempeh",
            rating = 5,
            comment = "Saves my home office during ECG light-outs! Ran my 55 inch TV, WiFi router, and 2 standing fans for 8 hours straight without a hiccup.",
            date = "Oct 05, 2026",
            isVerifiedPurchase = true,
            helpfulCount = 18
        ),
        Review(
            id = "rev_7",
            targetId = "seller_tech",
            targetType = ReviewTargetType.SELLER,
            authorName = "Yaw Osei Prempeh",
            rating = 5,
            comment = "Kwame from GoldCoast Tech came and tested the unit before dispatching rider. Legit warranty card included.",
            date = "Oct 05, 2026",
            isVerifiedPurchase = true,
            helpfulCount = 11
        ),
        Review(
            id = "rev_8",
            targetId = "prod_bolga_basket",
            targetType = ReviewTargetType.PRODUCT,
            authorName = "Akosua Dede",
            rating = 5,
            comment = "The weaving is so tight and strong. The leather handles feel smooth and sturdy. Excellent representation of Northern Ghanaian craftsmanship.",
            date = "Sep 28, 2026",
            isVerifiedPurchase = true,
            helpfulCount = 8
        ),
        Review(
            id = "rev_9",
            targetId = "prod_branding_service",
            targetType = ReviewTargetType.PRODUCT,
            authorName = "Fiifi Quayson",
            rating = 5,
            comment = "Kojo Studios transformed our pharmacy branding! Delivered 3 solid variations ahead of schedule. Highly recommended for any Ghanaian startup.",
            date = "Sep 20, 2026",
            isVerifiedPurchase = true,
            helpfulCount = 5
        )
    )

    private val initialOrders = listOf(
        Order(
            id = "order_seeded_1",
            orderNumber = "ASI-GH-9824",
            date = "Oct 20, 2026",
            items = listOf(
                CartItem(
                    product = initialProducts[4],
                    quantity = 1
                ),
                CartItem(
                    product = initialProducts[1],
                    quantity = 2
                )
            ),
            totalGhs = 290.0,
            customerName = "Akwasi Owusu",
            customerPhone = "+233 24 100 2000",
            deliveryAddress = "House 14, Ring Road Central, Kokomlemle, Accra",
            paymentMethod = "MTN Mobile Money (MoMo)",
            status = OrderStatus.COMPLETED
        )
    )

    private val _products = MutableStateFlow(initialProducts)
    val products: StateFlow<List<Product>> = _products.asStateFlow()

    private val _sellers = MutableStateFlow(initialSellers)
    val sellers: StateFlow<List<Seller>> = _sellers.asStateFlow()

    private val _reviews = MutableStateFlow(initialReviews)
    val reviews: StateFlow<List<Review>> = _reviews.asStateFlow()

    private val _cart = MutableStateFlow<List<CartItem>>(emptyList())
    val cart: StateFlow<List<CartItem>> = _cart.asStateFlow()

    private val _orders = MutableStateFlow(initialOrders)
    val orders: StateFlow<List<Order>> = _orders.asStateFlow()

    private val _sellerApplications = MutableStateFlow<List<SellerApplication>>(emptyList())
    val sellerApplications: StateFlow<List<SellerApplication>> = _sellerApplications.asStateFlow()

    private val _customerEnquiries = MutableStateFlow<List<CustomerEnquiry>>(emptyList())
    val customerEnquiries: StateFlow<List<CustomerEnquiry>> = _customerEnquiries.asStateFlow()

    fun getReviewsForTarget(targetId: String): List<Review> {
        return _reviews.value.filter { it.targetId == targetId }
    }

    fun hasPurchasedProduct(productId: String): Boolean {
        return _orders.value.any { order ->
            order.status == OrderStatus.COMPLETED && order.items.any { it.product.id == productId }
        }
    }

    fun hasPurchasedFromSeller(sellerId: String): Boolean {
        return _orders.value.any { order ->
            order.status == OrderStatus.COMPLETED && order.items.any { it.product.sellerId == sellerId }
        }
    }

    suspend fun addReview(review: Review): Result<String> {
        val updatedReviews = listOf(review) + _reviews.value
        _reviews.value = updatedReviews

        if (review.targetType == ReviewTargetType.PRODUCT) {
            _products.value = _products.value.map { prod ->
                if (prod.id == review.targetId) {
                    val newCount = prod.reviewCount + 1
                    val newAvg = Math.round(((prod.rating * prod.reviewCount + review.rating) / newCount) * 10.0) / 10.0
                    prod.copy(rating = newAvg, reviewCount = newCount)
                } else prod
            }
        } else {
            _sellers.value = _sellers.value.map { seller ->
                if (seller.id == review.targetId) {
                    val newCount = seller.reviewCount + 1
                    val newAvg = Math.round(((seller.rating * seller.reviewCount + review.rating) / newCount) * 10.0) / 10.0
                    seller.copy(rating = newAvg, reviewCount = newCount)
                } else seller
            }
        }

        if (db != null && auth.currentUser != null) {
            val uid = requireUserId()
            val payload = mapOf(
                "id" to review.id,
                "targetId" to review.targetId,
                "targetType" to review.targetType.name,
                "authorUid" to uid,
                "authorName" to review.authorName,
                "rating" to review.rating,
                "comment" to review.comment,
                "date" to review.date,
                "isVerifiedPurchase" to review.isVerifiedPurchase,
                "helpfulCount" to review.helpfulCount,
                "createdAt" to FieldValue.serverTimestamp()
            )
            val docRef = db.collection("reviews").document(review.id)
            try {
                docRef.set(payload).await()
                return Result.success(review.id)
            } catch (e: Exception) {
                handleFirestoreError(e, OperationType.CREATE, docRef.path)
                return Result.failure(e)
            }
        }
        return Result.success(review.id)
    }

    fun addToCart(product: Product, quantity: Int = 1) {
        val current = _cart.value.toMutableList()
        val index = current.indexOfFirst { it.product.id == product.id }
        if (index >= 0) {
            val existing = current[index]
            current[index] = existing.copy(quantity = existing.quantity + quantity)
        } else {
            current.add(CartItem(product = product, quantity = quantity))
        }
        _cart.value = current
    }

    fun updateCartQuantity(productId: String, quantity: Int) {
        if (quantity <= 0) {
            removeFromCart(productId)
            return
        }
        _cart.value = _cart.value.map {
            if (it.product.id == productId) it.copy(quantity = quantity) else it
        }
    }

    fun removeFromCart(productId: String) {
        _cart.value = _cart.value.filterNot { it.product.id == productId }
    }

    fun clearCart() {
        _cart.value = emptyList()
    }

    suspend fun placeOrder(
        customerName: String,
        customerPhone: String,
        deliveryAddress: String,
        paymentMethod: String
    ): Order? {
        val currentCart = _cart.value
        if (currentCart.isEmpty()) return null

        val totalGhs = currentCart.sumOf { it.product.priceGhs * it.quantity }
        val order = Order(
            id = "order_" + UUID.randomUUID().toString().replace("-", "").take(16),
            orderNumber = "ASI-GH-" + (1000..9999).random(),
            date = "Just now",
            items = currentCart,
            totalGhs = totalGhs,
            customerName = customerName,
            customerPhone = customerPhone,
            deliveryAddress = deliveryAddress,
            paymentMethod = paymentMethod,
            status = OrderStatus.COMPLETED
        )

        _orders.value = listOf(order) + _orders.value
        _cart.value = emptyList()

        if (db != null && auth.currentUser != null) {
            val uid = requireUserId()
            val payload = mapOf(
                "id" to order.id,
                "orderNumber" to order.orderNumber,
                "customerUid" to uid,
                "customerName" to order.customerName,
                "customerPhone" to order.customerPhone,
                "deliveryAddress" to order.deliveryAddress,
                "paymentMethod" to order.paymentMethod,
                "totalGhs" to order.totalGhs,
                "status" to order.status.name,
                "date" to order.date,
                "createdAt" to FieldValue.serverTimestamp()
            )
            val docRef = db.collection("orders").document(order.id)
            try {
                docRef.set(payload).await()
            } catch (e: Exception) {
                handleFirestoreError(e, OperationType.CREATE, docRef.path)
            }
        }
        return order
    }

    // Direct Firestore order creation for rule testing and SDK interactions
    suspend fun createFirestoreOrder(
        orderNumber: String,
        customerName: String,
        customerPhone: String,
        totalGhs: Double
    ): Result<String> {
        val database = checkNotNull(db) { "Firestore instance is required" }
        val uid = requireUserId()
        val orderId = "order_" + UUID.randomUUID().toString().replace("-", "").take(16)
        val payload = mapOf(
            "id" to orderId,
            "orderNumber" to orderNumber,
            "customerUid" to uid,
            "customerName" to customerName,
            "customerPhone" to customerPhone,
            "totalGhs" to totalGhs,
            "status" to "COMPLETED",
            "date" to "Just now",
            "createdAt" to FieldValue.serverTimestamp()
        )
        val docRef = database.collection("orders").document(orderId)
        return try {
            docRef.set(payload).await()
            Result.success(orderId)
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, docRef.path)
            Result.failure(e)
        }
    }

    suspend fun getUserOrders(): Result<List<Map<String, Any>>> {
        val database = checkNotNull(db) { "Firestore instance is required" }
        val uid = requireUserId()
        val collectionPath = "orders"
        return try {
            val querySnapshot = database.collection(collectionPath)
                .whereEqualTo("customerUid", uid)
                .get()
                .await()
            Result.success(querySnapshot.documents.map { it.data ?: emptyMap() })
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.LIST, collectionPath)
            Result.failure(e)
        }
    }

    fun observeOrders(): Flow<List<Map<String, Any>>> = flow {
        val database = checkNotNull(db) { "Firestore instance is required" }
        val uid = requireUserId()
        val path = "orders"
        emitAll(
            database.collection(path)
                .whereEqualTo("customerUid", uid)
                .snapshots()
                .map { snapshot -> snapshot.documents.mapNotNull { it.data } }
                .catch { error ->
                    if (error is Exception) handleFirestoreError(error, OperationType.LIST, path)
                    throw error
                }
        )
    }

    suspend fun submitSellerApplication(application: SellerApplication): Result<String> {
        _sellerApplications.value = listOf(application) + _sellerApplications.value

        if (db != null && auth.currentUser != null) {
            val uid = requireUserId()
            val appId = if (application.id.isNotBlank()) application.id else "app_" + UUID.randomUUID().toString().replace("-", "").take(16)
            val payload = mapOf(
                "id" to appId,
                "applicantUid" to uid,
                "businessName" to application.businessName,
                "ownerName" to application.ownerName,
                "category" to application.category,
                "region" to application.region,
                "phone" to application.phone,
                "email" to application.email,
                "description" to application.description,
                "status" to application.status,
                "createdAt" to FieldValue.serverTimestamp()
            )
            val docRef = db.collection("seller_applications").document(appId)
            try {
                docRef.set(payload).await()
                return Result.success(appId)
            } catch (e: Exception) {
                handleFirestoreError(e, OperationType.CREATE, docRef.path)
                return Result.failure(e)
            }
        }
        return Result.success(application.id)
    }

    suspend fun submitCustomerEnquiry(enquiry: CustomerEnquiry): Result<String> {
        _customerEnquiries.value = listOf(enquiry) + _customerEnquiries.value

        if (db != null && auth.currentUser != null) {
            val uid = requireUserId()
            val enquiryId = if (enquiry.id.isNotBlank()) enquiry.id else "enq_" + UUID.randomUUID().toString().replace("-", "").take(16)
            val payload = mapOf(
                "id" to enquiryId,
                "sellerId" to enquiry.sellerId,
                "customerUid" to uid,
                "customerName" to enquiry.customerName,
                "customerPhone" to enquiry.customerPhone,
                "subject" to enquiry.subject,
                "message" to enquiry.message,
                "createdAt" to FieldValue.serverTimestamp()
            )
            val docRef = db.collection("enquiries").document(enquiryId)
            try {
                docRef.set(payload).await()
                return Result.success(enquiryId)
            } catch (e: Exception) {
                handleFirestoreError(e, OperationType.CREATE, docRef.path)
                return Result.failure(e)
            }
        }
        return Result.success(enquiry.id)
    }

    suspend fun stockNewProduct(product: Product): Result<String> {
        _products.value = listOf(product) + _products.value

        if (db != null && auth.currentUser != null) {
            val uid = requireUserId()
            val prodId = if (product.id.isNotBlank()) product.id else "prod_" + UUID.randomUUID().toString().replace("-", "").take(16)
            val payload = mapOf(
                "id" to prodId,
                "sellerId" to product.sellerId,
                "sellerUid" to uid,
                "title" to product.title,
                "description" to product.description,
                "categoryId" to product.categoryId,
                "priceGhs" to product.priceGhs,
                "isService" to product.isService,
                "rating" to product.rating,
                "reviewCount" to product.reviewCount,
                "inStock" to product.inStock,
                "location" to product.location,
                "badge" to product.badge,
                "createdAt" to FieldValue.serverTimestamp()
            )
            val docRef = db.collection("products").document(prodId)
            try {
                docRef.set(payload).await()
                return Result.success(prodId)
            } catch (e: Exception) {
                handleFirestoreError(e, OperationType.CREATE, docRef.path)
                return Result.failure(e)
            }
        }
        return Result.success(product.id)
    }
}
private suspend fun <T> kotlinx.coroutines.flow.FlowCollector<T>.emitAll(flow: Flow<T>) {
    flow.collect { value -> emit(value) }
}

package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.Product
import com.example.data.model.Review
import com.example.data.model.ReviewTargetType
import com.example.data.repository.MarketRepository
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.util.UUID

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Asigame", appName)
    }

    @Test
    fun `test adding review updates product average rating`() = runBlocking {
        val repository = MarketRepository()
        val initialProduct = repository.products.value.first()
        val initialCount = initialProduct.reviewCount

        val testReview = Review(
            id = UUID.randomUUID().toString(),
            targetId = initialProduct.id,
            targetType = ReviewTargetType.PRODUCT,
            authorName = "Kwesi Mensah",
            rating = 5,
            comment = "Outstanding quality and fast Accra dispatch!",
            date = "Oct 2026",
            isVerifiedPurchase = true,
            helpfulCount = 0
        )

        repository.addReview(testReview)

        val updatedProduct = repository.products.value.first { it.id == initialProduct.id }
        assertEquals(initialCount + 1, updatedProduct.reviewCount)
        assertTrue(updatedProduct.rating in 1.0..5.0)
    }

    @Test
    fun `test adding review updates seller average rating`() = runBlocking {
        val repository = MarketRepository()
        val seller = repository.sellers.value.first()
        val initialCount = seller.reviewCount

        val testReview = Review(
            id = UUID.randomUUID().toString(),
            targetId = seller.id,
            targetType = ReviewTargetType.SELLER,
            authorName = "Akua Osei",
            rating = 5,
            comment = "Very polite seller with authentic Ghanaian craft.",
            date = "Oct 2026",
            isVerifiedPurchase = true,
            helpfulCount = 0
        )

        repository.addReview(testReview)

        val updatedSeller = repository.sellers.value.first { it.id == seller.id }
        assertEquals(initialCount + 1, updatedSeller.reviewCount)
    }

    @Test
    fun `test stocking product from online vendor increases catalog count`() = runBlocking {
        val repository = MarketRepository()
        val initialProductCount = repository.products.value.size

        val vendorStockedProduct = Product(
            id = "test_ai_stocked_1",
            sellerId = "seller_tech",
            title = "Imported 45W Fast Charger Pack",
            description = "High efficiency charger with dual ports",
            categoryId = "phones_electronics",
            priceGhs = 180.0,
            isService = false,
            rating = 5.0,
            reviewCount = 1,
            inStock = true,
            location = "Circle, Accra",
            highlights = listOf("45W Fast Output"),
            badge = "AI Stocked"
        )

        repository.stockNewProduct(vendorStockedProduct)
        val updatedCount = repository.products.value.size
        assertEquals(initialProductCount + 1, updatedCount)
        assertEquals("Imported 45W Fast Charger Pack", repository.products.value.first().title)
    }

    @Test
    fun `test category filter toggles categoryId in viewModel`() {
        val viewModel = com.example.ui.viewmodel.MarketViewModel()
        assertEquals(null, viewModel.uiState.value.selectedCategoryId)

        viewModel.selectCategoryFilter("fashion_beauty")
        assertEquals("fashion_beauty", viewModel.uiState.value.selectedCategoryId)

        // Tapping again toggles off to All
        viewModel.selectCategoryFilter("fashion_beauty")
        assertEquals(null, viewModel.uiState.value.selectedCategoryId)
    }
}

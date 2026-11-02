package com.example.data.repository

import com.example.base.FirestoreEmulatorTestBase
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeout
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test

class MarketRepositoryRuleTest : FirestoreEmulatorTestBase() {

    @Test
    fun createFirestoreOrder_authenticatedUser_createsDocumentAndReturnsId() = runBlocking {
        signInTestUser(ALICE_EMAIL)
        val repository = MarketRepository(firestore)

        val createResult = withTimeout(DEFAULT_TIMEOUT_MS) {
            repository.createFirestoreOrder(
                orderNumber = "ASI-GH-8888",
                customerName = "Alice Mensah",
                customerPhone = "+233241112222",
                totalGhs = 550.0
            )
        }
        assertTrue(createResult.isSuccess)
        val orderId = createResult.getOrThrow()
        assertTrue(orderId.isNotEmpty())
    }

    @Test
    fun getUserOrders_authenticatedUser_returnsOwnOrders() = runBlocking {
        signInTestUser(ALICE_EMAIL)
        val repository = MarketRepository(firestore)
        val orderId = withTimeout(DEFAULT_TIMEOUT_MS) {
            repository.createFirestoreOrder(
                orderNumber = "ASI-GH-7777",
                customerName = "Alice Mensah",
                customerPhone = "+233241112222",
                totalGhs = 320.0
            ).getOrThrow()
        }

        val ordersResult = withTimeout(DEFAULT_TIMEOUT_MS) { repository.getUserOrders() }
        assertTrue(ordersResult.isSuccess)
        val orders = ordersResult.getOrThrow()
        assertTrue(orders.any { it["id"] == orderId })
    }

    @Test
    fun observeOrders_authenticatedUser_emitsRealtimeUpdates() = runBlocking {
        signInTestUser(ALICE_EMAIL)
        val repository = MarketRepository(firestore)
        val orderId = withTimeout(DEFAULT_TIMEOUT_MS) {
            repository.createFirestoreOrder(
                orderNumber = "ASI-GH-6666",
                customerName = "Alice Mensah",
                customerPhone = "+233241112222",
                totalGhs = 190.0
            ).getOrThrow()
        }

        val emittedOrders = withTimeout(FLOW_TIMEOUT_MS) {
            repository.observeOrders().first { list: List<Map<String, Any?>> -> list.any { it["id"] == orderId } }
        }
        assertTrue(emittedOrders.any { it["id"] == orderId })
    }

    @Test
    fun getOrderById_crossUserAccess_failsWithPermissionDenied() = runBlocking {
        signInTestUser(ALICE_EMAIL)
        val aliceRepo = MarketRepository(firestore)
        val orderId = withTimeout(DEFAULT_TIMEOUT_MS) {
            aliceRepo.createFirestoreOrder(
                orderNumber = "ASI-GH-5555",
                customerName = "Alice Mensah",
                customerPhone = "+233241112222",
                totalGhs = 240.0
            ).getOrThrow()
        }

        signInTestUser(BOB_EMAIL)
        val exception = assertThrows(FirebaseFirestoreException::class.java) {
            runBlocking {
                withTimeout(DEFAULT_TIMEOUT_MS) {
                    firestore.collection("orders").document(orderId).get().await()
                }
            }
        }
        assertEquals(FirebaseFirestoreException.Code.PERMISSION_DENIED, exception.code)
    }

    @Test
    fun observeOrders_unauthenticatedUser_failsWithPermissionDenied() = runBlocking {
        auth.signOut()
        val repository = MarketRepository(firestore)

        val exception = assertThrows(Exception::class.java) {
            runBlocking {
                withTimeout(FLOW_TIMEOUT_MS) { repository.observeOrders().first() }
            }
        }
        val isAuthError = exception is IllegalStateException ||
                exception.unwrapFirestoreException()?.code == FirebaseFirestoreException.Code.PERMISSION_DENIED
        assertTrue("Expected IllegalStateException or PERMISSION_DENIED, got $exception", isAuthError)
    }

    private companion object {
        const val ALICE_EMAIL = "alice@asigame.test"
        const val BOB_EMAIL = "bob@asigame.test"
        const val DEFAULT_TIMEOUT_MS = 5000L
        const val FLOW_TIMEOUT_MS = 4000L
    }
}

package com.example.shoppingcart.domain.model

import com.example.shoppingcart.domain.error.CartError
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class QuantityTest {

    @Test
    fun `rechaza cero`() {
        assertThrows(CartError.InvalidQuantity::class.java) { Quantity(0) }
    }

    @Test
    fun `rechaza valores negativos`() {
        assertThrows(CartError.InvalidQuantity::class.java) { Quantity(-3) }
    }

    @Test
    fun `suma cantidades`() {
        assertEquals(Quantity(5), Quantity(2) + Quantity(3))
    }
}

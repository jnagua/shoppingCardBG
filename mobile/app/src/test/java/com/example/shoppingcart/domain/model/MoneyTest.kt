package com.example.shoppingcart.domain.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class MoneyTest {

    @Test
    fun `rechaza importes negativos`() {
        assertThrows(IllegalArgumentException::class.java) { Money(-1) }
    }

    @Test
    fun `multiplica por cantidad`() {
        assertEquals(Money(7500), Money(2500) * Quantity(3))
    }

    @Test
    fun `suma importes`() {
        assertEquals(Money(3750), Money(2500) + Money(1250))
    }
}

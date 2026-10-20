package com.example.shoppingcart.domain.model

import com.example.shoppingcart.domain.error.CartError
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test

class CartTest {

    private val keyboard = Product(1, "Teclado", "Teclado compacto USB", Money(2500), stock = 10)
    private val mouse = Product(2, "Mouse", "Mouse inalámbrico", Money(1250), stock = 5)

    @Test
    fun `calcula subtotales y total con varios productos`() {
        val cart = Cart()
            .add(keyboard, Quantity(2))
            .add(mouse, Quantity(3))

        assertEquals(Money(5000), cart.items.first { it.productId == keyboard.id }.lineTotal)
        assertEquals(Money(3750), cart.items.first { it.productId == mouse.id }.lineTotal)
        assertEquals(Money(8750), cart.total)
        assertEquals(5, cart.totalUnits)
    }

    @Test
    fun `agregar un producto existente acumula la cantidad sin duplicar lineas`() {
        val cart = Cart()
            .add(keyboard, Quantity(2))
            .add(keyboard, Quantity(3))

        assertEquals(1, cart.items.size)
        assertEquals(Quantity(5), cart.items.single().quantity)
        assertEquals(Money(12500), cart.total)
    }

    @Test
    fun `rechaza una cantidad mayor al stock y mantiene el estado anterior`() {
        val cart = Cart().add(mouse, Quantity(4))

        assertThrows(CartError.StockExceeded::class.java) {
            cart.add(mouse, Quantity(2))
        }

        assertEquals(Quantity(4), cart.items.single().quantity)
        assertEquals(Money(5000), cart.total)
    }

    @Test
    fun `actualizar reemplaza la cantidad anterior`() {
        val cart = Cart()
            .add(keyboard, Quantity(2))
            .updateQuantity(keyboard, Quantity(7))

        assertEquals(Quantity(7), cart.items.single().quantity)
        assertEquals(Money(17500), cart.total)
    }

    @Test
    fun `actualizar por encima del stock es rechazado`() {
        val cart = Cart().add(mouse, Quantity(1))

        assertThrows(CartError.StockExceeded::class.java) {
            cart.updateQuantity(mouse, Quantity(6))
        }
        assertEquals(Quantity(1), cart.items.single().quantity)
    }

    @Test
    fun `actualizar un producto que no esta en el carrito es rechazado`() {
        assertThrows(CartError.ItemNotInCart::class.java) {
            Cart().updateQuantity(keyboard, Quantity(1))
        }
    }

    @Test
    fun `eliminar quita la linea y recalcula el total`() {
        val cart = Cart()
            .add(keyboard, Quantity(1))
            .add(mouse, Quantity(2))
            .remove(keyboard.id)

        assertEquals(listOf(mouse.id), cart.items.map { it.productId })
        assertEquals(Money(2500), cart.total)
    }

    @Test
    fun `eliminar un producto que no esta en el carrito es rechazado`() {
        assertThrows(CartError.ItemNotInCart::class.java) {
            Cart().remove(keyboard.id)
        }
    }

    @Test
    fun `carrito vacio no tiene lineas y su total es cero`() {
        val cart = Cart()

        assertTrue(cart.isEmpty())
        assertEquals(Money.ZERO, cart.total)
        assertEquals(0, cart.totalUnits)
    }
}

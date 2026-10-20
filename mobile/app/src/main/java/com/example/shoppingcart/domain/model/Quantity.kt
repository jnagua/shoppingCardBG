package com.example.shoppingcart.domain.model

import com.example.shoppingcart.domain.error.CartError

/**
 * Cantidad de unidades de un producto. Siempre es un entero mayor que cero.
 */
@JvmInline
value class Quantity(val value: Int) {

    init {
        if (value <= 0) throw CartError.InvalidQuantity(value)
    }

    operator fun plus(other: Quantity): Quantity = Quantity(value + other.value)
}

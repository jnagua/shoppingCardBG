package com.example.shoppingcart.ui.common

import com.example.shoppingcart.R
import com.example.shoppingcart.domain.error.CartError

/** Traduce errores del dominio (o inesperados) a un mensaje comprensible para el usuario. */
fun Throwable.toUiMessage(): UiMessage = when (this) {
    is CartError.StockExceeded -> UiMessage(R.string.error_stock_exceeded, listOf(stock))
    is CartError.ProductNotFound -> UiMessage(R.string.error_product_not_found)
    is CartError.InvalidQuantity -> UiMessage(R.string.error_invalid_quantity)
    is CartError.ItemNotInCart -> UiMessage(R.string.error_item_not_in_cart)
    else -> UiMessage(R.string.error_unexpected)
}

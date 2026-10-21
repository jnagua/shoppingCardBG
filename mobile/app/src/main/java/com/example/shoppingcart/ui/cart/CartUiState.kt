package com.example.shoppingcart.ui.cart

import com.example.shoppingcart.ui.common.UiMessage

sealed interface CartUiState {
    data object Loading : CartUiState
    data object Empty : CartUiState
    data class Error(val message: UiMessage) : CartUiState
    data class Content(
        val items: List<CartItemUi>,
        val totalUnits: Int,
        val total: String
    ) : CartUiState
}

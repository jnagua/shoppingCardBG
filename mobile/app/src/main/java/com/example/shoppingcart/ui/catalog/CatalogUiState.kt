package com.example.shoppingcart.ui.catalog

import com.example.shoppingcart.ui.common.UiMessage

sealed interface CatalogUiState {
    data object Loading : CatalogUiState
    data object Empty : CatalogUiState
    data class Error(val message: UiMessage) : CatalogUiState
    data class Content(val products: List<ProductItemUi>) : CatalogUiState
}

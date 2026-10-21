package com.example.shoppingcart.ui.catalog

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shoppingcart.R
import com.example.shoppingcart.domain.model.Cart
import com.example.shoppingcart.domain.model.Product
import com.example.shoppingcart.domain.usecase.AddToCartUseCase
import com.example.shoppingcart.domain.usecase.GetProductsUseCase
import com.example.shoppingcart.domain.usecase.ObserveCartUseCase
import com.example.shoppingcart.ui.common.UiMessage
import com.example.shoppingcart.ui.common.toUiMessage
import com.example.shoppingcart.ui.common.toUsd
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * viewmodel Combina productos y carrito para que cada tarjeta refleje lo agregado
 */
@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class CatalogViewModel @Inject constructor(
    getProducts: GetProductsUseCase,
    observeCart: ObserveCartUseCase,
    private val addToCartUseCase: AddToCartUseCase
) : ViewModel() {

    private val pendingProductId = MutableStateFlow<Long?>(null)
    private val reloadTrigger = MutableStateFlow(0)

    private val _events = Channel<UiMessage>(Channel.BUFFERED)
    val events: Flow<UiMessage> = _events.receiveAsFlow()

    val uiState: StateFlow<CatalogUiState> = reloadTrigger
        .flatMapLatest {
            combine(getProducts(), observeCart(), pendingProductId, ::buildState)
                .onStart { emit(CatalogUiState.Loading) }
                .catch { emit(CatalogUiState.Error(UiMessage(R.string.catalog_error))) }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), CatalogUiState.Loading)

    val cartUnits: StateFlow<Int> = observeCart()
        .map { it.totalUnits }
        .catch { emit(0) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 0)

    fun addToCart(productId: Long) {
        if (!pendingProductId.compareAndSet(null, productId)) return

        viewModelScope.launch {
            try {
                addToCartUseCase(productId, quantity = 1)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _events.send(e.toUiMessage())
            } finally {
                pendingProductId.value = null
            }
        }
    }

    fun retry() {
        reloadTrigger.update { it + 1 }
    }

    private fun buildState(products: List<Product>, cart: Cart, pendingId: Long?): CatalogUiState {
        if (products.isEmpty()) return CatalogUiState.Empty

        val quantitiesInCart = cart.items.associate { it.productId to it.quantity.value }
        return CatalogUiState.Content(
            products.map { product ->
                val inCart = quantitiesInCart[product.id] ?: 0
                ProductItemUi(
                    id = product.id,
                    name = product.name,
                    description = product.description,
                    price = product.price.toUsd(),
                    stock = product.stock,
                    quantityInCart = inCart,
                    isAdding = pendingId == product.id,
                    canAdd = pendingId == null && inCart < product.stock
                )
            }
        )
    }
}

package com.example.shoppingcart.ui.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shoppingcart.R
import com.example.shoppingcart.domain.model.Cart
import com.example.shoppingcart.domain.model.Product
import com.example.shoppingcart.domain.usecase.GetProductsUseCase
import com.example.shoppingcart.domain.usecase.ObserveCartUseCase
import com.example.shoppingcart.domain.usecase.RemoveFromCartUseCase
import com.example.shoppingcart.domain.usecase.UpdateCartItemQuantityUseCase
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
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Estado y acciones del carrito. Los importes vienen del dominio; el stock del
 * catálogo solo se usa para habilitar o no el botón de aumentar.
 */
@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class CartViewModel @Inject constructor(
    observeCart: ObserveCartUseCase,
    getProducts: GetProductsUseCase,
    private val updateQuantityUseCase: UpdateCartItemQuantityUseCase,
    private val removeFromCartUseCase: RemoveFromCartUseCase
) : ViewModel() {

    private val pendingProductId = MutableStateFlow<Long?>(null)
    private val reloadTrigger = MutableStateFlow(0)

    private val _events = Channel<UiMessage>(Channel.BUFFERED)
    val events: Flow<UiMessage> = _events.receiveAsFlow()

    val uiState: StateFlow<CartUiState> = reloadTrigger
        .flatMapLatest {
            combine(observeCart(), getProducts(), pendingProductId, ::buildState)
                .onStart { emit(CartUiState.Loading) }
                .catch { emit(CartUiState.Error(UiMessage(R.string.cart_error))) }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), CartUiState.Loading)

    fun increase(productId: Long) {
        val quantity = currentQuantity(productId) ?: return
        runOperation(productId) { updateQuantityUseCase(productId, quantity + 1) }
    }

    fun decrease(productId: Long) {
        val quantity = currentQuantity(productId) ?: return
        runOperation(productId) { updateQuantityUseCase(productId, quantity - 1) }
    }

    fun remove(productId: Long) {
        runOperation(productId) { removeFromCartUseCase(productId) }
    }

    fun retry() {
        reloadTrigger.update { it + 1 }
    }

    private fun currentQuantity(productId: Long): Int? =
        (uiState.value as? CartUiState.Content)
            ?.items
            ?.find { it.productId == productId }
            ?.quantity

    private fun runOperation(productId: Long, operation: suspend () -> Unit) {
        if (!pendingProductId.compareAndSet(null, productId)) return

        viewModelScope.launch {
            try {
                operation()
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _events.send(e.toUiMessage())
            } finally {
                pendingProductId.value = null
            }
        }
    }

    private fun buildState(cart: Cart, products: List<Product>, pendingId: Long?): CartUiState {
        if (cart.isEmpty()) return CartUiState.Empty

        val stockByProduct = products.associate { it.id to it.stock }
        val isIdle = pendingId == null
        return CartUiState.Content(
            items = cart.items.map { item ->
                val quantity = item.quantity.value
                CartItemUi(
                    productId = item.productId,
                    name = item.name,
                    unitPrice = item.unitPrice.toUsd(),
                    quantity = quantity,
                    lineTotal = item.lineTotal.toUsd(),
                    canIncrease = isIdle && quantity < (stockByProduct[item.productId] ?: 0),
                    canDecrease = isIdle && quantity > 1,
                    canRemove = isIdle,
                    isPending = pendingId == item.productId
                )
            },
            totalUnits = cart.totalUnits,
            total = cart.total.toUsd()
        )
    }
}

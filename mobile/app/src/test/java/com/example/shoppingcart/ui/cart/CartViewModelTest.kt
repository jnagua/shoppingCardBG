package com.example.shoppingcart.ui.cart

import com.example.shoppingcart.domain.model.Cart
import com.example.shoppingcart.domain.model.Money
import com.example.shoppingcart.domain.model.Product
import com.example.shoppingcart.domain.model.Quantity
import com.example.shoppingcart.domain.usecase.GetProductsUseCase
import com.example.shoppingcart.domain.usecase.ObserveCartUseCase
import com.example.shoppingcart.domain.usecase.RemoveFromCartUseCase
import com.example.shoppingcart.domain.usecase.UpdateCartItemQuantityUseCase
import com.example.shoppingcart.testutil.FakeCartRepository
import com.example.shoppingcart.testutil.FakeProductRepository
import com.example.shoppingcart.testutil.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CartViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val keyboard = Product(1, "Teclado", "Teclado compacto USB", Money(2500), stock = 10)
    private val mouse = Product(2, "Mouse", "Mouse inalámbrico", Money(1250), stock = 3)

    private val productRepository = FakeProductRepository(listOf(keyboard, mouse))
    private val cartRepository = FakeCartRepository(
        Cart()
            .add(keyboard, Quantity(2))
            .add(mouse, Quantity(1))
    )

    private fun TestScope.createViewModel(): CartViewModel {
        val viewModel = CartViewModel(
            observeCart = ObserveCartUseCase(cartRepository),
            getProducts = GetProductsUseCase(productRepository),
            updateQuantityUseCase = UpdateCartItemQuantityUseCase(productRepository, cartRepository),
            removeFromCartUseCase = RemoveFromCartUseCase(cartRepository)
        )
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.uiState.collect {} }
        return viewModel
    }

    private fun CartViewModel.content(): CartUiState.Content = uiState.value as CartUiState.Content

    private fun CartViewModel.item(productId: Long): CartItemUi =
        content().items.first { it.productId == productId }

    @Test
    fun `muestra subtotales total y unidades calculados por el dominio`() = runTest {
        val viewModel = createViewModel()

        val content = viewModel.content()
        assertEquals("$50.00", viewModel.item(keyboard.id).lineTotal)
        assertEquals("$12.50", viewModel.item(mouse.id).lineTotal)
        assertEquals("$62.50", content.total)
        assertEquals(3, content.totalUnits)
    }

    @Test
    fun `con el carrito vacio muestra estado vacio`() = runTest {
        cartRepository.save(Cart())
        val viewModel = createViewModel()

        assertEquals(CartUiState.Empty, viewModel.uiState.value)
    }

    @Test
    fun `aumentar y disminuir cambian la cantidad y el total`() = runTest {
        val viewModel = createViewModel()

        viewModel.increase(keyboard.id)
        advanceUntilIdle()
        assertEquals(3, viewModel.item(keyboard.id).quantity)

        viewModel.decrease(keyboard.id)
        viewModel.decrease(keyboard.id)
        advanceUntilIdle()
        assertEquals(1, viewModel.item(keyboard.id).quantity)
        assertEquals("$37.50", viewModel.content().total)
    }

    @Test
    fun `eliminar quita la linea y al quitar la ultima queda vacio`() = runTest {
        val viewModel = createViewModel()

        viewModel.remove(keyboard.id)
        advanceUntilIdle()
        assertEquals(listOf(mouse.id), viewModel.content().items.map { it.productId })

        viewModel.remove(mouse.id)
        advanceUntilIdle()
        assertEquals(CartUiState.Empty, viewModel.uiState.value)
    }

    @Test
    fun `disminuir se deshabilita en uno y aumentar al llegar al stock`() = runTest {
        cartRepository.save(Cart().add(mouse, Quantity(3)).add(keyboard, Quantity(1)))
        val viewModel = createViewModel()

        assertFalse(viewModel.item(mouse.id).canIncrease)
        assertTrue(viewModel.item(mouse.id).canDecrease)
        assertTrue(viewModel.item(keyboard.id).canIncrease)
        assertFalse(viewModel.item(keyboard.id).canDecrease)
    }

    @Test
    fun `mientras hay una operacion pendiente deshabilita todos los botones`() = runTest {
        val viewModel = createViewModel()
        cartRepository.pauseSaves()

        viewModel.increase(keyboard.id)

        assertTrue(viewModel.item(keyboard.id).isPending)
        assertTrue(viewModel.content().items.none { it.canIncrease || it.canDecrease || it.canRemove })

        cartRepository.resumeSaves()
        advanceUntilIdle()

        assertEquals(3, viewModel.item(keyboard.id).quantity)
        assertTrue(viewModel.content().items.none { it.isPending })
    }
}

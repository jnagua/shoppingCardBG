package com.example.shoppingcart.ui.catalog

import com.example.shoppingcart.R
import com.example.shoppingcart.domain.model.Cart
import com.example.shoppingcart.domain.model.Money
import com.example.shoppingcart.domain.model.Product
import com.example.shoppingcart.domain.model.Quantity
import com.example.shoppingcart.domain.usecase.AddToCartUseCase
import com.example.shoppingcart.domain.usecase.GetProductsUseCase
import com.example.shoppingcart.domain.usecase.ObserveCartUseCase
import com.example.shoppingcart.testutil.FakeCartRepository
import com.example.shoppingcart.testutil.FakeProductRepository
import com.example.shoppingcart.testutil.MainDispatcherRule
import com.example.shoppingcart.ui.common.UiMessage
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
class CatalogViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val keyboard = Product(1, "Teclado", "Teclado compacto USB", Money(2500), stock = 10)
    private val mouse = Product(2, "Mouse", "Mouse inalámbrico", Money(1250), stock = 1)

    private val productRepository = FakeProductRepository(listOf(keyboard, mouse))
    private var cartRepository = FakeCartRepository()

    private fun createViewModel() = CatalogViewModel(
        getProducts = GetProductsUseCase(productRepository),
        observeCart = ObserveCartUseCase(cartRepository),
        addToCartUseCase = AddToCartUseCase(productRepository, cartRepository)
    )

    private fun TestScope.observe(viewModel: CatalogViewModel) {
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.uiState.collect {} }
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.cartUnits.collect {} }
    }

    private fun CatalogViewModel.products(): List<ProductItemUi> =
        (uiState.value as CatalogUiState.Content).products

    @Test
    fun `muestra los productos con su precio formateado`() = runTest {
        val viewModel = createViewModel()
        observe(viewModel)

        val products = viewModel.products()
        assertEquals(listOf("Teclado", "Mouse"), products.map { it.name })
        assertEquals("$25.00", products.first().price)
        assertTrue(products.all { it.canAdd })
    }

    @Test
    fun `muestra estado vacio si no hay productos`() = runTest {
        val viewModel = CatalogViewModel(
            GetProductsUseCase(FakeProductRepository()),
            ObserveCartUseCase(cartRepository),
            AddToCartUseCase(productRepository, cartRepository)
        )
        observe(viewModel)

        assertEquals(CatalogUiState.Empty, viewModel.uiState.value)
    }

    @Test
    fun `un fallo de carga muestra error y reintentar lo recupera`() = runTest {
        productRepository.failOnObserve = true
        val viewModel = createViewModel()
        observe(viewModel)

        assertEquals(CatalogUiState.Error(UiMessage(R.string.catalog_error)), viewModel.uiState.value)

        productRepository.failOnObserve = false
        viewModel.retry()

        assertTrue(viewModel.uiState.value is CatalogUiState.Content)
    }

    @Test
    fun `agregar actualiza la cantidad en carrito y las unidades del badge`() = runTest {
        val viewModel = createViewModel()
        observe(viewModel)

        viewModel.addToCart(keyboard.id)
        viewModel.addToCart(keyboard.id)
        advanceUntilIdle()

        assertEquals(2, viewModel.products().first { it.id == keyboard.id }.quantityInCart)
        assertEquals(2, viewModel.cartUnits.value)
    }

    @Test
    fun `mientras hay una operacion pendiente bloquea todos los botones e ignora otro clic`() = runTest {
        val viewModel = createViewModel()
        observe(viewModel)
        cartRepository.pauseSaves()

        viewModel.addToCart(keyboard.id)

        val pending = viewModel.products()
        assertTrue(pending.first { it.id == keyboard.id }.isAdding)
        assertTrue(pending.none { it.canAdd })

        viewModel.addToCart(mouse.id)
        cartRepository.resumeSaves()
        advanceUntilIdle()

        assertEquals(listOf(keyboard.id), cartRepository.current.items.map { it.productId })
        assertTrue(viewModel.products().none { it.isAdding })
    }

    @Test
    fun `al llegar al stock deshabilita agregar`() = runTest {
        cartRepository = FakeCartRepository(Cart().add(mouse, Quantity(1)))
        val viewModel = createViewModel()
        observe(viewModel)

        assertFalse(viewModel.products().first { it.id == mouse.id }.canAdd)
    }

    @Test
    fun `si el dominio rechaza por stock emite un mensaje y no cambia el carrito`() = runTest {
        cartRepository = FakeCartRepository(Cart().add(mouse, Quantity(1)))
        val viewModel = createViewModel()
        observe(viewModel)
        val events = mutableListOf<UiMessage>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.events.collect(events::add) }

        viewModel.addToCart(mouse.id)
        advanceUntilIdle()

        assertEquals(listOf(UiMessage(R.string.error_stock_exceeded, listOf(1))), events)
        assertEquals(Quantity(1), cartRepository.current.items.single().quantity)
    }
}

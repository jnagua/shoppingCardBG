package com.example.shoppingcart.ui.catalog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.MenuProvider
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.shoppingcart.R
import com.example.shoppingcart.databinding.FragmentCatalogBinding
import com.example.shoppingcart.ui.common.UiMessage
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

/**
 * Pantalla de catálogo: lista los productos disponibles y permite
 * agregarlos al carrito. Desde la Toolbar se navega al carrito.
 */
@AndroidEntryPoint
class CatalogFragment : Fragment() {

    private var _binding: FragmentCatalogBinding? = null
    private val binding get() = _binding!!

    private val viewModel: CatalogViewModel by viewModels()

    private val productAdapter = ProductAdapter(onAddClick = { viewModel.addToCart(it) })

    private var cartBadge: TextView? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCatalogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupMenu()
        binding.recyclerProducts.adapter = productAdapter
        binding.buttonRetry.setOnClickListener { viewModel.retry() }
        observeViewModel()
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch { viewModel.uiState.collect(::render) }
                launch { viewModel.events.collect(::showMessage) }
                launch { viewModel.cartUnits.collect(::updateCartBadge) }
            }
        }
    }

    private fun render(state: CatalogUiState) = when (state) {
        CatalogUiState.Loading -> showLoading()
        CatalogUiState.Empty -> showEmpty()
        is CatalogUiState.Error -> showError(state.message.resolve(requireContext()))
        is CatalogUiState.Content -> showProducts(state.products)
    }

    private fun showMessage(message: UiMessage) {
        Snackbar.make(binding.root, message.resolve(requireContext()), Snackbar.LENGTH_SHORT).show()
    }

    private fun updateCartBadge(units: Int) {
        cartBadge?.let { badge ->
            badge.isVisible = units > 0
            badge.text = if (units > MAX_BADGE_UNITS) "$MAX_BADGE_UNITS+" else units.toString()
        }
    }

    private fun showLoading() = showOnly(binding.progressLoading)

    private fun showEmpty() = showOnly(binding.textEmpty)

    private fun showError(message: String) {
        binding.textError.text = message
        showOnly(binding.layoutError)
    }

    private fun showProducts(products: List<ProductItemUi>) {
        productAdapter.submitList(products)
        showOnly(binding.recyclerProducts)
    }

    private fun showOnly(visibleView: View) = with(binding) {
        listOf(recyclerProducts, progressLoading, textEmpty, layoutError).forEach {
            it.isVisible = it == visibleView
        }
    }

    private fun setupMenu() {
        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_catalog, menu)
                val cartItem = menu.findItem(R.id.action_open_cart)
                cartItem.actionView?.let { actionView ->
                    actionView.setOnClickListener { onMenuItemSelected(cartItem) }
                    cartBadge = actionView.findViewById(R.id.text_cart_badge)
                }
                updateCartBadge(viewModel.cartUnits.value)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean =
                when (menuItem.itemId) {
                    R.id.action_open_cart -> {
                        findNavController().navigate(
                            CatalogFragmentDirections.actionCatalogToCart()
                        )
                        true
                    }
                    else -> false
                }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.recyclerProducts.adapter = null
        cartBadge = null
        _binding = null
    }

    private companion object {
        const val MAX_BADGE_UNITS = 99
    }
}

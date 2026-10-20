package com.example.shoppingcart.ui.catalog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.view.MenuProvider
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import com.example.shoppingcart.R
import com.example.shoppingcart.databinding.FragmentCatalogBinding
import dagger.hilt.android.AndroidEntryPoint

/**
 * Pantalla de catálogo: lista los productos disponibles y permite
 * agregarlos al carrito. Desde la Toolbar se navega al carrito.
 */
@AndroidEntryPoint
class CatalogFragment : Fragment() {

    private var _binding: FragmentCatalogBinding? = null
    private val binding get() = _binding!!

    private val productAdapter = ProductAdapter(onAddClick = { })

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
        _binding = null
    }
}

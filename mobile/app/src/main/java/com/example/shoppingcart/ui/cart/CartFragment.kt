package com.example.shoppingcart.ui.cart

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.shoppingcart.R
import com.example.shoppingcart.databinding.FragmentCartBinding
import com.example.shoppingcart.ui.common.UiMessage
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

/**
 * Pantalla del carrito: muestra las líneas agregadas con sus subtotales y el
 * total, y permite modificar cantidades o eliminar productos.
 */
@AndroidEntryPoint
class CartFragment : Fragment() {

    private var _binding: FragmentCartBinding? = null
    private val binding get() = _binding!!

    private val viewModel: CartViewModel by viewModels()

    private val cartAdapter = CartItemAdapter(
        onIncrease = { viewModel.increase(it) },
        onDecrease = { viewModel.decrease(it) },
        onRemove = { viewModel.remove(it) }
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCartBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.recyclerCart.adapter = cartAdapter
        binding.buttonRetry.setOnClickListener { viewModel.retry() }
        binding.buttonGoToCatalog.setOnClickListener { findNavController().navigateUp() }
        observeViewModel()
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch { viewModel.uiState.collect(::render) }
                launch { viewModel.events.collect(::showMessage) }
            }
        }
    }

    private fun render(state: CartUiState) = when (state) {
        CartUiState.Loading -> showOnly(binding.progressLoading)
        CartUiState.Empty -> showOnly(binding.layoutEmpty)
        is CartUiState.Error -> {
            binding.textError.text = state.message.resolve(requireContext())
            showOnly(binding.layoutError)
        }
        is CartUiState.Content -> showContent(state)
    }

    private fun showContent(state: CartUiState.Content) = with(binding) {
        cartAdapter.submitList(state.items)
        textTotalUnits.text = getString(R.string.cart_total_units, state.totalUnits)
        textTotal.text = state.total
        showOnly(layoutContent)
    }

    private fun showOnly(visibleView: View) = with(binding) {
        listOf(layoutContent, progressLoading, layoutEmpty, layoutError).forEach {
            it.isVisible = it == visibleView
        }
    }

    private fun showMessage(message: UiMessage) {
        Snackbar.make(binding.root, message.resolve(requireContext()), Snackbar.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.recyclerCart.adapter = null
        _binding = null
    }
}

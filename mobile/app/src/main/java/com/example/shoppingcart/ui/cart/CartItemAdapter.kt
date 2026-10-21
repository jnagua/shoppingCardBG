package com.example.shoppingcart.ui.cart

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.shoppingcart.R
import com.example.shoppingcart.databinding.ItemCartBinding

/**
 * Pinta las líneas del carrito y avisa cuando se pulsa aumentar, disminuir o eliminar.
 */
class CartItemAdapter(
    private val onIncrease: (productId: Long) -> Unit,
    private val onDecrease: (productId: Long) -> Unit,
    private val onRemove: (productId: Long) -> Unit
) : ListAdapter<CartItemUi, CartItemAdapter.CartItemViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartItemViewHolder {
        val binding = ItemCartBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CartItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CartItemViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class CartItemViewHolder(
        private val binding: ItemCartBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: CartItemUi) = with(binding) {
            textName.text = item.name
            textUnitPrice.text = root.context.getString(R.string.cart_unit_price, item.unitPrice)
            textLineTotal.text = item.lineTotal
            textQuantity.text = item.quantity.toString()

            textQuantity.isInvisible = item.isPending
            progressUpdating.isVisible = item.isPending

            buttonIncrease.isEnabled = item.canIncrease
            buttonDecrease.isEnabled = item.canDecrease
            buttonRemove.isEnabled = item.canRemove

            buttonIncrease.setOnClickListener { onIncrease(item.productId) }
            buttonDecrease.setOnClickListener { onDecrease(item.productId) }
            buttonRemove.setOnClickListener { onRemove(item.productId) }
        }
    }

    private object DiffCallback : DiffUtil.ItemCallback<CartItemUi>() {
        override fun areItemsTheSame(oldItem: CartItemUi, newItem: CartItemUi) =
            oldItem.productId == newItem.productId

        override fun areContentsTheSame(oldItem: CartItemUi, newItem: CartItemUi) =
            oldItem == newItem
    }
}

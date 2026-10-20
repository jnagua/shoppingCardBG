package com.example.shoppingcart.ui.catalog

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.shoppingcart.R
import com.example.shoppingcart.databinding.ItemProductBinding

/**
 * Pinta las tarjetas del catálogo y avisa cuando se pulsa "Agregar".
 */
class ProductAdapter(
    private val onAddClick: (productId: Long) -> Unit
) : ListAdapter<ProductItemUi, ProductAdapter.ProductViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding = ItemProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ProductViewHolder(
        private val binding: ItemProductBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ProductItemUi) = with(binding) {
            val context = root.context
            textName.text = item.name
            textDescription.text = item.description
            textPrice.text = item.price
            textAvailability.text = if (item.stock > 0) {
                context.getString(R.string.product_available, item.stock)
            } else {
                context.getString(R.string.product_out_of_stock)
            }
            textInCart.isVisible = item.quantityInCart > 0
            textInCart.text = context.getString(R.string.product_in_cart, item.quantityInCart)

            buttonAdd.isEnabled = item.canAdd
            buttonAdd.isInvisible = item.isAdding
            progressAdding.isVisible = item.isAdding
            buttonAdd.setOnClickListener { onAddClick(item.id) }
        }
    }

    private object DiffCallback : DiffUtil.ItemCallback<ProductItemUi>() {
        override fun areItemsTheSame(oldItem: ProductItemUi, newItem: ProductItemUi) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: ProductItemUi, newItem: ProductItemUi) =
            oldItem == newItem
    }
}

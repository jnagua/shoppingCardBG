package com.example.shoppingcart.ui.common

import android.content.Context
import androidx.annotation.StringRes

/**
 * Mensaje para mostrar en pantalla, expresado como recurso de texto para que
 * el ViewModel no dependa de Context.
 */
data class UiMessage(
    @StringRes val resId: Int,
    val args: List<Any> = emptyList()
) {
    fun resolve(context: Context): String = context.getString(resId, *args.toTypedArray())
}

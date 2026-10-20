package com.example.shoppingcart.ui.common

import com.example.shoppingcart.domain.model.Money
import java.math.BigDecimal
import java.text.NumberFormat
import java.util.Locale

private val usdFormat: NumberFormat = NumberFormat.getCurrencyInstance(Locale.US)

fun Money.toUsd(): String = usdFormat.format(BigDecimal.valueOf(cents, 2))

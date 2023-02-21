package com.example.thelegend27.eventinfrastructure.trading

data class TradablePricesItemDto(
    val name: String,
    val price: Int,
    val type: TradableType
)
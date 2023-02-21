package com.example.thelegend27.eventinfrastructure.trading

// Even Header type "TradableSold"
data class TradableSoldEvent(
    val playerId: String,
    val robotId: String,
    val type: TradableType,
    val name: String,
    val amount: Int,
    val pricePerUnit: Number,
    val totalPrice: Number
)

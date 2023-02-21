package com.example.thelegend27.eventinfrastructure.trading

//header type for the event "TradableBought"
data class TradableBoughtEvent(
    val playerId: String,
    val robotId: String?,
    val type: TradableType,
    val name: String,
    val amount: Int,
    val pricePerUnit: Number,
    val totalPrice: Number
)

package com.example.thelegend27.eventinfrastructure.trading

data class BankAccountInitializedEvent(
    val balance: Int,
    val playerId: String
)

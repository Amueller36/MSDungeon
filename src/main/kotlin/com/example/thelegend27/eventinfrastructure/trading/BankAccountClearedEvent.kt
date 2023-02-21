package com.example.thelegend27.eventinfrastructure.trading

data class BankAccountClearedEvent(
    val playerId: String,
    val balance: Number
)
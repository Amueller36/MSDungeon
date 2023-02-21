package com.example.thelegend27.eventinfrastructure.trading

//Event header for the event "BankAccountTransactionBooked"
data class BankAccountTransactionBookedEvent(
    val playerId: String,
    val transactionAmount: Number,
    val balance: Number
)

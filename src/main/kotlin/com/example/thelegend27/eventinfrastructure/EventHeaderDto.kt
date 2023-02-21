package com.example.thelegend27.eventinfrastructure

data class EventHeaderDto(
    val eventId: String?,
    val kafkaTopic: String?,
    val timestamp: String?,
    val transactionId: String?,
    val type: String?,
    val version: String?
)
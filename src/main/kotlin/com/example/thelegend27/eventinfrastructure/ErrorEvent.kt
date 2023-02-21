package com.example.thelegend27.eventinfrastructure

import com.fasterxml.jackson.annotation.JsonInclude

data class ErrorEvent(
    val code: String,
    val description: String,
    val details: String,
    @JsonInclude(JsonInclude.Include.NON_ABSENT)
    val robotId: String? = null,
    val playerId: String,
    val transactionId: String
)
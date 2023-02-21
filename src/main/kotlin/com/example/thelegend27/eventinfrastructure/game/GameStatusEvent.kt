package com.example.thelegend27.eventinfrastructure.game

import com.fasterxml.jackson.annotation.JsonProperty

data class GameStatusEvent(
    val gameId: String,
    @JsonProperty("gameworldId")
    val gameWorldId: String?,
    val status: String
)

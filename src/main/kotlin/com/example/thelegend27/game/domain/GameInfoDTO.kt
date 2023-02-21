package com.example.thelegend27.game.domain

data class GameInfoDTO(
    val currentRoundNumber: Any,
    val gameId: String,
    val gameStatus: String,
    val maxPlayers: Int,
    val maxRounds: Int,
    val participatingPlayers: List<String>,
    val roundLengthInMillis: Int
)
package com.example.thelegend27.eventinfrastructure.game

data class RoundStatusEvent(
    val gameId: String,
    val impreciseTimingPredictions: ImpreciseTimingPredictionsDto,
    val impreciseTimings: ImpreciseTimingsDto,
    val roundId: String,
    val roundNumber: Int,
    val roundStatus: String
)
package com.example.thelegend27.eventinfrastructure.map

import com.example.thelegend27.trading.domain.Resource


data class PlanetResourceMinedDto(
    val type: Resource?,
    val maxAmount: Int,
    val currentAmount: Int
)

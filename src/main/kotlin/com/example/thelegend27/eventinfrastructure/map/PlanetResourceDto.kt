package com.example.thelegend27.eventinfrastructure.map

import com.example.thelegend27.trading.domain.Resource


data class PlanetResourceDto(
    val resourceType: Resource?,
    val maxAmount: Int,
    val currentAmount: Int
)

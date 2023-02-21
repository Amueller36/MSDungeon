package com.example.thelegend27.eventinfrastructure.map

import com.fasterxml.jackson.annotation.JsonProperty

data class PlanetResourceMinedEvent(
    @JsonProperty("planet")
    val planetId: String,
    val minedAmount: Int,
    val resource: PlanetResourceMinedDto,
)

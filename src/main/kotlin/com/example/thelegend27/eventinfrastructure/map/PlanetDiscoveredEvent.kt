package com.example.thelegend27.eventinfrastructure.map

import com.fasterxml.jackson.annotation.JsonProperty


//Header type for the event "planet-discovered"
data class PlanetDiscoveredEvent(
    @JsonProperty("planet")
    val planetId: String,
    val movementDifficulty: Int,
    val neighbours: List<PlanetNeighbourDto>,
    val resource: PlanetResourceDto?
)


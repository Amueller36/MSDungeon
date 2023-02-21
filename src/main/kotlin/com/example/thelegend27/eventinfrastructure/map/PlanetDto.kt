package com.example.thelegend27.eventinfrastructure.map

data class PlanetDto(
    val planetId: String,
    val gameWorldId: String,
    val movementDifficulty: Int,
    val resourceType: String?
)
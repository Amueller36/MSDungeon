package com.example.thelegend27.eventinfrastructure.robot.integrationevents

data class RobotMovedIntegrationEvent(
    val robotId: String,
    val remainingEnergy: Int,
    val fromPlanet: PlanetMovementInfoDto,
    val toPlanet: PlanetMovementInfoDto
)
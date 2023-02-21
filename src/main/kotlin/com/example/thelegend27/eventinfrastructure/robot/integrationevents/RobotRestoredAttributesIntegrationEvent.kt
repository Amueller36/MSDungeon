package com.example.thelegend27.eventinfrastructure.robot.integrationevents

data class RobotRestoredAttributesIntegrationEvent(
    val robotId: String,
    val restorationType: String,
    val availableEnergy: Int,
    val availableHealth: Int
)
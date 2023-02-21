package com.example.thelegend27.eventinfrastructure.robot.integrationevents

import com.example.thelegend27.eventinfrastructure.robot.RobotDto

data class RobotSpawnedIntegrationEvent(
    val robot: RobotDto
)
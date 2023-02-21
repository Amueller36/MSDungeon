package com.example.thelegend27.eventinfrastructure.robot

//Header type for the event "robot-spawned"
data class RobotSpawnedEvent(
    val robot: RobotDto
)
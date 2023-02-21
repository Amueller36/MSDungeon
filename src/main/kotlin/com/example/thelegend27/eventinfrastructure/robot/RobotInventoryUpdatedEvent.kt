package com.example.thelegend27.eventinfrastructure.robot

import com.fasterxml.jackson.annotation.JsonProperty

data class RobotInventoryUpdatedEvent(
    @JsonProperty("robot")
    val robotId: String,
    val inventory: RobotInventoryDto
)
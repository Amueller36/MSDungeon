package com.example.thelegend27.eventinfrastructure.robot

import com.fasterxml.jackson.annotation.JsonProperty

data class RobotHealthUpdatedEvent(
    @JsonProperty("robot")
    val robotId: String,
    val amount: Int,
    val health: Int
)
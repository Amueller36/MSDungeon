package com.example.thelegend27.eventinfrastructure.robot

import com.fasterxml.jackson.annotation.JsonProperty

data class RobotEnergyUpdatedEvent(
    @JsonProperty("robot")
    val robotId: String,
    val amount: Int,
    val energy: Int
)
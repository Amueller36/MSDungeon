package com.example.thelegend27.eventinfrastructure.robot

import com.fasterxml.jackson.annotation.JsonProperty

data class RobotKilledEvent(
    @JsonProperty("robot")
    val robotId: String
)
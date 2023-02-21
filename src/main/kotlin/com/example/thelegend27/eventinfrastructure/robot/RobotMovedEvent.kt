package com.example.thelegend27.eventinfrastructure.robot

import com.fasterxml.jackson.annotation.JsonProperty

data class RobotMovedEvent(
    @JsonProperty("robot")
    val robotId: String,
    val fromPlanet: String,
    val toPlanet: String
)
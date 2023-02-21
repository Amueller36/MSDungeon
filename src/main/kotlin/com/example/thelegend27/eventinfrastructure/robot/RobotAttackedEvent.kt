package com.example.thelegend27.eventinfrastructure.robot

import com.fasterxml.jackson.annotation.JsonProperty

data class RobotAttackedEvent(
    @JsonProperty("attacker")
    val attackerId: String,
    @JsonProperty("defender")
    val defenderId: String
)
package com.example.thelegend27.eventinfrastructure.robot

import com.example.thelegend27.trading.domain.Upgrade
import com.fasterxml.jackson.annotation.JsonProperty

data class RobotUpgradedEvent(
    @JsonProperty("robot")
    val robotId: String,
    @JsonProperty("type")
    val upgradeType: Upgrade,
    val level: Int
)

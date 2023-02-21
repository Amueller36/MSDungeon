package com.example.thelegend27.eventinfrastructure.robot.integrationevents

import com.example.thelegend27.eventinfrastructure.robot.RobotDto
import com.example.thelegend27.trading.domain.Upgrade

data class RobotUpgradedIntegrationEvent(
    val robotId: String,
    val level: Int,
    val upgrade: Upgrade,
    val robot: RobotDto
)
package com.example.thelegend27.eventinfrastructure.robot.integrationevents

data class RobotBattleInfoDto(
    val robotId: String,
    val availableHealth: Int,
    val availableEnergy: Int,
    val alive: Boolean
)
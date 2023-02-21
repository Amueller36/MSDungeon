package com.example.thelegend27.eventinfrastructure.robot.integrationevents

data class RobotAttackedIntegrationEvent(
    val attacker: RobotBattleInfoDto,
    val target: RobotBattleInfoDto
)
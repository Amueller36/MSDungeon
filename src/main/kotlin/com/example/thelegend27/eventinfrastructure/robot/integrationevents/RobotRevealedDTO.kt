package com.example.thelegend27.eventinfrastructure.robot.integrationevents

import com.example.thelegend27.robot.domain.EnemyRobot
import com.example.thelegend27.robot.domainprimitives.RobotLevels
import java.util.*


data class RobotRevealedDTO(
    val levels: RobotLevels,
    val planetId: String,
    val playerNotion: String,
    var health: Int,
    var energy: Int,
    val robotId: String,
) {
    fun toEnemyRobot(): EnemyRobot {
        return EnemyRobot(UUID.fromString(robotId), levels, UUID.fromString(planetId), playerNotion, health, energy)
    }
}
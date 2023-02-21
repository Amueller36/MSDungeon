package com.example.thelegend27.robot.domainprimitives

import com.example.thelegend27.robot.domain.EnemyRobot

data class EnemyRobotEntryDetailed(
    val id: String,
    val planetId: String,
    val type: String,
    val player: String,
    val health: Int,
    val energy: Int,
    val levels: RobotLevels
) {
    companion object {
        fun fromEnemyRobot(robot: EnemyRobot): EnemyRobotEntryDetailed {
            return EnemyRobotEntryDetailed(
                id = robot.robotId.toString(),
                planetId = robot.planetId.toString(),
                type = "enemy_robot",
                player = robot.playerNotion,
                health = robot.health,
                energy = robot.energy,
                levels = robot.levels
            )
        }
    }
}
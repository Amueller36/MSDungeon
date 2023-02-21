package com.example.thelegend27.robot.domainprimitives

import com.example.thelegend27.robot.domain.EnemyRobot

data class EnemyRobotEntryMinimal(
    val id: String,
    val planetId: String,
    val type: String,
    val player: String
) {
    companion object {
        fun fromEnemyRobot(robot: EnemyRobot): EnemyRobotEntryMinimal {
            return EnemyRobotEntryMinimal(
                id = robot.robotId.toString(),
                planetId = robot.planetId.toString(),
                type = "enemy_robot",
                player = robot.playerNotion
            )
        }
    }
}

package com.example.thelegend27.robot.domainprimitives

import com.example.thelegend27.robot.domain.Robot
import java.util.*


data class FriendlyRobotEntryDetailed(
    val id: UUID,
    val planetId: UUID,
    val strategy: String,
    val type: String,
    val alive: Boolean,
    val status: CurrentStatus,
    val inventory: Inventory,
    val levels: RobotLevels
) {
    companion object {
        fun fromRobot(robot: Robot): FriendlyRobotEntryDetailed {
            return FriendlyRobotEntryDetailed(
                id = robot.id,
                planetId = robot.currentPlanet.id,
                strategy = robot.strategy::class.java.simpleName,
                type = "friendly_Robot",
                alive = robot.alive,
                status = robot.currentStatus,
                inventory = robot.inventory,
                levels = robot.levels
            )
        }
    }
}
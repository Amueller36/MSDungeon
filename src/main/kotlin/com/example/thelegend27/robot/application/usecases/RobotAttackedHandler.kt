package com.example.thelegend27.robot.application.usecases

import com.example.thelegend27.eventinfrastructure.robot.integrationevents.RobotAttackedIntegrationEvent
import com.example.thelegend27.robot.application.RobotService
import com.example.thelegend27.robot.domain.EnemyRobotRepository
import com.example.thelegend27.robot.domain.RobotRepository
import com.example.thelegend27.robot.domainprimitives.CurrentStatus
import org.slf4j.LoggerFactory
import java.util.*

class RobotAttackedHandler {
    private val logger = LoggerFactory.getLogger(RobotAttackedHandler::class.java)
    suspend fun handle(event: RobotAttackedIntegrationEvent) {
        val target = event.target
        val robotId = UUID.fromString(event.target.robotId)
        val randomUUID = UUID.randomUUID()
        val result = RobotRepository.get(robotId)
        if (result.isSuccess) {
            val ourRobot = result.getOrThrow()
            ourRobot.mutex.lock(randomUUID)
            try {
                logger.info("Robot with id $robotId just got attacked! Health: ${target.availableHealth} Energy: ${target.availableEnergy} Alive: ${target.alive} ")
                ourRobot.alive = target.alive
                ourRobot.currentStatus = CurrentStatus(target.availableHealth, target.availableEnergy)
                RobotService.addOrReplace(ourRobot)
            } finally {
                ourRobot.mutex.unlock(randomUUID)
            }
        } else {
            val enemyRobotResult = EnemyRobotRepository.get(robotId)
            if (enemyRobotResult.isSuccess) {
                val enemy = enemyRobotResult.getOrThrow()
                enemy.mutex.lock(randomUUID)
                try {
                    logger.info("Enemy Robot with id $robotId just got attacked! Health: ${target.availableHealth} Energy: ${target.availableEnergy} Alive: ${target.alive} ")
                    enemy.health = target.availableHealth
                    enemy.energy = target.availableEnergy
                    EnemyRobotRepository.addOrReplace(enemy)
                } finally {
                    enemy.mutex.unlock(randomUUID)
                }
            }
        }

    }
}
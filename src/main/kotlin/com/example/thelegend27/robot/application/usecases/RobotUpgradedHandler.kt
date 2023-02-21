package com.example.thelegend27.robot.application.usecases

import com.example.thelegend27.eventinfrastructure.robot.RobotUpgradedEvent
import com.example.thelegend27.robot.application.RobotService
import com.example.thelegend27.robot.domain.RobotRepository
import org.slf4j.LoggerFactory
import java.util.*

class RobotUpgradedHandler {
    private val logger = LoggerFactory.getLogger(RobotUpgradedHandler::class.java)

    suspend fun handle(event: RobotUpgradedEvent) {
        val result = RobotRepository.get(UUID.fromString(event.robotId))

        result.onSuccess { robot ->
            val randomUUID = UUID.randomUUID()
            robot.mutex.lock(randomUUID)
            robot.levelUp(event.upgradeType)
            logger.info("Robot ${robot.id} upgraded ${event.upgradeType} to Level ${event.level}")
            RobotService.addOrReplace(robot)
            robot.mutex.unlock(randomUUID)
        }
        result.onFailure {
            logger.error("RobotUpgraded failed! Robot: ${event.robotId} not found")
        }
    }

}
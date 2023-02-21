package com.example.thelegend27.robot.application.usecases

import com.example.thelegend27.eventinfrastructure.robot.RobotHealthUpdatedEvent
import com.example.thelegend27.robot.application.RobotService
import com.example.thelegend27.robot.domain.RobotRepository
import com.example.thelegend27.robot.domainprimitives.CurrentStatus
import org.slf4j.LoggerFactory
import java.util.*

class RobotHealthUpdatedHandler {
    private val logger = LoggerFactory.getLogger(RobotsRevealedHandler::class.java)

    suspend fun handle(event: RobotHealthUpdatedEvent) {
        val result = RobotRepository.get(UUID.fromString(event.robotId))
        result.onSuccess { robot ->
            val randomUUID = UUID.randomUUID()
            robot.mutex.lock(randomUUID)
            logger.info(
                """
                    |--------------------------------------------------|
                    |Robot ${robot.id} health updated from ${robot.currentStatus.health} to ${event.health}|
                    |--------------------------------------------------|
                """.trimIndent()
            )
            robot.currentStatus = CurrentStatus(event.health, robot.currentStatus.energy)

            RobotService.addOrReplace(robot)

            robot.mutex.unlock(randomUUID)
        }
        result.onFailure {
            logger.error("HealthUpdate failed! Robot: ${event.robotId} not found")
        }
    }
}
package com.example.thelegend27.robot.application.usecases

import com.example.thelegend27.eventinfrastructure.robot.RobotEnergyUpdatedEvent
import com.example.thelegend27.robot.application.RobotService
import com.example.thelegend27.robot.domain.RobotRepository
import com.example.thelegend27.robot.domainprimitives.CurrentStatus
import org.slf4j.LoggerFactory
import java.util.*

class RobotEnergyUpdatedHandler {
    private val logger = LoggerFactory.getLogger(RobotEnergyUpdatedHandler::class.java)
    suspend fun handle(event: RobotEnergyUpdatedEvent) {
        val result = RobotRepository.get(UUID.fromString(event.robotId))
        result.onSuccess { robot ->
            val randomUUID = UUID.randomUUID()
            robot.mutex.lock(randomUUID)
            logger.info(
                """
                   |---------------------------------------------------------|
                   |Robot ${robot.id} energy updated from ${robot.currentStatus.energy} to ${event.energy}|
                   |---------------------------------------------------------|
                    
                """.trimIndent()
            )
            robot.currentStatus = CurrentStatus(robot.currentStatus.health, event.energy)

            RobotService.addOrReplace(robot)
            robot.mutex.unlock(randomUUID)

        }
        result.onFailure {
            logger.error("EnergyUpdate failed! Robot:${event.robotId} not found")
        }
    }


}
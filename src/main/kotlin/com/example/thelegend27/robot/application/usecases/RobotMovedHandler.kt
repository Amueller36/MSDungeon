package com.example.thelegend27.robot.application.usecases

import com.example.thelegend27.eventinfrastructure.robot.RobotMovedEvent
import com.example.thelegend27.planet.application.PlanetService
import com.example.thelegend27.robot.application.RobotService
import com.example.thelegend27.robot.domain.RobotRepository
import org.slf4j.LoggerFactory
import java.util.*

class RobotMovedHandler {
    private val logger = LoggerFactory.getLogger(RobotMovedHandler::class.java)
    suspend fun handle(event: RobotMovedEvent) {
        val result = RobotRepository.get(UUID.fromString(event.robotId))
        result.onSuccess { robot ->
            val randomUUID = UUID.randomUUID()
            robot.mutex.lock(randomUUID)
            val result2 = PlanetService.getPlanetById(UUID.fromString(event.toPlanet))
            result2.onSuccess { planet ->
                robot.moveToPlanet(planet)
                RobotService.addOrReplace(robot)
                robot.mutex.unlock(randomUUID)

            }
            result2.onFailure {
                logger.error("RobotMoved failed! Planet: ${event.toPlanet} not found")
            }
        }
        result.onFailure {
            logger.error("RobotMoved failed! Robot: ${event.robotId} not found")
        }
    }

}
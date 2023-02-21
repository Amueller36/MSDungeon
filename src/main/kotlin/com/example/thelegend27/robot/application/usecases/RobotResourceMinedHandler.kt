package com.example.thelegend27.robot.application.usecases

import com.example.thelegend27.eventinfrastructure.robot.integrationevents.RobotResourceMinedIntegrationEvent
import com.example.thelegend27.robot.application.RobotService
import com.example.thelegend27.robot.domain.RobotRepository
import com.example.thelegend27.utility.throwables.EntryDoesNotExistException
import org.slf4j.LoggerFactory
import org.slf4j.MarkerFactory
import java.util.*

class RobotResourceMinedHandler {
    private val logger = LoggerFactory.getLogger(RobotResourceMinedHandler::class.java)

    suspend fun handle(event: RobotResourceMinedIntegrationEvent) {
        val result = RobotRepository.get(UUID.fromString(event.robotId))

        result.onSuccess { robot ->
            val randomUUID = UUID.randomUUID()
            robot.mutex.lock(randomUUID)
            val robotInventoryBefore = robot.inventory.usedStorage
            val robotInventoryAfter = robot.inventory.fromInventoryAndRobotResourceMined(event)
            if (robotInventoryAfter.usedStorage != 0 && robotInventoryBefore > robotInventoryAfter.usedStorage) {
                logger.error(
                    MarkerFactory.getMarker("RACECONDITION"),
                    "Robot ${robot.id} just got less inventory than he had before, probably Race Condition ${robot.inventory.resources} MAX STORAGE :${robot.inventory.maxStorage} USED STORAGE :${robot.inventory.usedStorage} Before : $robotInventoryBefore"
                )
            } else {
                robot.inventory = robotInventoryAfter
                logger.info("Robot ${robot.id} just mined something! ${robot.inventory.resources} MAX STORAGE :${robot.inventory.maxStorage} USED STORAGE :${robot.inventory.usedStorage} Before : $robotInventoryBefore")
                RobotService.addOrReplace(robot)

            }
            robot.mutex.unlock(randomUUID)
        }
        result.onFailure { throw EntryDoesNotExistException("handleRobotResourceMined: Robot with id ${event.robotId} doesnt not exist!") }
    }
}
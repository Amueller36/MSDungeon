package com.example.thelegend27.robot.application.usecases

import com.example.thelegend27.eventinfrastructure.robot.integrationevents.RobotResourceRemovedIntegrationEvent
import com.example.thelegend27.robot.application.RobotService
import com.example.thelegend27.robot.domain.RobotRepository
import com.example.thelegend27.utility.throwables.EntryDoesNotExistException
import java.util.*


class RobotResourceRemovedHandler {
    suspend fun handle(event: RobotResourceRemovedIntegrationEvent) {
        val robotId = UUID.fromString(event.robotId)
        val randomUUID = UUID.randomUUID()
        val robot = RobotRepository.get(robotId)
            .getOrElse { throw EntryDoesNotExistException("Robot: ${event.robotId} not found") }
        robot.mutex.lock(randomUUID)
        robot.inventory = robot.inventory.fromInventoryAndRobotResourceRemoved(event)
        RobotService.addOrReplace(robot)
        robot.mutex.unlock(randomUUID)
    }
}
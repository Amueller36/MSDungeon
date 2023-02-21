package com.example.thelegend27.robot.domain

import com.example.thelegend27.eventinfrastructure.Event
import com.example.thelegend27.eventinfrastructure.EventHeaderDto
import com.example.thelegend27.eventinfrastructure.map.PlanetDto
import com.example.thelegend27.eventinfrastructure.robot.*
import java.util.*

class TestHelper {

    fun getRobotEvent(): RobotDto {
        return RobotDto(
            PlanetDto(UUID.randomUUID().toString(), "1", 1, "coal"),
            RobotInventoryDto(0, 0, 20, false, ResourceInventoryDto()),
            UUID.randomUUID().toString(),
            true,
            "TheLegend27",
            10,
            20,
            4,
            1,
            2,
            10,
            20,
            0,
            0,
            0,
            0,
            0,
            0,
        )
    }

    fun createSpawnedEvent(): Event<RobotSpawnedEvent> {
        val eventHeader =
            EventHeaderDto(
                UUID.randomUUID().toString(),
                "{}",
                "${UUID.randomUUID()}",
                "robotSpawned",
                "42",
                "TheLegend27"
            )
        return Event(eventHeader, RobotSpawnedEvent(getRobotEvent()))
    }

    fun createRobotMovedEvent(robot: String, currentPlanet: String, destinationPlanet: String): RobotMovedEvent {
        return RobotMovedEvent(robot, currentPlanet, destinationPlanet)
    }

    fun createRobotAttackedEvent() {
        TODO()
    }

    fun createRobotKilledEvent(robot: String): RobotKilledEvent {
        return RobotKilledEvent(robot)
    }

    fun createRobotHealthUpdatedEvent(robot: String, health: Int): RobotHealthUpdatedEvent {
        return RobotHealthUpdatedEvent(robot, 1, health)
    }

    fun createRobotEnergyUpdatedEvent(robot: String, energy: Int): RobotEnergyUpdatedEvent {
        return RobotEnergyUpdatedEvent(robot, 0, energy)
    }

    fun createRobotResourceMinedEvent(robot: String, resource: String, amount: Int): RobotResourceMinedEvent {
        return RobotResourceMinedEvent(robot, amount, resource)
    }
}
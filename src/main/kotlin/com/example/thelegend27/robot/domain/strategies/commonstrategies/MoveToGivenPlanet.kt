package com.example.thelegend27.robot.domain.strategies.commonstrategies

import com.example.thelegend27.planet.application.PlanetService
import com.example.thelegend27.planet.domain.DiscoveredPlanet
import com.example.thelegend27.planet.domain.Planet
import com.example.thelegend27.robot.domain.Robot
import com.example.thelegend27.robot.domain.RobotRepository
import com.example.thelegend27.robot.domain.commands.Command
import com.example.thelegend27.robot.domain.commands.CommandFactory
import com.example.thelegend27.robot.domain.strategies.RobotStrategy
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class MoveToGivenPlanet(private var robot: Robot, val planet: Planet) : RobotStrategy {
    @OptIn(ExperimentalCoroutinesApi::class)
    private val robotFlow = RobotRepository.asFlow().map { it[robot.id] }

    override suspend fun getCommand(): Command? {
        robot = robotFlow.first() ?: return null
        if (robot.currentPlanet is DiscoveredPlanet) {
            if (robot.currentPlanet.id == planet.id) return null
            return moveToPlanet(robot.currentPlanet as DiscoveredPlanet, planet)
        }
        return null
    }

    private fun moveToPlanet(currentPlanet: DiscoveredPlanet, planetWithEnemy: Planet): Command {
        val nextPlanet = PlanetService.getShortestPathFromPlanetToPlanet(currentPlanet, planetWithEnemy)
        val nextPlanetId = {
            if (nextPlanet[0] != currentPlanet) nextPlanet[0].id
            else nextPlanet[1].id
        }
        return CommandFactory.createRobotMoveCommand(robot.id.toString(), nextPlanetId.toString())
    }
}
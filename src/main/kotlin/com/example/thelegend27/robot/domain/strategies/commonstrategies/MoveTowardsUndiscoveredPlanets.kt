package com.example.thelegend27.robot.domain.strategies.commonstrategies

import com.example.thelegend27.planet.application.PlanetService
import com.example.thelegend27.planet.domain.DiscoveredPlanet
import com.example.thelegend27.planet.domain.UndiscoveredPlanet
import com.example.thelegend27.robot.domain.Robot
import com.example.thelegend27.robot.domain.RobotRepository
import com.example.thelegend27.robot.domain.commands.Command
import com.example.thelegend27.robot.domain.commands.CommandFactory
import com.example.thelegend27.robot.domain.strategies.RobotStrategy
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.util.*

class MoveTowardsUndiscoveredPlanet(private var robot: Robot) : RobotStrategy {
    @OptIn(ExperimentalCoroutinesApi::class)
    private val robotFlow = RobotRepository.asFlow().map { it[robot.id] }

    override suspend fun getCommand(): Command? {
        robot = robotFlow.first() ?: return null
        val closestUndiscoveredPlanet = getClosestUndiscoveredPlanetIdInCluster()
        closestUndiscoveredPlanet?.let { planetId ->
            return CommandFactory.createRobotMoveCommand(robot.id.toString(), planetId.toString())
        }
        return null
    }

    private fun getClosestUndiscoveredPlanetIdInCluster(): UUID? {
        val currentPlanet = robot.currentPlanet as DiscoveredPlanet
        val undiscoveredPlanets =
            PlanetService.getAllPlanetsInCluster(robot.currentPlanet.clusterId)
                .filterIsInstance<UndiscoveredPlanet>()
        if (undiscoveredPlanets.isEmpty()) return null
        val closestNeighborToUndiscoveredPlanetPath =
            PlanetService.getShortestPathFromPlanetToListOfPlanets(currentPlanet, undiscoveredPlanets)

        if (closestNeighborToUndiscoveredPlanetPath.size > 1) {
            val closestNeighborToUndiscoveredPlanet =
                if (closestNeighborToUndiscoveredPlanetPath[0].id != currentPlanet.id) closestNeighborToUndiscoveredPlanetPath[0]
                else closestNeighborToUndiscoveredPlanetPath[1]
            return closestNeighborToUndiscoveredPlanet.id
        }
        return null
    }
}
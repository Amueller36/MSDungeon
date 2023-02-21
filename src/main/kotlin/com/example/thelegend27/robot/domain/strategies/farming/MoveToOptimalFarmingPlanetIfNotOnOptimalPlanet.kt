package com.example.thelegend27.robot.domain.strategies.farming

import com.example.thelegend27.planet.application.PlanetService
import com.example.thelegend27.planet.domain.DiscoveredPlanet
import com.example.thelegend27.planet.domain.UndiscoveredPlanet
import com.example.thelegend27.planet.domainprimitives.DiscoveredDeposit
import com.example.thelegend27.robot.application.RobotService
import com.example.thelegend27.robot.domain.Robot
import com.example.thelegend27.robot.domain.RobotRepository
import com.example.thelegend27.robot.domain.commands.Command
import com.example.thelegend27.robot.domain.commands.CommandFactory
import com.example.thelegend27.robot.domain.strategies.RobotStrategy
import com.example.thelegend27.trading.domain.Resource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import org.slf4j.LoggerFactory
import java.util.*

class MoveToOptimalFarmingPlanetIfNotOnOptimalPlanet(private var robot: Robot) : RobotStrategy {
    private val logger = LoggerFactory.getLogger(MoveToOptimalFarmingPlanetIfNotOnOptimalPlanet::class.java)

    @OptIn(ExperimentalCoroutinesApi::class)
    private val robotFlow = RobotRepository.asFlow().map { it[robot.id] }


    override suspend fun getCommand(): Command? {
        val robot = robotFlow.first() ?: return null
        val optimalNeighborPlanetId = getOptimalNeighborPlanet(robot)
        if (optimalNeighborPlanetId == robot.currentPlanet.id) {
            logger.info("Robot ${robot.id} is already on an optimal planet!")
            return null
        }
        logger.info("Robot ${robot.id} is not on an optimal planet. Moving to optimal neighbor planet : ${optimalNeighborPlanetId}")
        return CommandFactory.createRobotMoveCommand(robot.id.toString(), optimalNeighborPlanetId.toString())

    }


    /**
     * @return the optimal planet to farm on is a planet with a deposit and most amount of our robots on it,
     * because then it is easier to defend it.
     */
    private fun getOptimalNeighborPlanet(robot: Robot): UUID {
        val currentPlanet = robot.currentPlanet as? DiscoveredPlanet ?: return robot.currentPlanet.id
        val nearbyDiscoveredPlanets = getDiscoveredPlanetsInCluster(currentPlanet)
        val undiscoveredPlanets = getUndiscoveredPlanetsInCluster(currentPlanet)

        val planetsWithMinableResources = getOptimalResourcePlanets(robot, nearbyDiscoveredPlanets)

        val planetsWithDepositAndOurRobots = filterByPlanetsWithMostAmountOfOurRobots(planetsWithMinableResources)
        if (planetsWithDepositAndOurRobots.isEmpty() && undiscoveredPlanets.isNotEmpty()) {
            logger.info("No planets with minable resources found in cluster. Going to explore undiscovered planets")
            val path = PlanetService.getShortestPathFromPlanetToListOfPlanets(currentPlanet, undiscoveredPlanets)
            if (path.contains(currentPlanet)) return path[1].id
            return path[0].id

        } else if (planetsWithDepositAndOurRobots.isNotEmpty()) {
            logger.info("Found planets with minable resources and friendly robots in cluster. Trying to figure out shortest path to them")
            val planetWithMinableResourceAndMostFriendlyRobots = planetsWithDepositAndOurRobots.sortedBy {
                PlanetService.getShortestPathFromPlanetToPlanet(
                    currentPlanet, it
                ).size
            }[0]
            val pathToOptimalPlanet = PlanetService.getShortestPathFromPlanetToPlanet(
                currentPlanet, planetWithMinableResourceAndMostFriendlyRobots
            )
            if (planetWithMinableResourceAndMostFriendlyRobots.id == currentPlanet.id) return currentPlanet.id
            return pathToOptimalPlanet[1].id
        }
        return currentPlanet.id
    }

    private fun getDiscoveredPlanetsInCluster(currentPlanet: DiscoveredPlanet): List<DiscoveredPlanet> {
        val nearbyPlanets = currentPlanet.clusterId.let {
            PlanetService.getAllPlanetsInCluster(it)
        }
        return nearbyPlanets.filterIsInstance<DiscoveredPlanet>().toList()
    }

    private fun getUndiscoveredPlanetsInCluster(currentPlanet: DiscoveredPlanet): List<UndiscoveredPlanet> {
        val nearbyPlanets = currentPlanet.clusterId.let {
            PlanetService.getAllPlanetsInCluster(it)
        }
        return nearbyPlanets.filterIsInstance<UndiscoveredPlanet>().toList()
    }

    private fun filterByPlanetsWithMostAmountOfOurRobots(minableResourcePlanets: List<DiscoveredPlanet>): List<DiscoveredPlanet> {
        val planetsWithFriendlyRobots =
            minableResourcePlanets.sortedByDescending { RobotService.amountOfFriendlyRobotsOnPlanet(it.id) }
        return planetsWithFriendlyRobots
    }

    /**
     * Returns a list of planets with the highest resource that can be mined by the robot in the cluster.
     * It will return the next best Resource planets if there are no planets with the optimal resource.
     */
    private fun getOptimalResourcePlanets(
        currentRobot: Robot, planetsWithDepositAndNoEnemy: List<DiscoveredPlanet>
    ): List<DiscoveredPlanet> {
        // Get the optimal resource for the current mining level
        val optimalResource = Resource.getHighestMinableResourceByLevel(currentRobot.levels.miningLevel)


        // Filter only the planets that have the optimal resource
        val resourcePlanets =
            planetsWithDepositAndNoEnemy.filter { (it.deposit as? DiscoveredDeposit)?.resourceType == optimalResource }

        // If there are no planets with the optimal resource, look for a planet with the next best resource.
        if (resourcePlanets.isEmpty()) {
            var i = currentRobot.levels.miningLevel
            val resources = mutableListOf<Resource>()
            while (i >= 0) {
                resources.add(Resource.getHighestMinableResourceByLevel(i))
                i--
            }
            for (resource in resources) {
                val nextBestResourcePlanets =
                    planetsWithDepositAndNoEnemy.filter { (it.deposit as? DiscoveredDeposit)?.resourceType == resource }
                if (nextBestResourcePlanets.isNotEmpty()) {
                    return nextBestResourcePlanets
                }
            }
        }
        return resourcePlanets
    }

}

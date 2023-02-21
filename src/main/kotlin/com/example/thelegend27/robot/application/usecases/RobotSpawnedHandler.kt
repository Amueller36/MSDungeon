package com.example.thelegend27.robot.application.usecases

import com.example.thelegend27.eventinfrastructure.robot.RobotSpawnedEvent
import com.example.thelegend27.planet.application.PlanetService
import com.example.thelegend27.planet.application.PlanetService.areThereUndiscoveredPlanets
import com.example.thelegend27.robot.application.RobotService.getAmountOfOurRobots
import com.example.thelegend27.robot.domain.Robot
import com.example.thelegend27.robot.domain.RobotRepository
import com.example.thelegend27.robot.domain.strategies.ExploreStrategy
import com.example.thelegend27.robot.domain.strategies.RobotStrategy
import com.example.thelegend27.robot.domain.strategies.farming.FarmStrategy
import com.example.thelegend27.robot.domain.strategies.farming.RobotFarmStrategy
import com.example.thelegend27.robot.domain.strategies.fighting.FightStrategy
import com.example.thelegend27.robot.domainprimitives.CurrentStatus
import com.example.thelegend27.trading.domain.Resource
import java.util.*

class RobotSpawnedHandler {
    private val OPTIMAL_PERCENTAGE_OF_FARMERS = 0.80
    private val OPTIMAL_PERCENTAGE_OF_COAL_FARMERS = 0.5906
    private val OPTIMAL_PERCENTAGE_OF_IRON_FARMERS = 0.0729
    private val OPTIMAL_PERCENTAGE_OF_GEM_FARMERS = 0.02
    private val OPTIMAL_PERCENTAGE_OF_GOLD_FARMERS = 0.01
    private val OPTIMAL_PERCENTAGE_OF_PLATIN_FARMERS = 0.0036
    private val MAXIMUM_AMOUNT_OF_EXPLORERS = 3

    suspend fun handle(robotSpawned: RobotSpawnedEvent) {
        val planet =
            PlanetService.getPlanetById(UUID.fromString(robotSpawned.robot.planet.planetId)).getOrThrow()
        val robot = Robot(UUID.fromString(robotSpawned.robot.robotId), planet)
        val randomUUID = UUID.randomUUID()
        robot.mutex.lock(randomUUID)
        robot.currentStatus = CurrentStatus(robotSpawned.robot.health, robotSpawned.robot.energy)
        robot.strategy = determineStrategy(robot)
        RobotRepository.add(robot)
        robot.mutex.unlock(randomUUID)
    }

    private fun determineStrategy(robot: Robot): RobotStrategy {
        if (getPercentageOfFarmers() < OPTIMAL_PERCENTAGE_OF_FARMERS || getAmountOfOurRobots() < 45) {
            if (getPercentageOfCoalFarmers() < OPTIMAL_PERCENTAGE_OF_COAL_FARMERS) return FarmStrategy(
                robot,
                Resource.COAL
            )
            else if (getPercentageOfIronFarmers() < OPTIMAL_PERCENTAGE_OF_IRON_FARMERS) return FarmStrategy(
                robot,
                Resource.IRON
            )
            else if (getPercentageOfGemFarmers() < OPTIMAL_PERCENTAGE_OF_GEM_FARMERS) return FarmStrategy(
                robot,
                Resource.GEM
            )
            else if (getPercentageOfGoldFarmers() < OPTIMAL_PERCENTAGE_OF_GOLD_FARMERS) return FarmStrategy(
                robot,
                Resource.GOLD
            )
            else if (getPercentageOfPlatinFarmers() < OPTIMAL_PERCENTAGE_OF_PLATIN_FARMERS) return FarmStrategy(
                robot,
                Resource.PLATIN
            ) else
                return FarmStrategy(robot, Resource.COAL)
        }
        if (getAmountOfOurExplorerRobots() < MAXIMUM_AMOUNT_OF_EXPLORERS && areThereUndiscoveredPlanets()) return ExploreStrategy(
            robot
        )
        return FightStrategy(robot)
    }

    private fun getPercentageOfFarmers(): Double =
        RobotRepository.getAll().count { it.strategy is FarmStrategy } / (getAmountOfOurRobots()
            .toDouble())

    private fun getPercentageOfCoalFarmers(): Double =
        RobotRepository.getAll()
            .count { (it.strategy as? RobotFarmStrategy)?.getResourceThatShouldBeMined() == Resource.COAL } / (getAmountOfOurRobots()
            .toDouble())

    private fun getPercentageOfIronFarmers(): Double =
        RobotRepository.getAll()
            .count { (it.strategy as? RobotFarmStrategy)?.getResourceThatShouldBeMined() == Resource.IRON } / (getAmountOfOurRobots()
            .toDouble())

    private fun getPercentageOfGemFarmers(): Double =
        RobotRepository.getAll()
            .count { (it.strategy as? RobotFarmStrategy)?.getResourceThatShouldBeMined() == Resource.GEM } / (getAmountOfOurRobots()
            .toDouble())

    private fun getPercentageOfGoldFarmers(): Double =
        RobotRepository.getAll()
            .count { (it.strategy as? RobotFarmStrategy)?.getResourceThatShouldBeMined() == Resource.GOLD } / (getAmountOfOurRobots()
            .toDouble())

    private fun getPercentageOfPlatinFarmers(): Double =
        RobotRepository.getAll()
            .count { (it.strategy as? RobotFarmStrategy)?.getResourceThatShouldBeMined() == Resource.PLATIN } / (getAmountOfOurRobots()
            .toDouble())

    private fun getAmountOfOurExplorerRobots(): Int {
        return RobotRepository.getAll().count { it.strategy is ExploreStrategy }
    }


}
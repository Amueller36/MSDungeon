package com.example.thelegend27.robot.domain.strategies.commonstrategies

import com.example.thelegend27.planet.domain.DiscoveredPlanet
import com.example.thelegend27.planet.domain.PlanetRepository
import com.example.thelegend27.robot.domain.Robot
import com.example.thelegend27.robot.domain.RobotRepository
import com.example.thelegend27.robot.domain.commands.Command
import com.example.thelegend27.robot.domain.commands.CommandFactory
import com.example.thelegend27.robot.domain.strategies.RobotStrategy
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import org.slf4j.LoggerFactory

class RegenerateIfNoPlanetInfoOrLowEnergy(private var robot: Robot) : RobotStrategy {
    private val logger = LoggerFactory.getLogger(RegenerateIfNoPlanetInfoOrLowEnergy::class.java)
    private val MINIMUM_REQUIRED_ENERGY = 3

    @OptIn(ExperimentalCoroutinesApi::class)
    private val robotFlow = RobotRepository.asFlow().map { it[robot.id] }

    override suspend fun getCommand(): Command? {
        robot = robotFlow.first() ?: return null
        return if (regenerateOnlyIfUndiscoveredPlanetOrLowEnergy(robot)) CommandFactory.createRegenerateCommand(robot.id.toString())
        else null
    }


    /**
     * @return true if robot needs to regenerate or false if otherwise
     */
    private fun regenerateOnlyIfUndiscoveredPlanetOrLowEnergy(robot: Robot): Boolean {


        when (robot.currentPlanet) {
            is DiscoveredPlanet -> robot.currentPlanet as DiscoveredPlanet
            else -> {
                val planetFromRepo = PlanetRepository.get(robot.currentPlanet.id).getOrThrow()
                logger.info("Robot ${robot.id} is on undiscovered planet ${robot.currentPlanet}, regenerating\nBUT HE SHOULD BE ON PLANET $planetFromRepo")
                return true
            }
        }
        if (robot.currentStatus.energy <= MINIMUM_REQUIRED_ENERGY) return true
        return false
    }


}
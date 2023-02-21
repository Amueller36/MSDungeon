package com.example.thelegend27.robot.domain.strategies

import com.example.thelegend27.robot.application.RobotService
import com.example.thelegend27.robot.domain.Robot
import com.example.thelegend27.robot.domain.RobotRepository
import com.example.thelegend27.robot.domain.commands.Command
import com.example.thelegend27.robot.domain.commands.CommandFactory
import com.example.thelegend27.robot.domain.strategies.commonstrategies.MoveTowardsUndiscoveredPlanet
import com.example.thelegend27.robot.domain.strategies.commonstrategies.RegenerateIfNoPlanetInfoOrLowEnergy
import com.example.thelegend27.robot.domain.strategies.fighting.FightStrategy
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import org.slf4j.LoggerFactory

class ExploreStrategy(var robot: Robot) : RobotStrategy {
    private val logger = LoggerFactory.getLogger(ExploreStrategy::class.java)

    @OptIn(ExperimentalCoroutinesApi::class)
    private val robotFlow = RobotRepository.asFlow().map { it[robot.id] }


    override suspend fun getCommand(): Command {
        robot = robotFlow.first() ?: return CommandFactory.createRegenerateCommand(robot.id.toString())

        val strategies = listOf(
            RegenerateIfNoPlanetInfoOrLowEnergy(robot),
            MoveTowardsUndiscoveredPlanet(robot)
        )
        val potentialCommand = ComposeRobotStrategies(strategies).getCommand()
        if (potentialCommand != null) return potentialCommand

        logger.info("Robot ${robot.id} is a fighter now because all planets in cluster ${robot.currentPlanet.clusterId} are discovered!")
        RobotService.switchRobotStrategy(robot.id, FightStrategy(robot))
        return CommandFactory.createRegenerateCommand(robot.id.toString())
    }
}










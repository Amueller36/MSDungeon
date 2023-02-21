package com.example.thelegend27.robot.domain.strategies.fighting

import com.example.thelegend27.robot.domain.Robot
import com.example.thelegend27.robot.domain.RobotRepository
import com.example.thelegend27.robot.domain.commands.Command
import com.example.thelegend27.robot.domain.commands.CommandFactory
import com.example.thelegend27.robot.domain.strategies.ComposeRobotStrategies
import com.example.thelegend27.robot.domain.strategies.RobotStrategy
import com.example.thelegend27.robot.domain.strategies.commonstrategies.RegenerateIfNoPlanetInfoOrLowEnergy
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import org.slf4j.LoggerFactory


class FightStrategy(private var robot: Robot) : RobotStrategy {
    private val logger = LoggerFactory.getLogger(FightStrategy::class.java)

    @OptIn(ExperimentalCoroutinesApi::class)
    private val robotFlow = RobotRepository.asFlow().map { it[robot.id] }
    private val strategies = listOf(
        RegenerateIfNoPlanetInfoOrLowEnergy(robot),
        SendUpgradeCommandIfEnoughMoney(robot),
        FindAndAttackNearestEnemyFarmer(robot),
        FindAndAttackNearestBeatableEnemy(robot)
    )


    override suspend fun getCommand(): Command {
        robot = robotFlow.first() ?: return CommandFactory.createRegenerateCommand(robot.id.toString())
        return ComposeRobotStrategies(strategies).getCommand()
            ?: CommandFactory.createRegenerateCommand(robot.id.toString())
    }
}
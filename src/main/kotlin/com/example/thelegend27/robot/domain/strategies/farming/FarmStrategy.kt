package com.example.thelegend27.robot.domain.strategies.farming

import com.example.thelegend27.robot.domain.Robot
import com.example.thelegend27.robot.domain.RobotRepository
import com.example.thelegend27.robot.domain.commands.Command
import com.example.thelegend27.robot.domain.commands.CommandFactory
import com.example.thelegend27.robot.domain.strategies.ComposeRobotStrategies
import com.example.thelegend27.robot.domain.strategies.commonstrategies.AttackLowestEnemyOnPlanetIfBeatableOrFlee
import com.example.thelegend27.robot.domain.strategies.commonstrategies.RegenerateIfNoPlanetInfoOrLowEnergy
import com.example.thelegend27.trading.domain.Resource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.slf4j.LoggerFactory

class FarmStrategy(private var robot: Robot, val resourceToBeMined: Resource) :
    RobotFarmStrategy {
    private val monetenMadeLock = Mutex()
    private val logger = LoggerFactory.getLogger(FarmStrategy::class.java)

    @OptIn(ExperimentalCoroutinesApi::class)
    private val robotFlow = RobotRepository.asFlow().map { it[robot.id] }


    var monetenMade: Double = 0.0
        get() : Double {
            return runBlocking {
                monetenMadeLock.withLock {
                    return@runBlocking field
                }
            }
        }
        set(value) {
            runBlocking {
                if (value >= 0.0) {
                    monetenMadeLock.withLock {
                        field = value
                    }
                } else {
                    logger.error("Tried to set monetenMade to a negative value: $value, before it was $field")
                }
            }
        }


    override suspend fun getCommand(): Command {
        robot = robotFlow.first() ?: return CommandFactory.createRegenerateCommand(robot.id.toString())
        val strategies = listOf(
            RegenerateIfNoPlanetInfoOrLowEnergy(robot),
            AttackLowestEnemyOnPlanetIfBeatableOrFlee(robot),
            SellInventoryIfFull(robot, farmStrategy = this),
            UpgradeOrSendMoneyToUpgradeManagerWhenFullyUpgraded(robot, farmStrategy = this),
            MoveToOptimalFarmingPlanetIfNotOnOptimalPlanet(robot),
            MineOrBuyRegenerateIfTooLowEnergy(robot, this),
        )
        return ComposeRobotStrategies(strategies).getCommand()!!

    }

    override fun getResourceThatShouldBeMined(): Resource {
        return resourceToBeMined
    }


}

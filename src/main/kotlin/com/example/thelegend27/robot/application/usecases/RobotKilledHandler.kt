package com.example.thelegend27.robot.application.usecases

import com.example.thelegend27.eventinfrastructure.Channels
import com.example.thelegend27.eventinfrastructure.robot.RobotKilledEvent
import com.example.thelegend27.robot.domain.Robot
import com.example.thelegend27.robot.domain.RobotRepository
import com.example.thelegend27.robot.domain.strategies.farming.FarmStrategy
import com.example.thelegend27.trading.domain.UpgradeManager
import org.slf4j.LoggerFactory
import java.util.*

class RobotKilledHandler {
    private val logger = LoggerFactory.getLogger(RobotKilledHandler::class.java)

    suspend fun handle(event: RobotKilledEvent) {
        val result = RobotRepository.get(UUID.fromString(event.robotId))

        result.onSuccess { robot ->
            val randomUUID = UUID.randomUUID()
            robot.mutex.lock(randomUUID)
            logKilledMessage(robot)
            sendMonetenMadeToUpgradeManagerIfFarmer(robot)
            UpgradeManager.removeFighterFromMaxLevelList(robot.id)
            RobotRepository.removeElement(robot)
            robot.mutex.unlock(randomUUID)
        }
        result.onFailure {
            logger.error("RobotKilled failed! Robot: ${event.robotId} not found\nMaybe it was an enemy robot")
            //TODO: Herausfinden ob das event auch kommt wenn man ein enemy robot killed und entsprechend cool loggen.
        }
    }

    private suspend fun sendMonetenMadeToUpgradeManagerIfFarmer(robot: Robot) {
        if (robot.strategy is FarmStrategy) {
            val channelForLeftoverMoney = Channels.channelForLeftoverMoney
            val monetenMade = (robot.strategy as FarmStrategy).monetenMade
            if (monetenMade > 0) {
                logger.info(
                    """
                    | ------------------------------------------------------------------------------------------------------------------------             
                    | Robot ${robot.id} just died but had $monetenMade left over! Sending $monetenMade to UpgradeManager                      
                    | ------------------------------------------------------------------------------------------------------------------------
                    """
                )
                channelForLeftoverMoney.send(monetenMade)
            }
        }
    }

    private fun logKilledMessage(robot: Robot) {
        val robotId = robot.id
        val strategy = robot.strategy.javaClass.simpleName
        val fightingScore = robot.calculateFightingScore()
        val message = """
| ------------------------------------------------------------------------------------------             
| Robot $robotId just died!                                                                 |
| It was a $strategy                                                                        |
| Fighting score : $fightingScore                                                           |
| ------------------------------------------------------------------------------------------
"""
        logger.info(message)
    }
}
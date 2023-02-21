package com.example.thelegend27.robot.domain.strategies.farming

import com.example.thelegend27.eventinfrastructure.Channels
import com.example.thelegend27.robot.domain.Robot
import com.example.thelegend27.robot.domain.RobotRepository
import com.example.thelegend27.robot.domain.commands.Command
import com.example.thelegend27.robot.domain.commands.CommandFactory
import com.example.thelegend27.robot.domain.strategies.RobotStrategy
import com.example.thelegend27.trading.domain.Resource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory


class SellInventoryIfFull(private var robot: Robot, val farmStrategy: FarmStrategy) : RobotStrategy {
    private val TAX_RATE = when (farmStrategy.resourceToBeMined) {
        Resource.PLATIN -> 0.1
        Resource.GOLD -> 0.2
        Resource.GEM -> 0.25
        Resource.IRON -> 0.3
        Resource.COAL -> 0.4
    }
    private val logger = LoggerFactory.getLogger(SellInventoryIfFull::class.java)

    @OptIn(ExperimentalCoroutinesApi::class)
    private val robotFlow = RobotRepository.asFlow().map { it[robot.id] }

    override suspend fun getCommand(): Command? {
        robot = robotFlow.first() ?: return null
        if (robot.inventory.isFull()) {
            logger.info("Inventory Full used Storage : ${robot.inventory.usedStorage} Max Storage : ${robot.inventory.maxStorage} resources : ${robot.inventory.resources}")
            val money = robot.inventory.evaluateInventoryWorth()
            val tax = money * TAX_RATE
            val moneyAfterTax = money - tax
            farmStrategy.monetenMade += moneyAfterTax
            sendMoneyToUpgradeManager(tax)
            return CommandFactory.createSellInventoryCommand(robot.id.toString())
        }
        return null
    }

    private fun sendMoneyToUpgradeManager(money: Double) {
        CoroutineScope(Dispatchers.IO).launch {
            Channels.channelForLeftoverMoney.send(money)
        }
    }
}


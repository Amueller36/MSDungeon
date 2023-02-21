package com.example.thelegend27.robot.domain.strategies.farming

import com.example.thelegend27.eventinfrastructure.Channels
import com.example.thelegend27.robot.domain.Robot
import com.example.thelegend27.robot.domain.RobotRepository
import com.example.thelegend27.robot.domain.commands.Command
import com.example.thelegend27.robot.domain.commands.CommandFactory
import com.example.thelegend27.robot.domain.strategies.RobotStrategy
import com.example.thelegend27.robot.domainprimitives.RobotLevels
import com.example.thelegend27.trading.domain.Resource
import com.example.thelegend27.trading.domain.Upgrade
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class UpgradeOrSendMoneyToUpgradeManagerWhenFullyUpgraded(
    private var robot: Robot, val farmStrategy: FarmStrategy
) : RobotStrategy {
    @OptIn(ExperimentalCoroutinesApi::class)
    private val robotFlow = RobotRepository.asFlow().map { it[robot.id] }

    override suspend fun getCommand(): Command? {
        robot = robotFlow.first() ?: return null
        if (shouldUpgradeRobot(robot.levels)) {
            upgradeFarmerRelevantStuff()?.let { return it }
        } else {
            sendMoneyToUpgradeManager(farmStrategy.monetenMade)
            farmStrategy.monetenMade = 0.0
        }
        return null
    }

    private val OPTIMAL_ENERGY_REGEN_LEVEL = 3
    private val OPTIMAL_MINING_SPEED_LEVEL = 3
    private val OPTIMAL_STORAGE_LEVEL = 3


    private fun shouldUpgradeRobot(robotLevels: RobotLevels): Boolean {
        return robotLevels.miningLevel < farmStrategy.resourceToBeMined.getRequiredLevel() || robotLevels.miningSpeedLevel < OPTIMAL_MINING_SPEED_LEVEL || robotLevels.energyRegenLevel < OPTIMAL_ENERGY_REGEN_LEVEL || robotLevels.storageLevel < OPTIMAL_STORAGE_LEVEL
    }

    private fun hasEnoughMoneyForUpgrade(currentUpgradeLevel: Int, upgrade: Upgrade): Boolean {
        val upgradePrice = getPriceForNextUpgrade(currentUpgradeLevel, upgrade)
        return farmStrategy.monetenMade >= upgradePrice
    }

    private fun getPriceForNextUpgrade(currentUpgradeLevel: Int, upgrade: Upgrade): Int {
        val nextMiningLevel = currentUpgradeLevel + 1
        return upgrade.getPrice(nextMiningLevel)
    }

    private fun upgradeFarmerRelevantStuff(): Command? {
        return upgradeMiningIfEnoughMoney() ?: upgradeMiningSpeedIfEnoughMoney()
        ?: if (farmStrategy.resourceToBeMined == Resource.PLATIN) {
            upgradeMaxEnergyIfEnoughMoney() ?: upgradeStorageIfEnoughMoney()
        } else upgradeEnergyRegenIfEnoughMoney() ?: upgradeStorageIfEnoughMoney()
    }

    private fun upgradeMiningIfEnoughMoney(): Command? {
        val robotLevels = robot.levels
        if (robotLevels.miningLevel == Upgrade.MAXLEVEL) return null
        if (hasEnoughMoneyForUpgrade(
                robotLevels.miningLevel, Upgrade.MINING
            ) && robotLevels.miningLevel < farmStrategy.resourceToBeMined.getRequiredLevel()
        ) {
            farmStrategy.monetenMade -= getPriceForNextUpgrade(robotLevels.miningLevel, Upgrade.MINING)
            return CommandFactory.createUpgradeCommand(robot.id.toString(), Upgrade.MINING, robotLevels.miningLevel + 1)
        }
        return null
    }

    private fun upgradeMaxEnergyIfEnoughMoney(): Command? {
        val robotLevels = robot.levels
        if (robotLevels.energyLevel == Upgrade.MAXLEVEL) return null
        if (hasEnoughMoneyForUpgrade(
                robotLevels.energyLevel, Upgrade.MAX_ENERGY
            ) && robotLevels.energyLevel < farmStrategy.resourceToBeMined.getRequiredLevel()
        ) {
            farmStrategy.monetenMade -= getPriceForNextUpgrade(robotLevels.energyLevel, Upgrade.MAX_ENERGY)
            return CommandFactory.createUpgradeCommand(
                robot.id.toString(), Upgrade.MAX_ENERGY, robotLevels.energyLevel + 1
            )
        }
        return null
    }

    private fun upgradeMiningSpeedIfEnoughMoney(): Command? {
        val robotLevels = robot.levels
        if (robotLevels.miningSpeedLevel == Upgrade.MAXLEVEL) return null
        if (hasEnoughMoneyForUpgrade(robotLevels.miningSpeedLevel, Upgrade.MINING_SPEED)) {
            farmStrategy.monetenMade -= getPriceForNextUpgrade(robotLevels.miningSpeedLevel, Upgrade.MINING_SPEED)
            return CommandFactory.createUpgradeCommand(
                robot.id.toString(), Upgrade.MINING_SPEED, robotLevels.miningSpeedLevel + 1
            )
        }
        return null
    }

    private fun upgradeStorageIfEnoughMoney(): Command? {
        val robotLevels = robot.levels
        if (robotLevels.storageLevel == Upgrade.MAXLEVEL) return null
        if (hasEnoughMoneyForUpgrade(robotLevels.storageLevel, Upgrade.STORAGE)) {
            farmStrategy.monetenMade -= getPriceForNextUpgrade(robotLevels.storageLevel, Upgrade.STORAGE)
            return CommandFactory.createUpgradeCommand(
                robot.id.toString(), Upgrade.STORAGE, robotLevels.storageLevel + 1
            )
        }
        return null
    }

    private fun upgradeEnergyRegenIfEnoughMoney(): Command? {
        val robotLevels = robot.levels
        if (robotLevels.energyRegenLevel == Upgrade.MAXLEVEL) return null

        if (hasEnoughMoneyForUpgrade(robotLevels.energyRegenLevel, Upgrade.ENERGY_REGEN)) {
            return CommandFactory.createUpgradeCommand(
                robot.id.toString(), Upgrade.ENERGY_REGEN, robot.levels.energyRegenLevel + 1
            )
        }
        return null
    }

    private fun sendMoneyToUpgradeManager(amount: Double) {
        val channelForLeftOverMoney = Channels.channelForLeftoverMoney
        CoroutineScope(Dispatchers.IO).launch {
            channelForLeftOverMoney.send(amount)
        }
    }

}
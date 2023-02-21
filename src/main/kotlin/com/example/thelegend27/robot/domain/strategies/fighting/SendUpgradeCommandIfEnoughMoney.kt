package com.example.thelegend27.robot.domain.strategies.fighting

import com.example.thelegend27.robot.domain.Robot
import com.example.thelegend27.robot.domain.RobotRepository
import com.example.thelegend27.robot.domain.commands.Command
import com.example.thelegend27.robot.domain.commands.CommandFactory
import com.example.thelegend27.robot.domain.strategies.RobotStrategy
import com.example.thelegend27.trading.domain.Upgrade
import com.example.thelegend27.trading.domain.UpgradeManager
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import org.slf4j.LoggerFactory

class SendUpgradeCommandIfEnoughMoney(val robot: Robot) : RobotStrategy {
    private val logger = LoggerFactory.getLogger(FightStrategy::class.java)

    @OptIn(ExperimentalCoroutinesApi::class)
    private val robotFlow = RobotRepository.asFlow().map { it[robot.id] }

    private val fightingLevels = UpgradeManager.getFightingLevels(robot.id)

    override suspend fun getCommand(): Command? {
        upgradeCommandIfShouldBuyUpgrade()?.let {
            return it
        }
        return null
    }

    private suspend fun upgradeCommandIfShouldBuyUpgrade(): Command? {
        val robot = robotFlow.first() ?: return null
        val healthLevel = robot.levels.healthLevel
        val damageLevel = robot.levels.damageLevel
        val energyLevel = robot.levels.energyLevel
        if (healthLevel >= fightingLevels[Upgrade.HEALTH]!! && damageLevel >= fightingLevels[Upgrade.DAMAGE]!! && energyLevel >= fightingLevels[Upgrade.MAX_ENERGY]!!) {
            logger.info("Robot ${robot.id} does not need to upgrade any more")
            return null
        }
        listOf(
            Pair(healthLevel, Upgrade.HEALTH),
            Pair(damageLevel, Upgrade.DAMAGE),
            Pair(energyLevel, Upgrade.MAX_ENERGY)
        )
            .sortedBy { (level, upgrade) -> level }
            .forEach { (level, upgrade) ->
                if (level + 1 <= fightingLevels[upgrade]!! &&
                    UpgradeManager.shouldBuyUpgrade(upgrade, level + 1)
                )
                    return CommandFactory.createUpgradeCommand(robot.id.toString(), upgrade, level + 1)

            }
        return null
    }
}



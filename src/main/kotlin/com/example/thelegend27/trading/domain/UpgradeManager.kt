package com.example.thelegend27.trading.domain

import com.example.thelegend27.game.application.GameClient
import com.example.thelegend27.robot.application.RobotService
import com.example.thelegend27.robot.domain.Robot
import com.example.thelegend27.robot.domain.strategies.farming.FarmStrategy
import com.example.thelegend27.robot.domain.strategies.fighting.FightStrategy
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.slf4j.LoggerFactory
import java.util.*

/**
 * This class is responsible for buying new robots and managing upgrades for fighters
 */
object UpgradeManager {
    private const val PUFFERAMOUNT = 500.0
    private val casualFighterLevels = mapOf(Upgrade.HEALTH to 3, Upgrade.DAMAGE to 3, Upgrade.MAX_ENERGY to 3)
    private val lock = Mutex()
    private val logger = LoggerFactory.getLogger(UpgradeManager::class.java)
    private var monetenForNewRobotsAndTheirUpgrades = 0.0
    private val fightersThatUpgradeToMaxLevel = mutableListOf<UUID>()
    private val maxFighterLevels = mapOf(
        Upgrade.HEALTH to 5, Upgrade.DAMAGE to 5, Upgrade.MAX_ENERGY to 3
    )


    private fun getAvailableMoneyForRobotsAndUpgrades(): Double {
        return monetenForNewRobotsAndTheirUpgrades - PUFFERAMOUNT
    }

    suspend fun increaseAvailableMoney(amount: Double): Double {
        lock.withLock {
            monetenForNewRobotsAndTheirUpgrades += amount
            return monetenForNewRobotsAndTheirUpgrades
        }
    }

    private fun decreaseAvailableMoney(amount: Double) {
        if (monetenForNewRobotsAndTheirUpgrades < amount) throw Exception("Not enough money available")
        if (amount < 0) throw Exception("Money amount  must be positive")
        monetenForNewRobotsAndTheirUpgrades -= amount
    }

    suspend fun shouldBuyUpgrade(upgrade: Upgrade, level: Int): Boolean {
        lock.withLock {
            val availableMoney = getAvailableMoneyForRobotsAndUpgrades()
            if (availableMoney >= upgrade.getPrice(level)) {
                logger.info("Buying upgrade ${upgrade.name}, level $level for ${upgrade.getPrice(level)}")
                decreaseAvailableMoney(upgrade.getPrice(level).toDouble())
                return true
            }
            return false
        }
    }

    suspend fun buyNewRobotsIfFightersAreUpgraded() {
        if (fightersAreFullyUpgraded()) {
            lock.withLock {
                val availableMoney = getAvailableMoneyForRobotsAndUpgrades()

                val numberOfNewRobots = (availableMoney / Item.ROBOT.value()).toInt()
                if (numberOfNewRobots > 0) {
                    val pricePaidForNewRobots = (numberOfNewRobots * Item.ROBOT.value()).toDouble()
                    logger.info("Buying $numberOfNewRobots new robots")
                    GameClient.buyXRobots(numberOfNewRobots)
                    decreaseAvailableMoney(pricePaidForNewRobots)
                }
            }
        }
        logger.info("Fighters are not fully upgraded yet. Not buying new robots")
    }

    fun removeFighterFromMaxLevelList(robotId: UUID) {
        fightersThatUpgradeToMaxLevel.remove(robotId)
    }

    /**
     * For every 100 farmers we can afford one fighter to be upgraded to max level.
     * @return this method returns the levels that a fighter should be upgraded to
     */
    fun getFightingLevels(robotId: UUID): Map<Upgrade, Int> {
        return runBlocking {
            lock.withLock {
                val amountOfFarmers = RobotService.getAllRobots().count { it.strategy is FarmStrategy }
                val canAffordMaxLevelFighter = (amountOfFarmers - fightersThatUpgradeToMaxLevel.size * 100) > 100
                if (canAffordMaxLevelFighter) {
                    logger.info("One fighter is allowed to be upgraded to max level")
                    fightersThatUpgradeToMaxLevel.add(robotId)
                    return@runBlocking maxFighterLevels
                }
                return@runBlocking casualFighterLevels
            }
        }

    }

    private fun getFighters(): List<Robot> {
        return RobotService.getAllRobots().filter { it.strategy is FightStrategy }
    }

    private fun areMaxFightersFullyUpgraded(): Boolean {
        val maxFighters = getFighters().filter { it.id in fightersThatUpgradeToMaxLevel }
        if (maxFighters.isEmpty()) return true
        return maxFighters.all {
            it.levels.healthLevel >= maxFighterLevels[Upgrade.HEALTH]!! && it.levels.damageLevel >= maxFighterLevels[Upgrade.DAMAGE]!! && it.levels.energyLevel >= maxFighterLevels[Upgrade.MAX_ENERGY]!!
        }

    }

    private fun areNormalFightersFullyUpgraded(): Boolean {
        val normalFighters = getFighters().filter { it.id !in fightersThatUpgradeToMaxLevel }
        if (normalFighters.isEmpty()) return true
        return normalFighters.all {
            it.levels.healthLevel >= casualFighterLevels[Upgrade.HEALTH]!! && it.levels.damageLevel >= casualFighterLevels[Upgrade.DAMAGE]!! && it.levels.energyLevel >= casualFighterLevels[Upgrade.MAX_ENERGY]!!
        }
    }

    private fun fightersAreFullyUpgraded(): Boolean {
        return areMaxFightersFullyUpgraded() && areNormalFightersFullyUpgraded()
    }

}



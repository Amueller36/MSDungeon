package com.example.thelegend27.robot.domain


import com.example.thelegend27.planet.domain.Planet
import com.example.thelegend27.robot.domain.strategies.RobotStrategy
import com.example.thelegend27.robot.domain.strategies.farming.FarmStrategy
import com.example.thelegend27.robot.domainprimitives.*
import com.example.thelegend27.trading.domain.Resource
import com.example.thelegend27.trading.domain.Upgrade
import kotlinx.coroutines.sync.Mutex
import java.util.*

class Robot(
    val id: UUID,
    var currentPlanet: Planet
) {

    var alive: Boolean = true

    val mutex = Mutex()

    var strategy: RobotStrategy = FarmStrategy(this, Resource.COAL)

    var levels: RobotLevels = RobotLevels()

    private var stats: RobotStats
        get() = RobotStats(
            Upgrade.HEALTH.getValue(levels.healthLevel),
            Upgrade.MAX_ENERGY.getValue(levels.energyLevel),
            Upgrade.ENERGY_REGEN.getValue(levels.energyRegenLevel),
            Upgrade.DAMAGE.getValue(levels.damageLevel),
            Upgrade.MINING_SPEED.getValue(levels.miningSpeedLevel),
            Upgrade.MINING.getValue(levels.miningLevel)
        )
        set(value) {
            stats = value
        }

    var currentStatus = CurrentStatus(stats.maxHealth, stats.maxEnergy)

    var inventory = Inventory.fromMaxStorage(Upgrade.STORAGE.getValue(levels.storageLevel))
        get() {
            val itemBag = ItemBag.fromAmount(field.itemBag)
            val resources = Resources.fromAmount(field.resources)
            return field.fromMaxStorageResourcesAndItems(
                Upgrade.STORAGE.getValue(levels.storageLevel),
                resources,
                itemBag
            )
        }

    fun levelUp(
        upgrade: Upgrade
    ) {
        when (upgrade) {
            Upgrade.STORAGE -> {
                levels.upgradeStorage()
            }

            Upgrade.DAMAGE -> {
                levels.upgradeAttack()
            }

            Upgrade.MAX_ENERGY -> {
                levels.upgradeEnergy()
            }

            Upgrade.ENERGY_REGEN -> {
                levels.upgradeEnergyRegen()
            }

            Upgrade.HEALTH -> {
                levels.upgradeHealth()
            }

            Upgrade.MINING -> {
                levels.upgradeMining()
            }

            Upgrade.MINING_SPEED -> {
                levels.upgradeMiningSpeed()
            }
        }
    }

    fun moveToPlanet(planet: Planet) {
        currentPlanet = planet
    }

    fun calculateFightingScore(): Double {
        val attackEnergyCost = 2.0
        return levels.healthLevel + (currentStatus.energy.toDouble() * levels.damageLevel.toDouble() / attackEnergyCost) + levels.energyLevel * 0.25
    }
}


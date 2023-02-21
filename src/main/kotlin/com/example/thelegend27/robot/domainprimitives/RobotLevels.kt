package com.example.thelegend27.robot.domainprimitives


class RobotLevels(
    var healthLevel: Int = 0,
    var damageLevel: Int = 0,
    var miningSpeedLevel: Int = 0,
    var miningLevel: Int = 0,
    var energyLevel: Int = 0,
    var energyRegenLevel: Int = 0,
    var storageLevel: Int = 0,
) {
    companion object {
        val MAXIMUMLEVEL = 5
    }

    fun sumOfLevels(): Int {
        return healthLevel + damageLevel + miningSpeedLevel + miningLevel + energyLevel + energyRegenLevel + storageLevel
    }

    fun upgradeHealth() {
        if (this.healthLevel >= MAXIMUMLEVEL) {
            throw IllegalArgumentException("Health level cannot be greater than 5")
        }
        healthLevel++
    }

    fun upgradeAttack() {
        if (this.damageLevel >= MAXIMUMLEVEL) {
            throw IllegalArgumentException("Attack level cannot be greater than 5")
        }
        damageLevel++
    }

    fun upgradeMiningSpeed() {
        if (this.miningSpeedLevel >= MAXIMUMLEVEL) {
            throw IllegalArgumentException("Mining speed level cannot be greater than 5")
        }
        miningSpeedLevel++
    }

    fun upgradeMining() {
        if (this.miningLevel >= MAXIMUMLEVEL) {
            throw IllegalArgumentException("Mining level cannot be greater than 5")
        }
        miningLevel++
    }

    fun upgradeEnergy() {
        if (this.energyLevel >= MAXIMUMLEVEL) {
            throw IllegalArgumentException("Energy level cannot be greater than 5")
        }
        energyLevel++
    }

    fun upgradeEnergyRegen() {
        if (this.energyRegenLevel > MAXIMUMLEVEL) {
            throw IllegalArgumentException("Energy regen level cannot be greater than 5")
        }
        energyRegenLevel++

    }

    fun upgradeStorage() {
        if (this.storageLevel >= MAXIMUMLEVEL) {
            throw IllegalArgumentException("Storage level cannot be greater than 5")
        }
        storageLevel++
    }

    fun isLevelZero(): Boolean {
        return healthLevel == 0 && damageLevel == 0 && miningSpeedLevel == 0 && miningLevel == 0 && energyLevel == 0 && energyRegenLevel == 0 && storageLevel == 0
    }
}
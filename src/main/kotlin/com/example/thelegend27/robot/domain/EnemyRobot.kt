package com.example.thelegend27.robot.domain

import com.example.thelegend27.robot.domainprimitives.RobotLevels
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import java.util.*

class EnemyRobot(
    val robotId: UUID,
    val levels: RobotLevels,
    val planetId: UUID,
    val playerNotion: String,
    var health: Int,
    var energy: Int,

    ) {
    val mutex: Mutex = Mutex()
    fun calculateFightingScore(): Double {
        val attackEnergyCost = 2.0
        return levels.healthLevel + (energy.toDouble() * levels.damageLevel.toDouble() / attackEnergyCost) + levels.energyLevel * 0.25
    }

    fun decreaseHealthBy(amount: Int) {
        runBlocking {
            mutex.lock()
            if (health - amount < 0) {
                health = 0
            } else {
                health -= amount
            }
            mutex.unlock()
        }
    }
}




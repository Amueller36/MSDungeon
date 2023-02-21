package com.example.thelegend27.robot.domain.strategies.fighting

import com.example.thelegend27.robot.domain.EnemyRobot
import com.example.thelegend27.robot.domain.Robot
import com.example.thelegend27.robot.domain.RobotRepository
import com.example.thelegend27.robot.domain.commands.Command
import com.example.thelegend27.robot.domain.strategies.RobotStrategy
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class FindAndAttackNearestEnemyFarmer(private var robot: Robot) : RobotStrategy {
    private val robotFlow = RobotRepository.asFlow().map { it[robot.id] }

    override suspend fun getCommand(): Command? {
        robot = robotFlow.first() ?: return null
        FindAndAttackNearestEnemy(robot, listOf(::filterByPotentitalFarmers)).getCommand()?.let { return it }
        return null
    }

    fun filterByPotentitalFarmers(robots: List<EnemyRobot>): List<EnemyRobot> {
        return robots.filter { it.levels.miningLevel >= 1 || it.levels.miningSpeedLevel > 0 || it.levels.storageLevel > 0 }
    }

}
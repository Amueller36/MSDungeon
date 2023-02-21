package com.example.thelegend27.robot.domain.strategies.fighting

import com.example.thelegend27.robot.domain.EnemyRobot
import com.example.thelegend27.robot.domain.Robot
import com.example.thelegend27.robot.domain.RobotRepository
import com.example.thelegend27.robot.domain.commands.Command
import com.example.thelegend27.robot.domain.strategies.RobotStrategy
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class FindAndAttackNearestBeatableEnemy(private var robot: Robot) : RobotStrategy {
    @OptIn(ExperimentalCoroutinesApi::class)
    private val robotFlow = RobotRepository.asFlow().map { it[robot.id] }

    override suspend fun getCommand(): Command? {
        robot = robotFlow.first() ?: return null
        FindAndAttackNearestEnemy(robot, listOf(::getBeatableEnemyRobots)).getCommand()?.let { return it }
        return null
    }

    private fun getBeatableEnemyRobots(enemyRobots: List<EnemyRobot>): List<EnemyRobot> =
        enemyRobots.filter { it.calculateFightingScore() < robot.calculateFightingScore() }


}
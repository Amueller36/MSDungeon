package com.example.thelegend27.robot.domain.strategies.commonstrategies

import com.example.thelegend27.robot.domain.EnemyRobot
import com.example.thelegend27.robot.domain.Robot
import com.example.thelegend27.robot.domain.commands.Command
import com.example.thelegend27.robot.domain.commands.CommandFactory
import com.example.thelegend27.robot.domain.strategies.RobotStrategy
import com.example.thelegend27.trading.domain.Upgrade

class AttackTarget(private var robot: Robot, val enemyRobot: EnemyRobot) : RobotStrategy {
    override suspend fun getCommand(): Command {
        val damageWeDeal = Upgrade.DAMAGE.getValue(robot.levels.damageLevel)
        enemyRobot.decreaseHealthBy(damageWeDeal)
        return CommandFactory.createAttackCommand(robot.id.toString(), enemyRobot.robotId.toString())
    }
}
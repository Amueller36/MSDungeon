package com.example.thelegend27.robot.domain.strategies

import com.example.thelegend27.robot.domain.commands.Command

class ComposeRobotStrategies(private val strategies: List<RobotStrategy>) : RobotStrategy {

    override suspend fun getCommand(): Command? {
        for (strategy in strategies) {
            val command = strategy.getCommand()
            if (command != null) {
                return command
            }
        }
        return null
    }
}
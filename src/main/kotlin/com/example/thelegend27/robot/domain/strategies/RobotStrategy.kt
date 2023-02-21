package com.example.thelegend27.robot.domain.strategies

import com.example.thelegend27.robot.domain.commands.Command

interface RobotStrategy {
    suspend fun getCommand(): Command?

}
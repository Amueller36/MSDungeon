package com.example.thelegend27.robot.domain.strategies.farming

import com.example.thelegend27.robot.domain.strategies.RobotStrategy
import com.example.thelegend27.trading.domain.Resource

interface RobotFarmStrategy : RobotStrategy {
    fun getResourceThatShouldBeMined(): Resource
}
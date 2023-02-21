package com.example.thelegend27.robot.domainprimitives

import com.example.thelegend27.robot.domain.Robot
import com.example.thelegend27.robot.domain.strategies.ExploreStrategy
import com.example.thelegend27.robot.domain.strategies.farming.RobotFarmStrategy
import com.example.thelegend27.robot.domain.strategies.fighting.FightStrategy
import com.example.thelegend27.trading.domain.Resource
import java.util.*

data class FriendlyRobotEntryMinimal(
    val id: UUID,
    val planetId: UUID,
    val strategy: String,
    val type: String
) {
    companion object {
        fun fromRobot(robot: Robot): FriendlyRobotEntryMinimal {
            return FriendlyRobotEntryMinimal(
                id = robot.id,
                planetId = robot.currentPlanet.id,
                strategy = when (robot.strategy) {
                    is FightStrategy -> "fighter"
                    is ExploreStrategy -> "explorer"
                    is RobotFarmStrategy -> when ((robot.strategy as RobotFarmStrategy).getResourceThatShouldBeMined()) {
                        Resource.COAL -> "farmer_coal"
                        Resource.GEM -> "farmer_gem"
                        Resource.GOLD -> "farmer_gold"
                        Resource.IRON -> "farmer_iron"
                        Resource.PLATIN -> "farmer_platin"
                    }

                    else -> robot.strategy.javaClass.simpleName
                },
                type = "friendly_Robot"

            )
        }
    }
}

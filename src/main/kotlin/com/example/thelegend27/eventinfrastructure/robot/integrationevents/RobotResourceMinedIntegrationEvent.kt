package com.example.thelegend27.eventinfrastructure.robot.integrationevents

import com.example.thelegend27.eventinfrastructure.robot.ResourceInventoryDto
import com.example.thelegend27.trading.domain.Resource

data class RobotResourceMinedIntegrationEvent(
    val robotId: String,
    val minedAmount: Int,
    val minedResource: Resource,
    val resourceInventory: ResourceInventoryDto
)
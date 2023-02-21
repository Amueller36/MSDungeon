package com.example.thelegend27.eventinfrastructure.robot.integrationevents

import com.example.thelegend27.eventinfrastructure.robot.ResourceInventoryDto
import com.example.thelegend27.trading.domain.Resource

data class RobotResourceRemovedIntegrationEvent(
    val robotId: String,
    val removedAmount: Int,
    val removedResource: Resource,
    val resourceInventory: ResourceInventoryDto
)
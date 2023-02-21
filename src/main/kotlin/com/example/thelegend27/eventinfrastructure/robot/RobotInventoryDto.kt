package com.example.thelegend27.eventinfrastructure.robot

data class RobotInventoryDto(
    val storageLevel: Int,
    val usedStorage: Int,
    val maxStorage: Int,
    val full: Boolean,
    val resources: ResourceInventoryDto,
)
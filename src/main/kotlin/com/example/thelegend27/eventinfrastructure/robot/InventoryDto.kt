package com.example.thelegend27.eventinfrastructure.robot

data class InventoryDto(
    val storageLevel: Int,
    val usedStorage: Int,
    val maxStorage: Int,
    val full: Boolean,
    val resourceInventory: ResourceInventoryDto,
)
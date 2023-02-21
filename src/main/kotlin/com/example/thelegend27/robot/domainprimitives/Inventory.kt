package com.example.thelegend27.robot.domainprimitives

import com.example.thelegend27.eventinfrastructure.robot.integrationevents.RobotResourceMinedIntegrationEvent
import com.example.thelegend27.eventinfrastructure.robot.integrationevents.RobotResourceRemovedIntegrationEvent

data class Inventory private constructor(
    val maxStorage: Int,
    val resources: Resources = Resources.fromNothing(),
    val itemBag: ItemBag = ItemBag.fromNothing()
) {

    companion object {
        fun fromMaxStorage(maxStorage: Int): Inventory {
            if (maxStorage < 0) {
                throw IllegalArgumentException("Max storage cannot be negative")
            }
            return Inventory(maxStorage)
        }
    }

    val usedStorage: Int
        get() = calculateUsedStorage()

    private fun calculateUsedStorage(): Int {
        var value = 0
        for (resource in resources) {
            value += resource.value
        }
        return value
    }

    fun fromInventoryAndRobotResourceRemoved(robotResourceRemoved: RobotResourceRemovedIntegrationEvent): Inventory {
        val resources = Resources.fromResourceRemovedIntegrationEvent(robotResourceRemoved)
        return Inventory(maxStorage, resources, itemBag)
    }

    fun fromMaxStorageResourcesAndItems(maxStorage: Int, resources: Resources, itemBag: ItemBag): Inventory {
        return Inventory(maxStorage, resources, itemBag)
    }

    fun fromInventoryAndRobotResourceMined(robotResourceMined: RobotResourceMinedIntegrationEvent): Inventory {
        return Inventory(maxStorage, Resources.fromResourceMinedIntegrationEvent(robotResourceMined), itemBag)
    }

    fun evaluateInventoryWorth(): Int {
        var value = 0
        for (res in resources) {
            value += res.value * res.key.value()
        }
        return value
    }

    fun isFull(): Boolean = usedStorage >= maxStorage

}

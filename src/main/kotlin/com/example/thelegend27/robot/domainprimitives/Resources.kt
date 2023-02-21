package com.example.thelegend27.robot.domainprimitives

import com.example.thelegend27.eventinfrastructure.robot.ResourceInventoryDto
import com.example.thelegend27.eventinfrastructure.robot.integrationevents.RobotResourceMinedIntegrationEvent
import com.example.thelegend27.eventinfrastructure.robot.integrationevents.RobotResourceRemovedIntegrationEvent
import com.example.thelegend27.trading.domain.Resource

class Resources private constructor() : HashMap<Resource, Int>() {

    init {
        Resource.values().forEach { this[it] = 0 }
    }

    companion object {
        fun fromAmount(amounts: Map<Resource, Int>): Resources {
            val resources = Resources()
            for (resource in amounts) {
                resources[resource.key] = resource.value
            }
            return resources
        }

        fun fromNothing(): Resources {
            return Resources()
        }

        fun fromResourceMinedIntegrationEvent(robotResourceMined: RobotResourceMinedIntegrationEvent): Resources {
            return resourceFromResourceInventoryDto(robotResourceMined.resourceInventory)
        }

        fun fromResourceRemovedIntegrationEvent(robotResourceRemoved: RobotResourceRemovedIntegrationEvent): Resources {
            return resourceFromResourceInventoryDto(robotResourceRemoved.resourceInventory)
        }

        private fun resourceFromResourceInventoryDto(resourceInventoryDto: ResourceInventoryDto): Resources {
            val resources = Resources()
            resources[Resource.COAL] = resourceInventoryDto.COAL
            resources[Resource.IRON] = resourceInventoryDto.IRON
            resources[Resource.GOLD] = resourceInventoryDto.GOLD
            resources[Resource.GEM] = resourceInventoryDto.GEM
            resources[Resource.PLATIN] = resourceInventoryDto.PLATIN
            return resources
        }
    }


}
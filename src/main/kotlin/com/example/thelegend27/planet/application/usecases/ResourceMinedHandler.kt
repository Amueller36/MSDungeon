package com.example.thelegend27.planet.application.usecases

import com.example.thelegend27.eventinfrastructure.map.PlanetResourceMinedEvent
import com.example.thelegend27.planet.domain.DiscoveredPlanet
import com.example.thelegend27.planet.domain.PlanetRepository
import com.example.thelegend27.planet.domainprimitives.DiscoveredDeposit
import com.example.thelegend27.planet.domainprimitives.NoDeposit
import org.slf4j.LoggerFactory
import java.util.*

class ResourceMinedHandler {
    private val logger = LoggerFactory.getLogger(ResourceMinedHandler::class.java)


    suspend fun handle(event: PlanetResourceMinedEvent) {
        logger.info("Updating Resource on Planet ${event.planetId}")

        PlanetRepository
            .get(UUID.fromString(event.planetId)).onSuccess {
                it.mutex.lock()
                if (it is DiscoveredPlanet) {
                    if (event.resource.currentAmount == 0) {
                        if (it.deposit !is NoDeposit)
                            logger.info("Deposit on Planet ${it.id} is depleted")
                        it.deposit = NoDeposit
                    } else
                        it.deposit = DiscoveredDeposit(
                            event.resource.type!!,
                            event.resource.maxAmount,
                            event.resource.currentAmount
                        )
                    PlanetRepository.addOrReplace(it)
                }
                it.mutex.unlock()
            }


    }
}
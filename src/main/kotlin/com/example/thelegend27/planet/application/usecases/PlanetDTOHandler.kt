package com.example.thelegend27.planet.application.usecases

import com.example.thelegend27.eventinfrastructure.map.PlanetDto
import com.example.thelegend27.planet.application.PlanetService
import com.example.thelegend27.planet.domain.*
import com.example.thelegend27.planet.domainprimitives.Deposit
import com.example.thelegend27.planet.domainprimitives.NoDeposit
import com.example.thelegend27.planet.domainprimitives.UndiscoveredDeposit
import com.example.thelegend27.trading.domain.Resource
import org.slf4j.LoggerFactory
import java.util.*

class PlanetDTOHandler {
    private val logger = LoggerFactory.getLogger(PlanetDTOHandler::class.java)
    //private val planets = PlanetRepository
    //private val clusters = ClusterRepository

    suspend fun handle(planetDTO: PlanetDto) {

        PlanetRepository.get(UUID.fromString(planetDTO.planetId))
            .onFailure {
                val newCluster = generateNewCluster()
                val newPlanet = planetFromPlanetDTO(planetDTO, newCluster)
                newCluster.addToCluster(newPlanet)
                PlanetRepository.add(newPlanet)
                ClusterRepository.add(newCluster)
            }
            .onSuccess { planet ->
                planet.mutex.lock()
                when (planet) {
                    is UndiscoveredPlanet -> {
                        //
                        val transformedPlanet =
                            planet.toDiscoveredPlanet(planetFromPlanetDTO(planetDTO, planet.clusterId))
                        updateNeighbourRelation(transformedPlanet)
                        PlanetRepository.addOrReplace(transformedPlanet)
                    }
                }
                planet.mutex.unlock()
            }
    }

    private suspend fun updateNeighbourRelation(planet: DiscoveredPlanet) {
        planet.neighbours.forEach { (direction, neighbour) ->
            val neighbourPlanet = PlanetRepository.get(neighbour.id)
            neighbourPlanet
                .onSuccess { planetToUpdate ->
                    planetToUpdate.mutex.lock()
                    planetToUpdate.setNeighbourPlanet(direction.opposite(), planet)
                    PlanetRepository.addOrReplace(planetToUpdate)
                    planetToUpdate.mutex.unlock()
                }

        }
    }

    fun generateNewCluster(): Cluster {
        val newCluster = Cluster()
        ClusterRepository.add(newCluster)
        return newCluster
    }

    fun planetFromPlanetDTO(dto: PlanetDto, cluster: Cluster): DiscoveredPlanet {
        return planetFromPlanetDTO(dto, cluster.id)
    }

    fun planetFromPlanetDTO(dto: PlanetDto, clusterId: UUID): DiscoveredPlanet {
        val deposit: Deposit = if (dto.resourceType == null) NoDeposit
        else UndiscoveredDeposit(Resource.valueOf(dto.resourceType.uppercase()))

        return DiscoveredPlanet(
            id = UUID.fromString(dto.planetId),
            gameWorldId = UUID.fromString(dto.gameWorldId),
            movementDifficulty = dto.movementDifficulty,
            deposit = deposit,
            clusterId = clusterId
        )
    }

}
package com.example.thelegend27.planet.application.usecases

import com.example.thelegend27.eventinfrastructure.map.PlanetDiscoveredEvent
import com.example.thelegend27.eventinfrastructure.map.PlanetNeighbourDto
import com.example.thelegend27.planet.application.PlanetService
import com.example.thelegend27.planet.domain.*
import org.slf4j.LoggerFactory
import java.util.*

class PlanetDiscoveredHandler {
    private val logger = LoggerFactory.getLogger(PlanetDiscoveredHandler::class.java)
    //private val planets = PlanetRepository
    //private val clusters = ClusterRepository

    suspend fun handle(planetDiscoveredEventDTO: PlanetDiscoveredEvent) {
        val planetId = UUID.fromString(planetDiscoveredEventDTO.planetId)
        PlanetRepository.get(planetId)
            .onFailure { generateNewDiscoveredPlanet(planetDiscoveredEventDTO) }
            .onSuccess { planet ->
                planet.mutex.lock()
                when (planet) {
                    is DiscoveredPlanet -> {
                        updateDiscoveredPlanet(planet, planetDiscoveredEventDTO)
                    }

                    is UndiscoveredPlanet -> {
                        transformUndiscoveredPlanet(planet, planetDiscoveredEventDTO)
                    }
                }
                planet.mutex.unlock()
            }
    }

    private fun generateNewDiscoveredPlanet(planetDiscoveredEventDTO: PlanetDiscoveredEvent) {
        val newCluster = generateNewCluster()
        val newPlanet =
            discoveredPlanetFromPlanetDiscoveredDTO(planetDiscoveredEventDTO, newCluster)
        PlanetRepository.addOrReplace(newPlanet)
        neighbourAndClusterAssignmentRoutine(newPlanet, planetDiscoveredEventDTO)
    }

    private suspend fun transformUndiscoveredPlanet(
        planet: UndiscoveredPlanet,
        planetDiscoveredEventDTO: PlanetDiscoveredEvent
    ) {
        val newPlanet = planet.toDiscoveredPlanet(
            discoveredPlanetFromPlanetDiscoveredDTO(
                planetDiscoveredEventDTO,
                planet.clusterId
            )
        )
        PlanetRepository.addOrReplace(newPlanet)
        updateNeighbourRelation(newPlanet)
        neighbourAndClusterAssignmentRoutine(newPlanet, planetDiscoveredEventDTO)
    }

    private fun updateDiscoveredPlanet(
        planet: DiscoveredPlanet,
        planetDiscoveredEventDTO: PlanetDiscoveredEvent
    ) {
        planet.deposit = PlanetService.createResourceFromPlanetDiscoveredDTO(planetDiscoveredEventDTO)
        PlanetRepository.addOrReplace(planet)
        neighbourAndClusterAssignmentRoutine(planet, planetDiscoveredEventDTO)
    }

    private suspend fun updateNeighbourRelation(planet: DiscoveredPlanet) {
        planet.neighbours.forEach { (direction, neighbour) ->
            PlanetRepository.get(neighbour.id)
                .onSuccess { planetToUpdate ->
                    planetToUpdate.mutex.lock()
                    planetToUpdate.setNeighbourPlanet(direction.opposite(), planet)
                    PlanetRepository.addOrReplace(planetToUpdate)
                    planetToUpdate.mutex.unlock()
                }

        }
    }

    private fun neighbourAndClusterAssignmentRoutine(
        planet: Planet,
        planetDiscoveredEventDTO: PlanetDiscoveredEvent
    ) {
        planetDiscoveredEventDTO.neighbours.forEach { neighbourDTO ->
            PlanetRepository.get(planet.id)
                .onSuccess { planet ->
                    PlanetRepository.get(UUID.fromString(neighbourDTO.id))
                        .onFailure { assignClusterForUnknownNeighbour(neighbourDTO, planet) }
                        .onSuccess { neighbour -> assignClusterForRegisteredNeighbour(planet, neighbourDTO, neighbour) }
                }
        }
    }

    private fun assignClusterForRegisteredNeighbour(
        planet: Planet,
        neighbourDTO: PlanetNeighbourDto,
        neighbour: Planet
    ) {
        planet.setNeighbourPlanet(Direction.fromString(neighbourDTO.direction), neighbour)
        neighbour.setNeighbourPlanet(
            Direction.fromString(neighbourDTO.direction).opposite(),
            planet
        )

        PlanetRepository.addOrReplace(planet)
        PlanetRepository.addOrReplace(neighbour)

        if (neighbour.clusterId != planet.clusterId) {
            ClusterRepository.get(planet.clusterId)
                .onSuccess {
                    it.addToCluster(neighbour)
                    ClusterRepository.addOrReplace(it)
                    mergeClustersById(planet.clusterId, neighbour.clusterId)
                }
        }
    }

    private fun assignClusterForUnknownNeighbour(
        neighbourDTO: PlanetNeighbourDto,
        planet: Planet
    ) {
        val newNeighbour = UndiscoveredPlanet(
            id = UUID.fromString(neighbourDTO.id),
            gameWorldId = null,
            clusterId = planet.clusterId
        )
        planet.setNeighbourPlanet(Direction.fromString(neighbourDTO.direction), newNeighbour)
        newNeighbour.setNeighbourPlanet(
            Direction.fromString(neighbourDTO.direction).opposite(),
            planet
        )
        ClusterRepository.get(planet.clusterId)
            .onSuccess {
                it.addToCluster(newNeighbour)
                ClusterRepository.addOrReplace(it)
            }
        PlanetRepository.addOrReplace(planet)
        PlanetRepository.addOrReplace(newNeighbour)

    }

    private fun mergeClustersById(clusterIdA: UUID, clusterIdB: UUID) {
        ClusterRepository.get(clusterIdA)
            .onSuccess { a ->
                ClusterRepository.get(clusterIdB)
                    .onSuccess { b ->
                        if (a.size >= b.size) {
                            a.mergeIntoPrechecked(b)
                            ClusterRepository.addOrReplace(a)
                            updateAllPlanetsToNewCluster(a)
                            logger.info("Removing Cluster : ${b.id} , merged into ${a.id}")
                            ClusterRepository.removeElement(b)
                        } else {
                            b.mergeIntoPrechecked(a)
                            ClusterRepository.addOrReplace(b)
                            updateAllPlanetsToNewCluster(b)
                            logger.info("Removing Cluster : ${a.id} , merged into ${b.id}")
                            ClusterRepository.removeElement(a)
                        }
                    }
            }

    }

    private fun updateAllPlanetsToNewCluster(cluster: Cluster) {
        cluster.getPlanetIds.forEach {
            PlanetRepository.get(it)
                .onSuccess { planet ->
                    if (planet.clusterId != cluster.id) {
                        planet.clusterId = cluster.id
                        PlanetRepository.addOrReplace(planet)
                    }

                }
        }
    }

    private fun generateNewCluster(): Cluster {
        val newCluster = Cluster()
        ClusterRepository.add(newCluster)
        return newCluster
    }

    fun discoveredPlanetFromPlanetDiscoveredDTO(
        planetDiscoveredEventDTO: PlanetDiscoveredEvent,
        cluster: Cluster
    ): DiscoveredPlanet {
        return discoveredPlanetFromPlanetDiscoveredDTO(planetDiscoveredEventDTO, cluster.id)
    }

    fun discoveredPlanetFromPlanetDiscoveredDTO(
        planetDiscoveredEventDTO: PlanetDiscoveredEvent,
        clusterId: UUID
    ): DiscoveredPlanet {
        return DiscoveredPlanet(
            id = UUID.fromString(planetDiscoveredEventDTO.planetId),
            gameWorldId = null,
            movementDifficulty = planetDiscoveredEventDTO.movementDifficulty,
            clusterId = clusterId,
            deposit = PlanetService.createResourceFromPlanetDiscoveredDTO(planetDiscoveredEventDTO)
        )
    }
}
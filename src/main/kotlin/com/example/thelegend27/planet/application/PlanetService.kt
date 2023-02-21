package com.example.thelegend27.planet.application


import com.example.thelegend27.eventinfrastructure.Channels
import com.example.thelegend27.eventinfrastructure.Event
import com.example.thelegend27.eventinfrastructure.map.PlanetDiscoveredEvent
import com.example.thelegend27.eventinfrastructure.map.PlanetDto
import com.example.thelegend27.eventinfrastructure.map.PlanetResourceMinedEvent
import com.example.thelegend27.eventinfrastructure.robot.RobotSpawnedEvent
import com.example.thelegend27.planet.application.usecases.PlanetDTOHandler
import com.example.thelegend27.planet.application.usecases.PlanetDiscoveredHandler
import com.example.thelegend27.planet.application.usecases.ResourceMinedHandler
import com.example.thelegend27.planet.domain.*
import com.example.thelegend27.planet.domainprimitives.*
import com.example.thelegend27.planet.throwables.PlanetDoesNotExist
import com.example.thelegend27.trading.domain.Resource
import com.example.thelegend27.utility.Repository
import com.example.thelegend27.utility.throwables.EntryDoesNotExistException
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.sync.Mutex
import org.slf4j.LoggerFactory
import org.slf4j.event.Level
import java.util.*


object PlanetService {
    private val mutex = Mutex()
    private val planets: Repository<Planet> = PlanetRepository
    private val clusters: Repository<Cluster> = ClusterRepository
    private val pathFinder = PathFinder()
    private val logger =
        LoggerFactory
            .getLogger(PlanetService::class.java)


    init {
        logger.atLevel(Level.DEBUG)
    }

    private val planetDiscoveredChannel: Channel<Event<PlanetDiscoveredEvent>> =
        Channels.planetDiscovered
    private val planetDTOChannel: Channel<Event<RobotSpawnedEvent>> = Channels.planetDTO
    private val planetResourceMinedEventChannel: Channel<Event<PlanetResourceMinedEvent>> = Channels.resourceMined

    fun getAllPlanets(): List<Planet> {
        return planets.getAll()

    }

    fun getPercentageOfUndiscoveredPlanets(): Double {
        val amountOfPlanetsWithLessThan15Players = 200
        val undiscoveredPlanets =
            planets.getAll().filterIsInstance<UndiscoveredPlanet>()
        return (amountOfPlanetsWithLessThan15Players - undiscoveredPlanets.size.toDouble()) / amountOfPlanetsWithLessThan15Players
    }

    fun areThereUndiscoveredPlanets(): Boolean {
        return planets.getAll().any { it is UndiscoveredPlanet }
    }

    private fun debug() {
        logger.info("===Begin Clusters===")
        clusters.getAll().forEach {
            logger.info("Cluster : ${it.id}  ; Elements ${it.getPlanetIds.size}")
        }

        val amountOfPlanets = planets.getSize()
        val amountOfClusters = clusters.getSize()
        val amountOfPlanetsWithClusterInPlanetRepository = planets.getAll().count()
        val amountOfPlanetsWithClusterInClusterRepository = clusters.getAll().sumOf { it.getPlanetIds.size }

        logger.info("Total amount of Clusters: $amountOfClusters")
        logger.info("Total amount of Planets : $amountOfPlanets")
        logger.info("Amount of planets assigned to a Cluster inside Planet Repository $amountOfPlanetsWithClusterInPlanetRepository")
        logger.info(
            "Amount of Planets assigned to a Cluster inside Cluster Repository $amountOfPlanetsWithClusterInClusterRepository"
        )
        logger.info("=== End Clusters ===")
    }

    fun getShortestPathFromPlanetToPlanet(fromPlanet: Planet, toPlanet: Planet): List<Planet> {
        return pathFinder.getShortestPathFromPlanetToPlanet(fromPlanet, toPlanet)
    }

    fun getShortestPathFromPlanetToListOfPlanets(fromPlanet: Planet, toPlanets: List<Planet>): List<Planet> {
        return pathFinder.getShortestPathFromPlanetToListOfPlanets(fromPlanet, toPlanets)
    }


    suspend fun getPlanetById(planetId: UUID): Result<Planet> {

        mutex.lock()
        return if (planets.containsKey(planetId)) {
            mutex.unlock()
            Result.success(planets.get(planetId).getOrThrow())
        } else {
            mutex.unlock()
            return Result.failure(PlanetDoesNotExist("Planet of Id $planetId does not exist:"))
        }

    }

    fun getAllPlanetsInCluster(clusterId: UUID): List<Planet> {
        return planets.elements.values.filter { it.clusterId == clusterId }

    }

    /**
     * This functions should be called when a game is over.
     * It clears the planet repository and the cluster repository.
     */

    fun clear() {
        runBlocking {
            val randomUUID = UUID.randomUUID()
            mutex.lock(randomUUID)
            planets.clear()
            clusters.clear()
            mutex.unlock(randomUUID)
        }
    }


    @OptIn(ExperimentalCoroutinesApi::class)
    suspend fun handlePlanetEvents() {
        CoroutineScope(Dispatchers.IO).launch {
            while (true) {
                if (!planetDTOChannel.isEmpty) {
                    val randomUUID = UUID.randomUUID()
                    mutex.lock(randomUUID)
                    val planetDTOEvent = planetDTOChannel.receive()
                    val planetDTO = planetDTOEvent.eventBody
                    PlanetDTOHandler().handle(planetDTO.robot.planet)
                    Channels.robotSpawned.send(planetDTOEvent)
                    mutex.unlock(randomUUID)
                }


                if (!planetDiscoveredChannel.isEmpty) {
                    val randomUUID = UUID.randomUUID()
                    mutex.lock(randomUUID)
                    logger.info("Handling Planet Event")
                    val planetDiscoveredDTO = planetDiscoveredChannel.receive().eventBody
                    PlanetDiscoveredHandler().handle(planetDiscoveredDTO)
                    mutex.unlock(randomUUID)
                }


                if (!planetResourceMinedEventChannel.isEmpty) {
                    val randomUUID = UUID.randomUUID()
                    mutex.lock(randomUUID)
                    ResourceMinedHandler().handle(planetResourceMinedEventChannel.receive().eventBody)
                    mutex.unlock(randomUUID)
                }

            }
        }
    }






    fun createResourceFromPlanetDiscoveredDTO(planetDiscoveredEventDTO: PlanetDiscoveredEvent): Deposit {
        return when (planetDiscoveredEventDTO.resource) {
            null -> NoDeposit
            else -> {
                if (planetDiscoveredEventDTO.resource.currentAmount == 0) NoDeposit
                else DiscoveredDeposit(
                    resourceType = Resource.fromString(planetDiscoveredEventDTO.resource.resourceType.toString()),
                    maxAmount = planetDiscoveredEventDTO.resource.maxAmount,
                    currentAmount = planetDiscoveredEventDTO.resource.currentAmount
                )
            }
        }
    }


    private fun generateNewCluster(): Cluster {
        val newCluster = Cluster()
        clusters.add(newCluster)
        return newCluster
    }

    fun getAllClusters(): List<Cluster> {
        return clusters.getAll()

    }

    fun getClusterStats(id: UUID): Result<ClusterStats> {
        clusters.get(id)
            .onSuccess {
                val stats = ClusterStats
                    .fromNothing(it.id)
                    .assignEntries(it.getPlanetIds.toList())
                return Result.success(stats)
            }
        return Result.failure(EntryDoesNotExistException("Entry with Id : $id does not exist"))


    }

    fun getPlanetStats(id: UUID): Result<PlanetStats> {
        val planet = planets.get(id)
        planet
            .onSuccess {
                val stats: PlanetStats = when (it) {
                    is DiscoveredPlanet -> PlanetStats.fromDiscoveredPlanet(it)
                    else -> PlanetStats.fromPlanet(it)
                }
                return Result.success(stats)
            }
        return Result.failure(EntryDoesNotExistException("Entry with Id : $id does not exist"))
    }

    fun generalStats(): GeneralStats {
        return GeneralStats
            .empty()
            .appendPlanetAmount(getAllPlanets().size)
            .appendClusterAmount(getAllClusters().size)
            .appendPlanetsContainingClusterInformation(getAllPlanets().count())
            .appendRegisteredPlanetsInAllClusters(getAllClusters().sumOf { it.size })

    }

    fun getClusterByClusterId(clusterId: UUID): Result<Cluster> {
        return clusters.get(clusterId)
    }


    fun getClusterIdByPlanetId(planetId: UUID): UUID {
        val planet = planets.get(planetId)
        planet.onSuccess {
            return it.clusterId
        }
        throw PlanetDoesNotExist("Planet with id $planetId does not exist")

    }

    fun distanceFromTo(fromPlanet: DiscoveredPlanet, toPlanet: Planet): Int? {
        // check if both planets are in the same cluster
        return if (fromPlanet.clusterId == toPlanet.clusterId) {
            // calculate distance
            getShortestPathFromPlanetToPlanet(fromPlanet, toPlanet).size - 1
        } else null
    }

}
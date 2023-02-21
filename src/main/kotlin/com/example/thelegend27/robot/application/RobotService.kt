package com.example.thelegend27.robot.application

import com.example.thelegend27.eventinfrastructure.Channels
import com.example.thelegend27.game.application.GameClient
import com.example.thelegend27.planet.application.PlanetService
import com.example.thelegend27.planet.domain.DiscoveredPlanet
import com.example.thelegend27.planet.domain.PlanetRepository
import com.example.thelegend27.robot.application.usecases.*
import com.example.thelegend27.robot.domain.EnemyRobot
import com.example.thelegend27.robot.domain.EnemyRobotRepository
import com.example.thelegend27.robot.domain.Robot
import com.example.thelegend27.robot.domain.RobotRepository
import com.example.thelegend27.robot.domain.strategies.RobotStrategy
import com.example.thelegend27.robot.domainprimitives.EnemyRobotEntryDetailed
import com.example.thelegend27.robot.domainprimitives.EnemyRobotEntryMinimal
import com.example.thelegend27.robot.domainprimitives.FriendlyRobotEntryDetailed
import com.example.thelegend27.robot.domainprimitives.FriendlyRobotEntryMinimal
import com.example.thelegend27.utility.throwables.EntryDoesNotExistException
import com.google.gson.JsonParser
import io.ktor.client.call.*
import kotlinx.coroutines.*
import kotlinx.coroutines.selects.select
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withLock
import org.slf4j.LoggerFactory
import java.util.*


object RobotService {
    private val logger = LoggerFactory.getLogger(RobotService::class.java)
    private val mutex = Mutex()
    private val planets = PlanetRepository
    private val robotSpawnedChannel = Channels.robotSpawned
    private val robotMovedChannel = Channels.robotMoved
    private val robotAttackedChannel = Channels.robotAttackedIntegrationEventChannel
    private val robotEnergyUpdatedChannel = Channels.robotEnergyUpdated
    private val robotHealthUpdatedChannel = Channels.robotHealthUpdated
    private val robotsRevealedChannel = Channels.robotsRevealedIntegrationEventChannel
    private val robotUpgradedChannel = Channels.robotUpgraded
    private val robotKilledChannel = Channels.robotKilled
    private val robotResourceMinedIntegrationEventChannel = Channels.robotResourceMinedIntegrationEventChannel
    private val robotResourceRemovedChannel = Channels.robotResourceRemovedIntegrationEventChannel

    @OptIn(ExperimentalCoroutinesApi::class)
    private suspend fun startListeningForPlanetUpdates() {
        CoroutineScope(Dispatchers.IO).launch {
            planets.asFlow().collect { planetMap ->
                val randomUUID = UUID.randomUUID()
                mutex.lock(randomUUID)
                val jobs = mutableListOf<Job>()
                val robots = RobotRepository.getAll()
                robots.forEach { robot ->
                    jobs += launch {
                        val randomUUID = UUID.randomUUID()
                        val currentPlanet = planetMap[robot.currentPlanet.id]
                        if (currentPlanet != null && currentPlanet is DiscoveredPlanet) {
                            robot.mutex.lock(randomUUID)
                            logger.info("UPDATING ROBOT ${robot.id} FROM ${robot.currentPlanet} TO PLANET ${currentPlanet} ")
                            robot.currentPlanet = currentPlanet
                            RobotRepository.addOrReplace(robot)
                            robot.mutex.unlock(randomUUID)
                        }
                    }
                    jobs.joinAll()
                }
                mutex.unlock(randomUUID)
                delay(2000)
            }
        }
    }

    suspend fun handleRobotEvents() {
        runBlocking {
            startListeningForPlanetUpdates()
            while (true) {
                select<Unit> {
                    robotSpawnedChannel.onReceive { event ->
                        launch(Dispatchers.IO) {
                            mutex.withLock {
                                RobotSpawnedHandler().handle(event.eventBody)
                            }
                        }
                    }
                    robotEnergyUpdatedChannel.onReceive { event ->
                        launch(Dispatchers.IO) {
                            mutex.withLock {
                                RobotEnergyUpdatedHandler().handle(event.eventBody)
                            }
                        }
                    }
                    robotHealthUpdatedChannel.onReceive { event ->
                        launch(Dispatchers.IO) {
                            mutex.withLock {
                                RobotHealthUpdatedHandler().handle(event.eventBody)
                            }
                        }
                    }
                    robotKilledChannel.onReceive { event ->
                        launch(Dispatchers.IO) {
                            mutex.withLock {
                                RobotKilledHandler().handle(event.eventBody)
                            }
                        }
                    }
                    robotMovedChannel.onReceive { event ->
                        launch(Dispatchers.IO) {
                            mutex.withLock {
                                RobotMovedHandler().handle(event.eventBody)
                            }
                        }
                    }
                    robotAttackedChannel.onReceive { event ->
                        launch(Dispatchers.IO) {
                            mutex.withLock {
                                RobotAttackedHandler().handle(event.eventBody)
                            }
                        }
                    }
                    robotUpgradedChannel.onReceive { event ->
                        launch(Dispatchers.IO) {
                            mutex.withLock {
                                RobotUpgradedHandler().handle(event.eventBody)
                            }
                        }
                    }
                    robotResourceRemovedChannel.onReceive { event ->
                        launch(Dispatchers.IO) {
                            mutex.withLock {
                                RobotResourceRemovedHandler().handle(event.eventBody)
                            }
                        }
                    }
                    robotResourceMinedIntegrationEventChannel.onReceive { event ->
                        launch(Dispatchers.IO) {
                            mutex.withLock {
                                RobotResourceMinedHandler().handle(event.eventBody)
                            }
                        }
                    }
                    robotsRevealedChannel.onReceive { event ->
                        launch(Dispatchers.IO) {
                            mutex.withLock {
                                RobotsRevealedHandler().handle(event.eventBody)
                            }
                        }
                    }
                }
            }
        }
    }

    fun getAllRobots(): List<Robot> {
        return RobotRepository.getAll()
    }

    fun addOrReplace(robot: Robot) {
        RobotRepository.addOrReplace(robot)
    }

    fun getAllRobotsOnPlanet(planetId: UUID): List<Robot> {
        return getAllRobots().filter { robot -> robot.currentPlanet.id == planetId }
    }

    fun isEnemyOnPlanetWithId(planetId: UUID): Boolean {
        return EnemyRobotRepository.getAll().any { it.planetId == planetId }
    }

    fun getAmountOfOurRobots(): Int = RobotRepository.getSize()

    fun getAllEnemiesOnPlanet(planetId: UUID): List<EnemyRobot> {
        return EnemyRobotRepository.getAll().filter { it.planetId == planetId }
    }

    suspend fun getAllEnemyRobotsInClusterByPlanetId(planetId: UUID): List<EnemyRobot> {
        val clusterId = PlanetService.getPlanetById(planetId).getOrElse { return emptyList() }.clusterId
        val cluster = PlanetService.getClusterByClusterId(clusterId).getOrElse { return emptyList() }
        val enemyRobots = EnemyRobotRepository.getAll()
        return enemyRobots.filter { enemyRobot -> cluster.getPlanetIds.contains(enemyRobot.planetId) }
    }

    /**
     * Clears all robots from the repository so that the next game can start with a clean state.
     */
    fun clear() {
        RobotRepository.clear()
        EnemyRobotRepository.clear()
    }


    suspend fun callCommandsForEachRobotParallel() {
        val semaphore = Semaphore(20)
        val randomUUID = UUID.randomUUID()
        val robots = RobotRepository.getAll()

        withContext(Dispatchers.IO) {
            robots.forEach { robot ->
                semaphore.acquire()
                launch {
                    robot.mutex.lock(randomUUID)
                    val command = robot.strategy.getCommand()!!
                    try {
                        //TODO: Move the request to the game service into a function in the GameService class
                        logger.info("Command for Robot :\n $command \n Current planet of robot : ${robot.currentPlanet.id} Strategy is ${robot.strategy::class.java.simpleName}")
                        val response = GameClient.sendCommand(command)
                        val jsonObject = JsonParser.parseString(response.body()).asJsonObject
                        val transactionId = jsonObject.get("transactionId").asString
                        logger.info("Sent Command to Game Service. Transaction ID: $transactionId")

                    } catch (e: Exception) {
                        logger.error(
                            "Sending Command to Game Service failed!${e.message}\n" + "Command that was tried to be sent :\n$command\n" + "Response from Game Service :\n$e"
                        )
                    } finally {
                        robot.mutex.unlock(randomUUID)
                    }
                }.invokeOnCompletion { semaphore.release() }
            }
        }
    }

    suspend fun switchRobotStrategy(robotId: UUID, strategy: RobotStrategy) {
        val robot = RobotRepository.get(robotId).getOrThrow()

        val randomUUID = UUID.randomUUID()
        robot.mutex.lock(randomUUID)
        robot.strategy = strategy

        RobotRepository.addOrReplace(robot)
        robot.mutex.unlock(randomUUID)

    }


    fun getAllRobotsAsFriendlyRobotEntryMinimal(): List<FriendlyRobotEntryMinimal> {
        return getAllRobots().map { FriendlyRobotEntryMinimal.fromRobot(it) }
    }

    fun getAllRobotsAsFriendlyRobotEntryDetailed(): List<FriendlyRobotEntryDetailed> {
        return getAllRobots().map { FriendlyRobotEntryDetailed.fromRobot(it) }
    }

    fun getMinimalFriendlyRobotEntryById(id: UUID): Result<FriendlyRobotEntryMinimal> {
        RobotRepository.get(id).onSuccess {
            return Result.success(FriendlyRobotEntryMinimal.fromRobot(it))
        }.onFailure {
            return Result.failure(it)
        }
        return Result.failure(EntryDoesNotExistException("Entry of Id $id does not exist"))
    }

    fun getDetailedFriendlyRobotEntryById(id: UUID): Result<FriendlyRobotEntryDetailed> {
        RobotRepository.get(id).onSuccess {
            return Result.success(FriendlyRobotEntryDetailed.fromRobot(it))
        }.onFailure {
            return Result.failure(it)
        }
        return Result.failure(EntryDoesNotExistException("Entry of Id $id does not exist"))
    }

    fun getAllEnemyRobotsAsEnemyRobotEntryMininmal(): List<EnemyRobotEntryMinimal> {
        return EnemyRobotRepository.getAll().map { EnemyRobotEntryMinimal.fromEnemyRobot(it) }
    }

    fun getEnemyRobotEntryMinimalById(id: UUID): Result<EnemyRobotEntryMinimal> {
        EnemyRobotRepository.get(id).onSuccess {
            return Result.success(EnemyRobotEntryMinimal.fromEnemyRobot(it))
        }.onFailure {
            return Result.failure(it)
        }
        return Result.failure(EntryDoesNotExistException("Entry of Id $id does not exist"))
    }

    fun getEnemyRobotEntryDetailedById(id: UUID): Result<EnemyRobotEntryDetailed> {
        EnemyRobotRepository.get(id).onSuccess {
            return Result.success(EnemyRobotEntryDetailed.fromEnemyRobot(it))
        }.onFailure {
            return Result.failure(it)
        }
        return Result.failure(EntryDoesNotExistException("Entry of Id $id does not exist"))
    }

    fun isFriendlyRobotOnPlanet(planetId: UUID): Boolean {
        return RobotRepository.getAll().any { it.currentPlanet.id == planetId }
    }

    fun amountOfFriendlyRobotsOnPlanet(planetId: UUID): Int {
        return RobotRepository.getAll().count { it.currentPlanet.id == planetId }
    }


}


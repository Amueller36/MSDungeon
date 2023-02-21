package com.example.thelegend27.game.domain

import com.example.thelegend27.eventinfrastructure.Channels
import com.example.thelegend27.eventinfrastructure.Consumer
import com.example.thelegend27.eventinfrastructure.Event
import com.example.thelegend27.eventinfrastructure.EventMapper
import com.example.thelegend27.eventinfrastructure.game.GameStatusEvent
import com.example.thelegend27.eventinfrastructure.game.RoundStatusEvent
import com.example.thelegend27.game.application.GameClient
import com.example.thelegend27.planet.application.PlanetService
import com.example.thelegend27.player.application.PlayerService
import com.example.thelegend27.player.domain.Player
import com.example.thelegend27.robot.application.RobotService
import com.example.thelegend27.trading.domain.Item
import com.rabbitmq.client.ConnectionFactory
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import org.slf4j.LoggerFactory
import java.time.Instant
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit


class GameStateHandler {
    //Rabbit MQ
    private val connectionFactory: ConnectionFactory = Channels.connectionFactory
    private val connection = connectionFactory.newConnection()
    private val playerQueueChannel = connection.createChannel()
    private val consumer = Consumer(playerQueueChannel, EventMapper)

    private val logger = LoggerFactory.getLogger("GameStateHandler")
    private var gameState: GameStates = GameStates.WaitingForGameCreation
    private val gameStatusChannel: Channel<Event<GameStatusEvent>> = Channels.gameStatus
    private val roundStatusEventChannel: Channel<Event<RoundStatusEvent>> = Channels.roundStatusEvent

    fun gameLoop() {
        while (true) {
            when (gameState) {
                GameStates.WaitingForGameCreation -> {
                    waitForGameCreation()
                }

                GameStates.GameFound -> {
                    createAndJoinPlayer()
                    startToConsumeIncomingEvents()
                    gameState = GameStates.WaitingForGameStart
                }

                GameStates.WaitingForGameStart -> {
                    waitForGameStartAndHandleEventsWhenStarted()
                }

                GameStates.Playing -> {
                    buyRobotsFirstRoundAndSendCommandsForThem()
                }

                GameStates.GameOver -> {
                    logger.info("Game Over")
                    clearAllServices()
                    gameState = GameStates.WaitingForGameCreation
                }

            }

        }
    }


    private fun waitForGameCreation() {
        logger.info("Waiting for game creation")
        while (gameState == GameStates.WaitingForGameCreation) {
            val listedGames = GameClient.getGameInfo()
            listedGames.forEach {
                if (it.gameStatus.contains("created", ignoreCase = true)) {
                    gameState = GameStates.GameFound
                }
            }
        }
    }

    private fun createAndJoinPlayer() {
        GameClient.registerPlayer()
        GameClient.joinGame()
        logger.info("Game found. Player registered and joined!")
    }

    private fun startToConsumeIncomingEvents() {
        playerQueueChannel.basicConsume(Player.playerQueue.playerQueue, true, "Tag", consumer)
    }

    private fun waitForGameStartAndHandleEventsWhenStarted() {
        val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
            logger.error("WaitForGameStart : CoroutineExceptionHandler got $throwable")
        }
        runBlocking(Dispatchers.IO + coroutineExceptionHandler) {
            val gameStatus = gameStatusChannel.receive()
            if (gameStatus.eventBody.status == "started") {
                gameState = GameStates.Playing
                logger.info("Game started!")
                handleIncomingGameRelatedEvents()
            }

        }
    }

    private fun buyRobotsFirstRoundAndSendCommandsForThem() {
        val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
            logger.error("handlingIncomingEventsAndSendCommands : CoroutineExceptionHandler got $throwable")
        }
        runBlocking(Dispatchers.IO + coroutineExceptionHandler) {
            val roundStatus = roundStatusEventChannel.receive()
            buyInitialRobotsForFirstRound(roundStatus.eventBody)
            sendCommandsAfterHalfOfRoundIsDone(roundStatus)
        }
    }

    private fun buyInitialRobotsForFirstRound(roundStatusEvent: RoundStatusEvent) {
        if (roundStatusEvent.roundStatus == "started" && roundStatusEvent.roundNumber == 2) {
            if (Player.moneten.toDouble() > 0.0) {
                val maximumAmountOfRobotsThatCanBeBoughtInFirstRound = Player.moneten.toInt() / Item.ROBOT.value()
                logger.info("Game TIME! Trying to buy first robots")
                GameClient.buyXRobots(maximumAmountOfRobotsThatCanBeBoughtInFirstRound)
            }

        }
    }

    /**
    Handles Incoming events first 50% of the round time and sends Commands for robots in the remaining time, so
    we can assure, that the robots strategy perform on the latest possible state of the game.
     */
    private suspend fun sendCommandsAfterHalfOfRoundIsDone(roundStatusEvent: Event<RoundStatusEvent>) {
        if (roundStatusEvent.eventBody.roundStatus == "started" && roundStatusEvent.eventBody.roundNumber > 2) {
            val impreciseTimingPredictions = roundStatusEvent.eventBody.impreciseTimingPredictions
            val roundStart = ZonedDateTime.parse(impreciseTimingPredictions.roundStart)
            val commandInputEnd = ZonedDateTime.parse(impreciseTimingPredictions.commandInputEnd)
            val roundEnd = ZonedDateTime.parse(impreciseTimingPredictions.roundEnd)

            val timeBeforeCommandInputEnd = commandInputEnd.minus(
                ((commandInputEnd.toInstant().toEpochMilli() - roundStart.toInstant().toEpochMilli()) * 0.5).toLong(),
                ChronoUnit.MILLIS
            )
            //Send Robot Commands when 50% of the round time is over
            while (Instant.now().isBefore(roundEnd.toInstant())) {
                if (Instant.now().isAfter(timeBeforeCommandInputEnd.toInstant())) {
                    CoroutineScope(Dispatchers.IO).launch {
                        RobotService.callCommandsForEachRobotParallel()
                    }
                    break
                }
            }
        }
    }

    private suspend fun handleIncomingGameRelatedEvents() {
        val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
            logger.error("handlingIncomingEventsAndSendCommands : CoroutineExceptionHandler got $throwable")
        }
        CoroutineScope(Dispatchers.IO + coroutineExceptionHandler).launch {
            launch { PlanetService.handlePlanetEvents() }

            launch { RobotService.handleRobotEvents() }

            launch { PlayerService.handlePlayerEvents() }
        }
    }


    private fun clearAllServices() {
        runBlocking {
            PlanetService.clear()
            PlayerService.clear()
            RobotService.clear()
        }
    }
}



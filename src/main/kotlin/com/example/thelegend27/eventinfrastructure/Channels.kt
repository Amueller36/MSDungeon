package com.example.thelegend27.eventinfrastructure

import com.example.thelegend27.eventinfrastructure.game.GameStatusEvent
import com.example.thelegend27.eventinfrastructure.game.RoundStatusEvent
import com.example.thelegend27.eventinfrastructure.map.PlanetDiscoveredEvent
import com.example.thelegend27.eventinfrastructure.map.PlanetResourceMinedEvent
import com.example.thelegend27.eventinfrastructure.robot.*
import com.example.thelegend27.eventinfrastructure.robot.integrationevents.*
import com.example.thelegend27.eventinfrastructure.trading.*
import com.rabbitmq.client.ConnectionFactory
import com.rabbitmq.client.impl.DefaultExceptionHandler
import kotlinx.coroutines.channels.Channel

object Channels {

    //Game
    val gameStatus = Channel<Event<GameStatusEvent>>(Channel.UNLIMITED)
    val roundStatusEvent = Channel<Event<RoundStatusEvent>>(Channel.UNLIMITED)
    val error = Channel<Event<ErrorEvent>>(Channel.UNLIMITED)

    //Player
    val bankAccountInitialized = Channel<Event<BankAccountInitializedEvent>>(Channel.UNLIMITED)
    val bankAccountCleared = Channel<Event<BankAccountClearedEvent>>(Channel.UNLIMITED)

    //Trading
    val bankAccountTransactionBooked = Channel<Event<BankAccountTransactionBookedEvent>>(Channel.UNLIMITED)
    val tradablePrices = Channel<Event<TradablePricesEvent>>(Channel.UNLIMITED)
    val tradableBought = Channel<Event<TradableBoughtEvent>>(Channel.UNLIMITED)
    val tradableSold = Channel<Event<TradableSoldEvent>>(Channel.UNLIMITED)

    //Planet/Robot
    val robotSpawned = Channel<Event<RobotSpawnedEvent>>(Channel.UNLIMITED)

    //Robot
    //IntegrationEvents
    val robotsRevealedIntegrationEventChannel = Channel<Event<RobotsRevealedIntegrationEvent>>(Channel.UNLIMITED)
    val robotAttackedIntegrationEventChannel = Channel<Event<RobotAttackedIntegrationEvent>>(Channel.UNLIMITED)
    val robotMovedIntegrationEventChannel = Channel<Event<RobotMovedIntegrationEvent>>(Channel.UNLIMITED)
    val robotRegeneratedIntegrationEventChannel = Channel<Event<RobotRegeneratedIntegrationEvent>>(Channel.UNLIMITED)
    val robotResourceMinedIntegrationEventChannel =
        Channel<Event<RobotResourceMinedIntegrationEvent>>(Channel.UNLIMITED)
    val robotResourceRemovedIntegrationEventChannel =
        Channel<Event<RobotResourceRemovedIntegrationEvent>>(Channel.UNLIMITED)
    val robotRestoredAttributesIntegrationEventChannel =
        Channel<Event<RobotRestoredAttributesIntegrationEvent>>(Channel.UNLIMITED)
    val robotSpawnedIntegrationEventChannel = Channel<Event<RobotSpawnedIntegrationEvent>>(Channel.UNLIMITED)
    val robotUpgradedIntegrationEventChannel = Channel<Event<RobotUpgradedIntegrationEvent>>(Channel.UNLIMITED)

    //Domain
    val robotMoved = Channel<Event<RobotMovedEvent>>(Channel.UNLIMITED)
    val robotUpgraded = Channel<Event<RobotUpgradedEvent>>(Channel.UNLIMITED)
    val robotAttacked = Channel<Event<RobotAttackedEvent>>(Channel.UNLIMITED)
    val robotKilled = Channel<Event<RobotKilledEvent>>(Channel.UNLIMITED)
    val robotEnergyUpdated = Channel<Event<RobotEnergyUpdatedEvent>>(Channel.UNLIMITED)
    val robotHealthUpdated = Channel<Event<RobotHealthUpdatedEvent>>(Channel.UNLIMITED)
    val robotResourceMined = Channel<Event<RobotResourceMinedEvent>>(Channel.UNLIMITED)
    val robotInventoryUpdated = Channel<Event<RobotInventoryUpdatedEvent>>(Channel.UNLIMITED)

    //Planet
    val planetDiscovered = Channel<Event<PlanetDiscoveredEvent>>(Channel.UNLIMITED)
    val resourceMined = Channel<Event<PlanetResourceMinedEvent>>(Channel.UNLIMITED)
    val planetDTO = Channel<Event<RobotSpawnedEvent>>(Channel.UNLIMITED)


    //PlayerService
    val channelForLeftoverMoney = Channel<Double>(Channel.UNLIMITED)
    val connectionFactory = connectionFactory()

    val channels = mutableMapOf(
        //Game
        "game-status" to gameStatus,
        "round-status" to roundStatusEvent,
        "error" to error,
        //Trading
        "BankAccountInitialized" to bankAccountInitialized,
        "BankAccountCleared" to bankAccountCleared,
        "BankAccountTransactionBooked" to bankAccountTransactionBooked,
        "TradablePrices" to tradablePrices,
        "TradableBought" to tradableBought,
        "TradableSold" to tradableSold,
        //Map
        "PlanetDiscovered" to planetDiscovered,
        "PlanetDTO" to planetDTO,

        //Robot
        //Integration
        "RobotInventoryUpdated" to robotInventoryUpdated,
        "RobotsRevealedIntegrationEvent" to robotsRevealedIntegrationEventChannel,
        "RobotAttackedIntegrationEvent" to robotAttackedIntegrationEventChannel,
        "RobotMovedIntegrationEvent" to robotMovedIntegrationEventChannel,
        "RobotRegeneratedIntegrationEvent" to robotRegeneratedIntegrationEventChannel,
        "RobotResourceMinedIntegrationEvent" to robotResourceMinedIntegrationEventChannel,
        "RobotResourceRemovedIntegrationEvent" to robotResourceRemovedIntegrationEventChannel,
        "RobotRestoredAttributesIntegrationEvent" to robotRestoredAttributesIntegrationEventChannel,
        "RobotSpawnedIntegrationEvent" to robotSpawnedIntegrationEventChannel,
        "RobotUpgradedIntegrationEvent" to robotUpgradedIntegrationEventChannel,
        //Domain
        "RobotSpawned" to robotSpawned,
        "RobotMoved" to robotMoved,
        "RobotUpgraded" to robotUpgraded,
        "RobotAttacked" to robotAttacked,
        "RobotKilled" to robotKilled,
        "RobotEnergyUpdated" to robotEnergyUpdated,
        "RobotHealthUpdated" to robotHealthUpdated,
        "RobotResourceMined" to robotResourceMined,
        "ResourceMined" to resourceMined,
    ) as Map<String, Channel<Event<*>>>

    private fun connectionFactory(): ConnectionFactory {
        val factory = ConnectionFactory()
        factory.username = System.getenv("RABBITMQ_USERNAME")
        factory.password = System.getenv("RABBITMQ_PASSWORD")
        factory.host = System.getenv("RABBITMQ_HOST")
        factory.port = System.getenv("RABBITMQ_PORT")?.toInt()!!
        factory.virtualHost = ("/")
        factory.isChannelShouldCheckRpcResponseType = true
        factory.isAutomaticRecoveryEnabled = true
        factory.exceptionHandler = DefaultExceptionHandler()
        return factory
    }

}
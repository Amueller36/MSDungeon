package com.example.thelegend27.eventinfrastructure

import com.example.thelegend27.eventinfrastructure.game.GameStatusEvent
import com.example.thelegend27.eventinfrastructure.game.RoundStatusEvent
import com.example.thelegend27.eventinfrastructure.map.PlanetDiscoveredEvent
import com.example.thelegend27.eventinfrastructure.map.PlanetResourceMinedEvent
import com.example.thelegend27.eventinfrastructure.robot.*
import com.example.thelegend27.eventinfrastructure.robot.integrationevents.*
import com.example.thelegend27.eventinfrastructure.trading.*
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.slf4j.LoggerFactory


object EventMapper {
    //create jacksonObjectMapper which ignores unknown properties
    private val logger = LoggerFactory.getLogger(EventMapper::class.java)
    private val objectMapper: ObjectMapper =
        jacksonObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, true)

    fun mapMessageToEvent(messageBody: ByteArray, headers: Map<String, ByteArray>): Event<*> {
        val headers = headers.mapValues { (_, value) ->
            String(value)
        }
        val eventHeader = EventHeaderDto(
            eventId = headers["eventId"] ?: "",
            kafkaTopic = headers["kafka-topic"] ?: "",
            type = headers["type"] ?: "",
            timestamp = headers["timestamp"] ?: "",
            transactionId = headers["transactionId"] ?: "",
            version = headers["version"] ?: ""
        )

        @Suppress("IMPLICIT_CAST_TO_ANY")
        val payload = when (eventHeader.type) {
            //Game
            "game-status" -> objectMapper.readValue<GameStatusEvent>(messageBody)
            "round-status" -> objectMapper.readValue<RoundStatusEvent>(messageBody)
            "error" -> objectMapper.readValue<ErrorEvent>(messageBody)
            //Trading
            "BankAccountInitialized" -> objectMapper.readValue<BankAccountInitializedEvent>(messageBody)
            "BankAccountCleared" -> objectMapper.readValue<BankAccountClearedEvent>(messageBody)
            "BankAccountTransactionBooked" -> objectMapper.readValue<BankAccountTransactionBookedEvent>(messageBody)
            "TradablePrices" -> objectMapper.readValue<TradablePricesEvent>(messageBody)
            "TradableBought" -> objectMapper.readValue<TradableBoughtEvent>(messageBody)
            "TradableSold" -> objectMapper.readValue<TradableSoldEvent>(messageBody)
            //Map
            "PlanetDiscovered" -> objectMapper.readValue<PlanetDiscoveredEvent>(messageBody)
            //Robot
            //IntegrationEvents
            "RobotAttackedIntegrationEvent" -> objectMapper.readValue<RobotAttackedIntegrationEvent>(messageBody)
            "RobotsRevealedIntegrationEvent" -> objectMapper.readValue<RobotsRevealedIntegrationEvent>(messageBody)
            "RobotSpawnedIntegrationEvent" -> objectMapper.readValue<RobotSpawnedIntegrationEvent>(messageBody)
            "RobotUpgradedIntegrationEvent" -> objectMapper.readValue<RobotUpgradedIntegrationEvent>(messageBody)
            "RobotRestoredAttributesIntegrationEvent" -> objectMapper.readValue<RobotRestoredAttributesIntegrationEvent>(
                messageBody
            )

            "RobotResourceRemovedIntegrationEvent" -> objectMapper.readValue<RobotResourceRemovedIntegrationEvent>(
                messageBody
            )

            "RobotResourceMinedIntegrationEvent" -> objectMapper.readValue<RobotResourceMinedIntegrationEvent>(
                messageBody
            )

            "RobotRegeneratedIntegrationEvent" -> objectMapper.readValue<RobotRegeneratedIntegrationEvent>(messageBody)
            "RobotMovedIntegrationEvent" -> objectMapper.readValue<RobotMovedIntegrationEvent>(messageBody)
            //DomainEvents
            "RobotSpawned" -> objectMapper.readValue<RobotSpawnedEvent>(messageBody)
            "RobotMoved" -> objectMapper.readValue<RobotMovedEvent>(messageBody)
            "RobotUpgraded" -> objectMapper.readValue<RobotUpgradedEvent>(messageBody)
            "RobotAttacked" -> objectMapper.readValue<RobotAttackedEvent>(messageBody)
            "RobotKilled" -> objectMapper.readValue<RobotKilledEvent>(messageBody)
            "RobotInventoryUpdated" -> objectMapper.readValue<RobotInventoryUpdatedEvent>(messageBody)
            "RobotEnergyUpdated" -> objectMapper.readValue<RobotEnergyUpdatedEvent>(messageBody)
            "RobotHealthUpdated" -> objectMapper.readValue<RobotHealthUpdatedEvent>(messageBody)
            "RobotResourceMined" -> objectMapper.readValue<RobotResourceMinedEvent>(messageBody)
            //Map
            "ResourceMined" -> objectMapper.readValue<PlanetResourceMinedEvent>(messageBody)
            else -> {
                println("Unknown Event type: ${eventHeader.type}\nFalls das passiert Andre bescheid geben.")
            }
        }
        return Event(eventHeader, payload)

    }


}
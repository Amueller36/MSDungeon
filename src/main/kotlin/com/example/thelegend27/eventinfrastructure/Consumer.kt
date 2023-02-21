package com.example.thelegend27.eventinfrastructure

import com.example.thelegend27.eventinfrastructure.robot.RobotSpawnedEvent
import com.rabbitmq.client.AMQP.BasicProperties
import com.rabbitmq.client.Channel
import com.rabbitmq.client.DefaultConsumer
import com.rabbitmq.client.Envelope
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory

class Consumer(
    channel: Channel?,
    private val eventMapper: EventMapper
) : DefaultConsumer(channel) {

    private val logger = LoggerFactory.getLogger(Consumer::class.java)

    override fun handleDelivery(
        consumerTag: String,
        envelope: Envelope,
        properties: BasicProperties,
        body: ByteArray
    ) {
        while (true) {
            try {
                printHeaderAndBody(body = body, properties = properties)
                sendEventToCorrespondingChannel(body = body, properties = properties)
                break
            } catch (e: Exception) {
                logger.error("Error processing message: ${e.message}", e)
                logger.info("Restarting consumer...")
                Thread.sleep(5000L) // wait 5 seconds before restarting
            }
        }
    }

    private fun sendEventToCorrespondingChannel(body: ByteArray, properties: BasicProperties) {
        val event = EventMapper.mapMessageToEvent(body, properties.headers as Map<String, ByteArray>)
        logger.info("Event ${event.eventHeader.type} received")
        runBlocking {
            if (event.eventHeader.type == "RobotSpawned") {
                //RobotSpawned contains Planet data which we want to handle before the robot data.
                Channels.planetDTO.send(event as Event<RobotSpawnedEvent>)
                return@runBlocking
            }
            if (Channels.channels.containsKey(event.eventHeader.type)) {
                Channels.channels[event.eventHeader.type]?.send(event)
            } else {
                logger.error("No channel found for event ${event.eventHeader.type}")
            }
        }
    }

    private fun printHeaderAndBody(body: ByteArray, properties: BasicProperties) {
        logger.info("------Header------")
        properties.headers?.forEach { (key, value) ->
            logger.info("| $key : ${String(value as? ByteArray ?: ByteArray(0))}")
        }

        val message = String(body)
        val elementIdMatch = Regex(".*\"id\"\\s*:\\s*\"?(\\w+)\"?.*").find(message)
        val elementId = elementIdMatch?.groupValues?.getOrNull(1)

        logger.info("+------Body-------${if (elementId != null) "\n| ElementId: $elementId" else ""}")
        logger.info("| Received message: $message")
        logger.info("L_________________\n")
    }

}
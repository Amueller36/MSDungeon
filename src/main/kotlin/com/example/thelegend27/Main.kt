package com.example.thelegend27

import com.example.thelegend27.dataendpoint.application.DataEndPointService
import com.example.thelegend27.game.application.GameClient
import com.example.thelegend27.game.domain.GameStateHandler
import org.slf4j.LoggerFactory

fun main() {
    val logger = LoggerFactory.getLogger("Main")
    GameClient.createGame()
    GameClient.patchGameTime(10000)
    Runtime.getRuntime().addShutdownHook(object : Thread() {
        override fun run() {
            preDestroy()
        }
    })
    try {
        DataEndPointService.start()
        GameStateHandler().gameLoop()
    } catch (e: Exception) {
        logger.info("Exception: ${e.message}")
    }


}


fun preDestroy() {
    GameClient.deleteGame()
}

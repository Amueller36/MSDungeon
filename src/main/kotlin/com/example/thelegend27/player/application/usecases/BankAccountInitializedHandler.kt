package com.example.thelegend27.player.application.usecases

import com.example.thelegend27.eventinfrastructure.trading.BankAccountInitializedEvent
import com.example.thelegend27.player.domain.Player
import org.slf4j.LoggerFactory

class BankAccountInitializedHandler {
    private val logger = LoggerFactory.getLogger(BankAccountInitializedHandler::class.java)
    fun handle(event: BankAccountInitializedEvent) {
        Player.moneten = event.balance
        logger.info("Player Moneten initialized to ${Player.moneten}")
    }
}
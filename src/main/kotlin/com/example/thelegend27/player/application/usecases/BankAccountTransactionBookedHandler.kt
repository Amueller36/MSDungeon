package com.example.thelegend27.player.application.usecases

import com.example.thelegend27.eventinfrastructure.trading.BankAccountTransactionBookedEvent
import com.example.thelegend27.player.domain.Player
import org.slf4j.LoggerFactory

class BankAccountTransactionBookedHandler {
    private val logger = LoggerFactory.getLogger(BankAccountTransactionBookedHandler::class.java)
    fun handle(event: BankAccountTransactionBookedEvent) {
        Player.moneten = event.balance
        logger.info("Player Moneten updated to ${Player.moneten}")
    }
}
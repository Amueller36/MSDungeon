package com.example.thelegend27.player.application.usecases

import com.example.thelegend27.eventinfrastructure.trading.BankAccountClearedEvent
import com.example.thelegend27.player.domain.Player
import org.slf4j.LoggerFactory

class BankAccountClearedHandler {
    private val logger = LoggerFactory.getLogger(BankAccountClearedHandler::class.java)
    fun handle(event: BankAccountClearedEvent) {
        Player.moneten = event.balance
        logger.info("Bank account cleared!")
    }
}
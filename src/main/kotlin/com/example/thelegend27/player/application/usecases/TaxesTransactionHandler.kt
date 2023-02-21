package com.example.thelegend27.player.application.usecases

import com.example.thelegend27.trading.domain.UpgradeManager
import org.slf4j.LoggerFactory

class TaxesTransactionHandler {
    private val logger = LoggerFactory.getLogger(TaxesTransactionHandler::class.java)
    suspend fun handle(moneyThatWasTaken: Double) {
        val moneten = UpgradeManager.increaseAvailableMoney(moneyThatWasTaken)
        logger.info("Some Farmer paid $$$ to the UpgradeManager. We now have $moneten moneten to buy new Robots or Upgrades for Fighters")
        UpgradeManager.buyNewRobotsIfFightersAreUpgraded()
    }
}
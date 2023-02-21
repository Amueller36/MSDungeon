package com.example.thelegend27.player.application

import com.example.thelegend27.eventinfrastructure.Channels
import com.example.thelegend27.player.application.usecases.BankAccountClearedHandler
import com.example.thelegend27.player.application.usecases.BankAccountInitializedHandler
import com.example.thelegend27.player.application.usecases.BankAccountTransactionBookedHandler
import com.example.thelegend27.player.application.usecases.TaxesTransactionHandler
import com.example.thelegend27.player.domain.Player
import kotlinx.coroutines.*
import org.slf4j.LoggerFactory
import java.util.*


object PlayerService {
    private val bankAccountInitialized = Channels.bankAccountInitialized
    private val bankAccountTransactionBooked = Channels.bankAccountTransactionBooked
    private val bankAccountCleared = Channels.bankAccountCleared
    private val channelForLeftoverMoney = Channels.channelForLeftoverMoney
    private val logger = LoggerFactory.getLogger(PlayerService::class.java)

    @OptIn(ExperimentalCoroutinesApi::class)
    suspend fun handlePlayerEvents() {
        CoroutineScope(Dispatchers.IO).launch {
            while (true) {
                if (!bankAccountInitialized.isEmpty) {
                    BankAccountInitializedHandler().handle(bankAccountInitialized.receive().eventBody)
                }
                if (!bankAccountCleared.isEmpty) {
                    BankAccountClearedHandler().handle(bankAccountCleared.receive().eventBody)
                }
                if (!bankAccountTransactionBooked.isEmpty) {
                    BankAccountTransactionBookedHandler().handle(bankAccountTransactionBooked.receive().eventBody)
                }
                if (!channelForLeftoverMoney.isEmpty) {
                    TaxesTransactionHandler().handle(channelForLeftoverMoney.receive())
                }
            }
        }


    }

    /**
     * This method should be called when a game is finished.
     * It resets the moneten to 0.0
     */
    fun clear() {
        Player.moneten = 0.0
    }
}
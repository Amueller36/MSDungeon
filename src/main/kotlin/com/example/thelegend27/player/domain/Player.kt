package com.example.thelegend27.player.domain

import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock


object Player {
    val name: String = "TheLegend27"
    val email: String = "andremuller562@gmail.com"
    lateinit var playerQueue: PlayerQueue
    lateinit var playerId: String
    val monetenMutex = Mutex()
    var moneten: Number = 0.0
        get() {
            return runBlocking {
                monetenMutex.withLock {
                    field
                }
            }
        }
        set(value) {
            runBlocking {
                monetenMutex.withLock {
                    field = value
                }
            }
        }

}
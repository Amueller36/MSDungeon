package com.example.thelegend27.trading.domain

enum class Item {
    ROCKET, WORMHOLE, LONGRANGEBOMBARDMENT, SELFFDESTRUCTION, REPAIRSWARM, NUKE,ROBOT;

    fun value(): Int {
        return when (this) {
            ROCKET -> 100
            WORMHOLE -> 100
            LONGRANGEBOMBARDMENT -> 100
            SELFFDESTRUCTION -> 100
            REPAIRSWARM -> 100
            NUKE -> 100
            ROBOT -> 100
        }
    }
}
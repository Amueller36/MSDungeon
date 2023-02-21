package com.example.thelegend27.trading.domain

enum class Restoration {
    HEALTH_RESTORE, ENERGY_RESTORE;

    fun getPrice(): Int {
        return when (this) {
            HEALTH_RESTORE -> 50
            ENERGY_RESTORE -> 75
        }
    }

}
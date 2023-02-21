package com.example.thelegend27.robot.domainprimitives


data class CurrentStatus(val health: Int, val energy: Int) {
    init {
        if (health < 0) {
            throw IllegalArgumentException("Health cannot be negative")
        }
        if (energy < 0) {
            throw IllegalArgumentException("Energy cannot be negative")
        }
    }
}
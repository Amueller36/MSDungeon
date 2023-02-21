package com.example.thelegend27.eventinfrastructure.robot

import com.example.thelegend27.eventinfrastructure.map.PlanetDto
import com.fasterxml.jackson.annotation.JsonProperty

data class RobotDto(
    val planet: PlanetDto,
    val inventory: RobotInventoryDto,
    @JsonProperty("id")
    val robotId: String,
    val alive: Boolean,
    @JsonProperty("player")
    val playerId: String,
    val maxHealth: Int,
    val maxEnergy: Int,
    val energyRegen: Int,
    val attackDamage: Int,
    val miningSpeed: Int,
    val health: Int,
    val energy: Int,
    val healthLevel: Int,
    val damageLevel: Int,
    val miningSpeedLevel: Int,
    val miningLevel: Int,
    val energyLevel: Int,
    val energyRegenLevel: Int
)
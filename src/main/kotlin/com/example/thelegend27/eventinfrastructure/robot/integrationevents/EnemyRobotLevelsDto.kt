package com.example.thelegend27.eventinfrastructure.robot.integrationevents

data class EnemyRobotLevelsDto(
    val damageLevel: Int,
    val energyLevel: Int,
    val energyRegenLevel: Int,
    val storageLevel: Int,
    val healthLevel: Int,
    val miningLevel: Int,
    val miningSpeedLevel: Int
)
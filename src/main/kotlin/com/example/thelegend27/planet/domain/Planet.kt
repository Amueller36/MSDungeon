package com.example.thelegend27.planet.domain

import kotlinx.coroutines.sync.Mutex
import java.util.*


interface Planet {
    val id: UUID
    val gameWorldId: UUID?

    var clusterId: UUID

    val mutex: Mutex

    val neighbours: Map<Direction, Planet>


    fun setNeighbourPlanet(direction: Direction, planet: Planet): Planet

    fun getNeighbourPlanet(direction: Direction): Result<Planet>

    fun addNeighbourPlanet(direction: Direction, planet: Planet): Result<Planet>

}
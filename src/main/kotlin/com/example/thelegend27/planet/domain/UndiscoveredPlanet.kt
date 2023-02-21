package com.example.thelegend27.planet.domain

import com.example.thelegend27.utility.throwables.EntryExistsExeption
import kotlinx.coroutines.sync.Mutex
import java.util.*


class UndiscoveredPlanet(

    override val id: UUID,
    override val gameWorldId: UUID?,

    val movementDifficulty: Int = 1,
    override val neighbours: MutableMap<Direction, Planet> = mutableMapOf<Direction, Planet>(),
    override var clusterId: UUID,
    override val mutex: Mutex = Mutex()
) : Planet {


    override fun equals(other: Any?): Boolean {
        if (other == null || other !is Planet || other is DiscoveredPlanet)
            return false
        return this.id == other.id
    }

    override fun addNeighbourPlanet(direction: Direction, planet: Planet): Result<Planet> {
        if (neighbours.contains(direction)) return Result.failure<DiscoveredPlanet>(EntryExistsExeption("Entry in "))
        neighbours[direction] = planet
        return Result.success(this)
    }

    override fun setNeighbourPlanet(direction: Direction, planet: Planet): Planet {
        neighbours[direction] = planet
        return this
    }



    override fun getNeighbourPlanet(direction: Direction): Result<Planet> {
        return if (neighbours.contains(direction)) Result.success<Planet>(neighbours[direction]!!)
        else Result.failure(Throwable("No Entry"))
    }

    fun toDiscoveredPlanet(discoveredPlanet: DiscoveredPlanet): DiscoveredPlanet {
        val convertedPlanet = DiscoveredPlanet(
            id = discoveredPlanet.id,
            gameWorldId = gameWorldId,
            movementDifficulty = discoveredPlanet.movementDifficulty,
            rechargeMultiplicator = discoveredPlanet.rechargeMultiplicator,
            planetType = discoveredPlanet.planetType,
            clusterId = clusterId,
            deposit = discoveredPlanet.deposit
        )
        this.neighbours.forEach { (direction, planet) ->
            convertedPlanet.addNeighbourPlanet(direction, planet)
        }
        return convertedPlanet
    }
}
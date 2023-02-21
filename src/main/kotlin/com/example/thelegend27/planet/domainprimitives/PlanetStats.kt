package com.example.thelegend27.planet.domainprimitives

import com.example.thelegend27.planet.domain.Direction
import com.example.thelegend27.planet.domain.DiscoveredPlanet
import com.example.thelegend27.planet.domain.Planet
import java.util.*

data class PlanetStats(
    val id : UUID,
    val clusterId : UUID?,
    val movementDifficulty: Int?,
    val resources : ResourceDeposit?,
    val neighbours : List<Pair<Direction,UUID>>
    ) {

    companion object{
        fun fromNothing(id : UUID) : PlanetStats {
            return PlanetStats(id,null,0,null, emptyList())
        }
        fun empty(id : UUID) :PlanetStats {
            return PlanetStats(id,null,null,null, emptyList())
        }
        fun fromPlanet(planet : Planet) : PlanetStats{
            return PlanetStats(
                id =planet.id,
                clusterId = planet.clusterId ,
                movementDifficulty = null,
                resources = null,
                neighbours = planet.neighbours.map{Pair(it.key,it.value.id)}.toList()
            )
        }
        fun fromDiscoveredPlanet(planet: DiscoveredPlanet) :PlanetStats{
            return PlanetStats(
                id =planet.id,
                clusterId = planet.clusterId ,
                movementDifficulty = planet.movementDifficulty,
                resources = if(planet.deposit is ResourceDeposit) planet.deposit as ResourceDeposit else null ,
                neighbours = planet.neighbours.map{Pair(it.key,it.value.id)}.toList()
            )
        }

    }

}
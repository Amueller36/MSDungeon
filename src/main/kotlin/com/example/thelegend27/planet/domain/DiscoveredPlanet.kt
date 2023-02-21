package com.example.thelegend27.planet.domain


import com.example.thelegend27.planet.domainprimitives.Deposit
import com.example.thelegend27.utility.throwables.EntryExistsExeption
import kotlinx.coroutines.sync.Mutex
import java.util.*


class DiscoveredPlanet(

    override val id: UUID, override val gameWorldId: UUID?,

    val movementDifficulty: Int = 0, val rechargeMultiplicator: Int = 0, var planetType: String = "default",

    var deposit: Deposit,
    override var clusterId: UUID,
    override val mutex: Mutex = Mutex()

) : Planet {

    override fun equals(other: Any?): Boolean {
        if (other == null || other !is Planet || other is UndiscoveredPlanet)
            return false
        return this.id == other.id
    }

    override val neighbours: MutableMap<Direction, Planet> = EnumMap(Direction::class.java)

    override fun addNeighbourPlanet(direction: Direction, planet: Planet): Result<DiscoveredPlanet> {
        if (neighbours.contains(direction)) return Result.failure<DiscoveredPlanet>(EntryExistsExeption("Entry in "))
        neighbours[direction] = planet
        return Result.success(this)
    }

    override fun setNeighbourPlanet(direction: Direction, planet: Planet): DiscoveredPlanet {
        neighbours[direction] = planet
        return this
    }

    override fun getNeighbourPlanet(direction: Direction): Result<Planet> {
        return if (neighbours.contains(direction)) Result.success<Planet>(neighbours[direction]!!)
        else Result.failure(Throwable("No Entry"))
    }


//    fun findShortestPath(planets: List<Planet>): UUID {
//        if (planets.contains(this)) return this.id
//        var closestPlanet: Planet? = null
//        var shortestPathToClosestPlanet = Int.MAX_VALUE
//        var pathToClosestPlanet = mutableListOf<Planet>()
//        for (planet in planets) {
//            val pathToPlanet = findPathToPlanet(planet)
//            if (pathToPlanet.size < shortestPathToClosestPlanet) {
//                shortestPathToClosestPlanet = pathToPlanet.size
//                pathToClosestPlanet = pathToPlanet
//            }
//        }
//        if (pathToClosestPlanet.isEmpty()) {
//            return this.id
//        }
//        if (pathToClosestPlanet[0] != this) return pathToClosestPlanet[0].id
//        return pathToClosestPlanet[1].id
//    }

//    fun findPathToPlanet(targetPlanet: Planet): MutableList<Planet> {
//        if (this == targetPlanet) return mutableListOf(this)
//        val queue = mutableListOf<Planet>(this)
//        val visited = mutableSetOf<Planet>()
//        val path = mutableMapOf<Planet, Planet>()
//
//        while (queue.isNotEmpty()) {
//            val currentPlanet = queue.removeAt(0)
//            if (currentPlanet == targetPlanet) {
//                val pathToPlanet = mutableListOf<Planet>()
//                var planet: Planet? = targetPlanet
//                while (planet != null) {
//                    pathToPlanet.add(0, planet)
//                    planet = path[planet]
//                }
//                return pathToPlanet
//            }
//
//            visited.add(currentPlanet)
//            for (neighbor in currentPlanet.neighbours.values) {
//                if (neighbor !in visited) {
//                    queue.add(neighbor)
//                    path[neighbor] = currentPlanet
//                }
//            }
//        }
//        return mutableListOf()
//    }


}
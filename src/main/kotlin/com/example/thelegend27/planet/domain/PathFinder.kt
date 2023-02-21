package com.example.thelegend27.planet.domain

import java.util.*

class PathFinder {
    private val pathCache = mutableMapOf<Pair<Planet, Planet>, MutableList<Planet>>()
    fun getShortestPathFromPlanetToPlanet(fromPlanet: Planet, toPlanet: Planet): List<Planet> {
        val key = Pair(fromPlanet, toPlanet)
        val reversedKey = Pair(toPlanet, fromPlanet)
        return when {
            pathCache.containsKey(key) -> pathCache[key]!!
            pathCache.containsKey(reversedKey) -> pathCache[reversedKey]!!.reversed()
            else -> {
                val path = calculateShortestPathFromPlanetToPlanet(fromPlanet, toPlanet)
                if (path.size > 1) {
                    pathCache[key] = path
                    pathCache[reversedKey] = path.asReversed()
                }
                path
            }
        }
    }

    fun getShortestPathFromPlanetToListOfPlanets(fromPlanet: Planet, toPlanets: List<Planet>): List<Planet> {
        val shortestPath = mutableListOf<Planet>()
        toPlanets.forEach { planet ->
            val path = getShortestPathFromPlanetToPlanet(fromPlanet, planet)
            if (path.size < shortestPath.size || shortestPath.isEmpty()) {
                shortestPath.clear()
                shortestPath.addAll(path)
            }
        }
        return shortestPath
    }

    private fun calculateShortestPathFromPlanetToPlanet(fromPlanet: Planet, toPlanet: Planet): MutableList<Planet> {
        if (fromPlanet == toPlanet) return mutableListOf(fromPlanet)
        val priorityQueue = PriorityQueue<Pair<Int, Planet>> { a, b -> a.first.compareTo(b.first) }
        priorityQueue.offer(Pair(0, fromPlanet))
        val visited = mutableSetOf<Planet>()
        val path = mutableMapOf<Planet, Planet>()
        val costSoFar = mutableMapOf<Planet, Int>()
        costSoFar[fromPlanet] = 0

        while (priorityQueue.isNotEmpty()) {
            val current = priorityQueue.poll().second
            if (current == toPlanet) {
                val pathToPlanet = mutableListOf<Planet>()
                var planet: Planet? = toPlanet
                while (planet != null) {
                    pathToPlanet.add(0, planet)
                    planet = path[planet]
                }
                return pathToPlanet
            }

            visited.add(current)
            for (neighbor in current.neighbours.values) {
                val cost = costSoFar[current]!! + 1
                if (neighbor !in visited && (!costSoFar.containsKey(neighbor) || cost < costSoFar[neighbor]!!)) {
                    priorityQueue.offer(Pair(cost + heuristic(neighbor, toPlanet), neighbor))
                    path[neighbor] = current
                    costSoFar[neighbor] = cost
                }
            }
        }
        return mutableListOf()
    }

    /**
     * The heuristic function does not take the movement difficulty into account,
     * just the amount of rounds it would take to get to the target planet, because
     * we think that the movement difficulty is not that important.
     */
    private fun heuristic(planet: Planet, targetPlanet: Planet): Int {
        return 1
    }
}
package com.example.thelegend27.planet.domain

import java.util.*

class Cluster {

    private val planets: MutableSet<UUID> = mutableSetOf<UUID>()

    val getPlanetIds: Set<UUID>
        get() {
            return planets.toSet()
        }

    val size: Int
        get() = planets.size

    val id: UUID = UUID.randomUUID()

    fun mergableWith(that: Cluster): Boolean {

        return (this.getPlanetIds intersect that.getPlanetIds).isNotEmpty()

    }

    fun addToCluster(planet: Planet) {
        planets.add(planet.id)
    }


    private fun addMultipleToCluster(planetIdCollection: Iterable<UUID>) {
        planets.addAll(planetIdCollection)
    }


    fun mergeWithToNewCluster(that: Cluster): Result<Cluster> {

        return if (mergableWith(that)) {
            //merge List
            val mergeddata = this.getPlanetIds union that.getPlanetIds
            //sync data
            val newCluster = Cluster()

            newCluster.addMultipleToCluster(mergeddata)

            Result.success(newCluster)

        } else Result.failure(Throwable("Cluster of ID: ${this.id} not merge able with Cluster of ID: ${that.id}"))

    }

    fun mergeInto(from: Cluster): Result<Cluster> {
        return if (mergableWith(from)) {
            this.addMultipleToCluster(
                this.getPlanetIds union from.getPlanetIds
            )
            Result.success(this)
        } else Result.failure(Throwable("Cluster of ID: ${this.id} not merge able with Cluster of ID: ${from.id}"))
    }

    fun mergeIntoPrechecked(from: Cluster) {
        this.addMultipleToCluster(
            this.getPlanetIds.union(from.getPlanetIds)
        )
    }


}
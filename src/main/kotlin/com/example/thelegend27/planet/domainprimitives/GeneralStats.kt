package com.example.thelegend27.planet.domainprimitives

import kotlinx.serialization.Serializable

@Serializable
data class GeneralStats private constructor(
    val clusterAmount : Int?,
    val planetAmount : Int?,
    val planetsContainingClusterInformation: Int?,
    val registeredPlanetsInAllClusters : Int?

){
    fun appendClusterAmount (amount : Int) : GeneralStats
        {
            return GeneralStats(
                clusterAmount = amount,
                planetAmount,
                planetsContainingClusterInformation,
                registeredPlanetsInAllClusters
            )
        }

    fun appendPlanetAmount (amount : Int) : GeneralStats {
        return GeneralStats(
            clusterAmount,
            planetAmount = amount,
            planetsContainingClusterInformation,
            registeredPlanetsInAllClusters
        )
    }

    fun appendPlanetsContainingClusterInformation  (amount : Int) : GeneralStats {
        return GeneralStats(
            clusterAmount,
            planetAmount,
            planetsContainingClusterInformation = amount,
            registeredPlanetsInAllClusters
        )
    }

    fun appendRegisteredPlanetsInAllClusters (amount: Int) : GeneralStats {
        return GeneralStats(
            clusterAmount,
            planetAmount,
            planetsContainingClusterInformation,
            registeredPlanetsInAllClusters = amount
        )
    }


    companion object {
        fun fromNothing() : GeneralStats{
            return GeneralStats(0,0,0,0)
        }
        fun empty() : GeneralStats {
            return GeneralStats(null,null,null,null)
        }
    }
}




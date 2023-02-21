package com.example.thelegend27

import com.example.thelegend27.planet.domain.*
import com.example.thelegend27.planet.domainprimitives.DiscoveredDeposit
import com.example.thelegend27.trading.domain.Resource
import java.util.*

class PlanetClusterCreator{

    val planetCluster1 = mutableListOf<Planet>()
    val cluster1 = Cluster()
    val planetCluster2 = mutableListOf<Planet>()
    val cluster2 = Cluster()
    val planet6ID = UUID.randomUUID()


    val planet1 = DiscoveredPlanet(UUID.randomUUID(),null, clusterId = cluster1.id,  deposit = DiscoveredDeposit(Resource.COAL,10000,10000 ))
    val planet2 = DiscoveredPlanet(UUID.randomUUID(),null, clusterId = cluster1.id,  deposit = DiscoveredDeposit(Resource.COAL,10000,10000))
    val planet3 = DiscoveredPlanet(UUID.randomUUID(),null, clusterId = cluster1.id, deposit = DiscoveredDeposit(Resource.IRON,10000,10000))
    val planet4 = DiscoveredPlanet(UUID.randomUUID(),null, clusterId = cluster1.id, deposit = DiscoveredDeposit(Resource.GEM,10000,10000))
    val planet5 = DiscoveredPlanet(UUID.randomUUID(),null, clusterId = cluster1.id, deposit = DiscoveredDeposit(Resource.PLATIN,10000,10000))
    val planet6 = DiscoveredPlanet(UUID.randomUUID(),null, clusterId = cluster1.id,  deposit = DiscoveredDeposit(Resource.PLATIN,10000,10000))
    val planet7 = DiscoveredPlanet(UUID.randomUUID(),null, clusterId = cluster2.id,  deposit = DiscoveredDeposit(Resource.COAL,10000,10000))
    val planet8 = UndiscoveredPlanet(UUID.randomUUID(),null , clusterId = cluster2.id)
    val planet9 = UndiscoveredPlanet(UUID.randomUUID(),null , clusterId = cluster2.id)
    init {


        // Diesen Planeten später mit planet 7 mergen, damit das als ein planet angesehen wird. Also Planet 7 wird Planet 6.
       // val planet6 = DiscoveredPlanet(planet6ID,null, clusterId = cluster1.id,  deposit = DiscoveredDeposit(Resource.PLATIN,10000,10000))


        planet2.addNeighbourPlanet(Direction.WEST, planet1)

        planet3.addNeighbourPlanet(Direction.WEST, planet2)
        planet3.addNeighbourPlanet(Direction.SOUTH, planet5)

        planet4.addNeighbourPlanet(Direction.NORTH, planet2)

        planet5.addNeighbourPlanet(Direction.WEST, planet4)

        planet5.addNeighbourPlanet(Direction.NORTH, planet3)

        planet6.addNeighbourPlanet(Direction.WEST, planet5)
        planet5.addNeighbourPlanet(Direction.EAST, planet6)
        planet4.addNeighbourPlanet(Direction.EAST, planet5)
        planet2.addNeighbourPlanet(Direction.SOUTH, planet4)
        planet2.addNeighbourPlanet(Direction.EAST, planet3)

        planet1.addNeighbourPlanet(Direction.EAST, planet2)





        planetCluster1.add(planet1)
        planetCluster1.add(planet2)
        planetCluster1.add(planet3)
        planetCluster1.add(planet4)
        planetCluster1.add(planet5)
        planetCluster1.add(planet6)
        planetCluster1.forEach{cluster1.addToCluster(it)}



        planet7.addNeighbourPlanet(Direction.WEST, planet8)
        planet7.addNeighbourPlanet(Direction.NORTH, planet9)

        planet8.addNeighbourPlanet(Direction.EAST, planet7)

        planet9.addNeighbourPlanet(Direction.SOUTH, planet7)

        planetCluster2.add(planet7)
        planetCluster2.add(planet8)
        planetCluster2.add(planet9)
        planetCluster1.forEach{cluster2.addToCluster(it)}
    }
}
package com.example.thelegend27.planet.application

import com.example.thelegend27.PlanetClusterCreator
import com.example.thelegend27.planet.domain.DiscoveredPlanet
import com.example.thelegend27.planet.domainprimitives.DiscoveredDeposit
import com.example.thelegend27.trading.domain.Resource
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class PlanetServiceTest {

    /*
Cluster looks as follows, C = Coal I = Iron G = Gem, P = Platin
  1C-2C-3I
     |  |
     4G-5P-6P
 */
    @Test
    fun calculateShortestPathFromPlanetToPlanet() {
        val planetClusterCreator = PlanetClusterCreator()

        val path = PlanetService.getShortestPathFromPlanetToPlanet(
            planetClusterCreator.planet1,
            planetClusterCreator.planet4
        )
        println("planet 1 ${planetClusterCreator.planet1.id} to planet 2 ${planetClusterCreator.planet2.id} to planet4 ${planetClusterCreator.planet4.id}")
        path.forEach { println(it.id) }
        assert(path.size == 3)
        assert(path[0].id == planetClusterCreator.planet1.id)
        assert(path[1].id == planetClusterCreator.planet2.id)
        assert(path[2].id == planetClusterCreator.planet4.id)
        val path2 = PlanetService.getShortestPathFromPlanetToPlanet(
            planetClusterCreator.planet1,
            planetClusterCreator.planet5
        )
    }

    /*
 Cluster looks as follows, C = Coal I = Iron G = Gem, P = Platin
   1C-2C-3I
      |  |
      4G-5P-6P
  */
    @Test
    fun testFindShortestPathToListOfPlanetsMethod() {
        val planetClusterCreator = PlanetClusterCreator()
        println("planet1: ${planetClusterCreator.planet1.id} \n planet2: ${planetClusterCreator.planet2.id} \n planet3: ${planetClusterCreator.planet3.id} \n planet4: ${planetClusterCreator.planet4.id} \n planet5: ${planetClusterCreator.planet5.id} \n planet6: ${planetClusterCreator.planet6.id} \n planet7: ${planetClusterCreator.planet7.id} \n planet8: ${planetClusterCreator.planet8.id} \n planet9: ${planetClusterCreator.planet9.id}")
        val resultClosestCoalPlanet = PlanetService.getShortestPathFromPlanetToListOfPlanets(
            planetClusterCreator.planet1,
            planetClusterCreator.planetCluster1.filter { ((it as DiscoveredPlanet).deposit as DiscoveredDeposit).resourceType == Resource.COAL })[0].id
        assertEquals(planetClusterCreator.planet1.id, resultClosestCoalPlanet)
        val resultUUID2 = PlanetService.getShortestPathFromPlanetToListOfPlanets(
            planetClusterCreator.planet1,
            planetClusterCreator.planetCluster1.filter { ((it as DiscoveredPlanet).deposit as DiscoveredDeposit).resourceType == Resource.PLATIN })[1].id
        assertEquals(planetClusterCreator.planet2.id, resultUUID2)
        val resultUUID3 = PlanetService.getShortestPathFromPlanetToListOfPlanets(
            planetClusterCreator.planet3,
            planetClusterCreator.planetCluster1.filter { ((it as DiscoveredPlanet).deposit as DiscoveredDeposit).resourceType == Resource.PLATIN })[1].id
        assertEquals(planetClusterCreator.planet5.id, resultUUID3)
        val resultUUID4 = PlanetService.getShortestPathFromPlanetToListOfPlanets(
            planetClusterCreator.planet5,
            planetClusterCreator.planetCluster1.filter { ((it as DiscoveredPlanet).deposit as DiscoveredDeposit).resourceType == Resource.GEM })[1].id
        assertEquals(planetClusterCreator.planet4.id, resultUUID4)
        val resultUUID5 = PlanetService.getShortestPathFromPlanetToListOfPlanets(
            planetClusterCreator.planet5,
            planetClusterCreator.planetCluster1.filter { ((it as DiscoveredPlanet).deposit as DiscoveredDeposit).resourceType == Resource.IRON })[1].id
        assertEquals(planetClusterCreator.planet3.id, resultUUID5)
        val resultUUID6 = PlanetService.getShortestPathFromPlanetToListOfPlanets(
            planetClusterCreator.planet6,
            planetClusterCreator.planetCluster1.filter { ((it as DiscoveredPlanet).deposit as DiscoveredDeposit).resourceType == Resource.GEM })[1].id
        assertEquals(planetClusterCreator.planet5.id, resultUUID6)
        val resultUUID7 = PlanetService.getShortestPathFromPlanetToListOfPlanets(
            planetClusterCreator.planet5,
            planetClusterCreator.planetCluster1.filter { ((it as DiscoveredPlanet).deposit as DiscoveredDeposit).resourceType == Resource.GEM })[1].id
        assertEquals(planetClusterCreator.planet4.id, resultUUID7)
        val resultUUID8 = PlanetService.getShortestPathFromPlanetToListOfPlanets(
            planetClusterCreator.planet5,
            planetClusterCreator.planetCluster1.filter { ((it as DiscoveredPlanet).deposit as DiscoveredDeposit).resourceType == Resource.PLATIN && it.id != planetClusterCreator.planet5.id })[1].id
        assertEquals(planetClusterCreator.planet6.id, resultUUID8)
        val pathToGoldPlanets = PlanetService.getShortestPathFromPlanetToListOfPlanets(
            planetClusterCreator.planet5,
            planetClusterCreator.planetCluster1.filter { ((it as DiscoveredPlanet).deposit as DiscoveredDeposit).resourceType == Resource.GOLD })
        assertEquals(0, pathToGoldPlanets.size)
    }

}
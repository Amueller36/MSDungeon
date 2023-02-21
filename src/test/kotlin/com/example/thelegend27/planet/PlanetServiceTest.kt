//package com.example.thelegend27.planet
//
//
//import com.example.thelegend27.planet.domainprimitives.NoDeposit
//import com.example.thelegend27.planet.application.PlanetService
//import com.example.thelegend27.planet.domain.DiscoveredPlanet
//import kotlinx.coroutines.runBlocking
//import org.junit.jupiter.api.Assertions.assertFalse
//import org.junit.jupiter.api.Assertions.assertTrue
//import org.junit.jupiter.api.BeforeEach
//import org.junit.jupiter.api.DisplayName
//import org.junit.jupiter.api.Test
//import com.example.thelegend27.planet.domain.Direction
//import com.example.thelegend27.planet.domain.UndiscoveredPlanet
//import java.util.*
//
//internal class PlanetServiceTest {
//
//
//    val map = PlanetService
//
//    @BeforeEach
//    fun clearMap() {
//        map.clearRepository()
//    }
//
//    @Test
//    @DisplayName("Discovery Test")
//    fun testDiscover() {
//
//        val discorveredPlanetId = UUID.randomUUID()
//        val gameworldId = UUID.randomUUID()
//
//        val randomPlanet1 = UndiscoveredPlanet(UUID.randomUUID(), gameworldId)
//        val randomPlanet2 = UndiscoveredPlanet(UUID.randomUUID(), gameworldId)
//        val discorveredPlanet1 = DiscoveredPlanet(discorveredPlanetId, gameworldId, deposit = NoDeposit)
//
//        map.registerPlanet(randomPlanet1)
//        map.registerPlanet(randomPlanet2)
//        map.registerPlanet(discorveredPlanet1)
//
//        discorveredPlanet1.addNeighbourPlanet(Direction.NORTH, randomPlanet1)
//        discorveredPlanet1.addNeighbourPlanet(Direction.WEST, randomPlanet2)
//
//        //Simulated Representation of processed Planet visit Event
//        runBlocking {
//            map.discoverPlanet(
//                DiscoveredPlanet(randomPlanet1.id, randomPlanet1.gameWorldId, deposit = NoDeposit)
//                    .setNeighbourPlanet(
//                        Direction.SOUTH,
//                        UndiscoveredPlanet(discorveredPlanetId, gameworldId)
//                    ) as DiscoveredPlanet
//            )
//        }
//
//
//        assertFalse(
//            discorveredPlanet1.getNeighbourPlanet(Direction.NORTH).getOrNull()!! == randomPlanet1,
//            "Planet should be Discovered"
//        )
//        assertTrue(
//            discorveredPlanet1.getNeighbourPlanet(Direction.NORTH).getOrNull()!! is DiscoveredPlanet,
//            "Wrong Planet Type"
//        )
//    }
//
//    @Test
//    fun DiscoverMultipleTest() {
//        val gameId = UUID.randomUUID()
//
//        val start = DiscoveredPlanet(UUID.randomUUID(), gameId, deposit = NoDeposit)
//
//        //Initial undiscovered as reference
//        val northPlanet = UndiscoveredPlanet(UUID.randomUUID(), gameId)
//        val northWestPlanet = UndiscoveredPlanet(UUID.randomUUID(), gameId)
//        val westPlanet = UndiscoveredPlanet(UUID.randomUUID(), gameId)
//        val eastPlanet = UndiscoveredPlanet(UUID.randomUUID(), gameId)
//
//        start.addNeighbourPlanet(Direction.NORTH, northPlanet)
//        start.addNeighbourPlanet(Direction.WEST, westPlanet)
//        start.addNeighbourPlanet(Direction.EAST, eastPlanet)
//        map.registerPlanet(start)
//        map.registerPlanet(northPlanet)
//        map.registerPlanet(northWestPlanet)
//        map.registerPlanet(westPlanet)
//        map.registerPlanet(eastPlanet)
//
//        // Discover North
//
//        runBlocking {
//            map.discoverPlanet(
//                DiscoveredPlanet(northPlanet.id, gameId, deposit = NoDeposit)
//                    .setNeighbourPlanet(
//                        Direction.SOUTH,
//                        UndiscoveredPlanet(start.id, gameId)
//                    )
//                    .setNeighbourPlanet(Direction.WEST, northWestPlanet) as DiscoveredPlanet
//            )
//        }
//        assertTrue(map.getPlanetById(northPlanet.id).getOrNull() is DiscoveredPlanet)
//        assertTrue {
//            val north = map.getPlanetById(northPlanet.id).getOrNull()
//            north is DiscoveredPlanet
//                    && north.getNeighbourPlanet(Direction.SOUTH).getOrNull() is DiscoveredPlanet
//                    && north.getNeighbourPlanet(Direction.SOUTH).getOrNull() == start
//                    && north.getNeighbourPlanet(Direction.WEST).getOrNull() is UndiscoveredPlanet
//                    && start.getNeighbourPlanet(Direction.NORTH).getOrNull() == north
//
//        }
//
//        // Discover West
//        runBlocking {
//            map.discoverPlanet(
//                DiscoveredPlanet(westPlanet.id, gameId, deposit = NoDeposit)
//                    .setNeighbourPlanet(
//                        Direction.EAST,
//                        UndiscoveredPlanet(start.id, gameId)
//                    )
//                    .setNeighbourPlanet(Direction.NORTH, northWestPlanet) as DiscoveredPlanet
//            )
//        }
//
//
//        assertTrue(map.getPlanetById(westPlanet.id).getOrNull() is DiscoveredPlanet)
//        assertTrue {
//            val west = map.getPlanetById(westPlanet.id).getOrNull()
//            west is DiscoveredPlanet
//                    && west.getNeighbourPlanet(Direction.EAST).getOrNull() == start
//                    && west.getNeighbourPlanet(Direction.EAST).getOrNull() is DiscoveredPlanet
//                    && west.getNeighbourPlanet(Direction.NORTH).getOrNull() is UndiscoveredPlanet
//                    && start.getNeighbourPlanet(Direction.WEST).getOrNull() == west
//
//        }
//
//        // Discover Northwest
//        runBlocking {
//            map.discoverPlanet(
//                DiscoveredPlanet(northWestPlanet.id, gameId, deposit = NoDeposit)
//                    .setNeighbourPlanet(Direction.EAST, northPlanet)
//                    .setNeighbourPlanet(Direction.SOUTH, westPlanet) as DiscoveredPlanet
//            )
//        }
//
//
//        assertTrue(map.getPlanetById(northWestPlanet.id).getOrNull() is DiscoveredPlanet)
//        assertTrue {
//            val northwest = map.getPlanetById(northWestPlanet.id).getOrNull()
//            northwest is DiscoveredPlanet
//                    && northwest.getNeighbourPlanet(Direction.EAST)
//                .getOrNull() == start.getNeighbourPlanet(Direction.NORTH)
//                .getOrNull()
//                    && northwest.getNeighbourPlanet(Direction.EAST).getOrNull() is DiscoveredPlanet
//                    && northwest.getNeighbourPlanet(Direction.SOUTH).getOrNull() is DiscoveredPlanet
//                    && start.getNeighbourPlanet(Direction.WEST).getOrNull() == start.getNeighbourPlanet(Direction.WEST)
//                .getOrNull()
//        }
//
//
//    }
//
//
//}
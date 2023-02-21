package com.example.thelegend27.robot.domain.strategies.farming

import com.example.thelegend27.PlanetClusterCreator
import com.example.thelegend27.robot.application.RobotService
import com.example.thelegend27.robot.domain.Robot
import com.example.thelegend27.robot.domain.RobotRepository
import com.example.thelegend27.trading.domain.Resource
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.*

class RobotSpawnedHandlerTest {
    lateinit var robot1: Robot
    lateinit var robot2: Robot
    lateinit var robot3: Robot
    lateinit var robot4: Robot
    lateinit var robot5: Robot
    lateinit var robot6: Robot
    lateinit var robot7: Robot
    lateinit var robot8: Robot
    val planetClusterCreator = PlanetClusterCreator()

    @BeforeEach
    fun setup() {
        RobotRepository.clear()
        robot1 = Robot(UUID.randomUUID(), planetClusterCreator.planet1)
        robot2 = Robot(UUID.randomUUID(), planetClusterCreator.planet2)
        robot3 = Robot(UUID.randomUUID(), planetClusterCreator.planet3)
        robot4 = Robot(UUID.randomUUID(), planetClusterCreator.planet4)
        robot5 = Robot(UUID.randomUUID(), planetClusterCreator.planet5)
        robot6 = Robot(UUID.randomUUID(), planetClusterCreator.planet6)
        robot7 = Robot(UUID.randomUUID(), planetClusterCreator.planet7)
        robot8 = Robot(UUID.randomUUID(), planetClusterCreator.planet8)
    }

    @AfterEach
    fun clearRepositoryAfter() {
        RobotRepository.clear()
    }

    @Test
    fun testGetRobotAmountByStrategy() {
        robot1.strategy = FarmStrategy(robot1, Resource.COAL)
        robot3.strategy = FarmStrategy(robot3, Resource.COAL)
        RobotRepository.add(robot1)
        RobotRepository.add(robot3)
        val result = RobotService.getAmountOfOurRobots()
        assertEquals(2, result)
        RobotRepository.removeElement(robot1)
        val result2 = RobotService.getAmountOfOurRobots()
        assertEquals(1, result2)
    }

//    @Test
//    fun properStrategyIsBeingChosenBasedOnExistingRobots() {
//        runBlocking {
//            robot1.strategy = FarmStrategy(robot1, Resource.COAL)
//            robot2.strategy = FarmStrategy(robot2, Resource.COAL)
//            robot3.strategy = FarmStrategy(robot3, Resource.COAL)
//            RobotRepository.add(robot1)
//            RobotRepository.add(robot2)
//            RobotRepository.add(robot3)
//            //100% of our Robots are farmers, so next robot should be an explorer
//            val robot4SpawnedEvent = createRobotSpawnedEventBasedOnRobot(robot4)
//            RobotSpawnedHandler().handle(robot4SpawnedEvent)
//            assertEquals(robot4.strategy is ExploreStrategy, true)
//            robot5.strategy = ExploreStrategy(robot5)
//            RobotRepository.add(robot5)
//            //50% of our Robots are farmers, so next robot should be a farmer
//        }
//    }
//
//    fun create15FarmerRobotsAndAddToRobotRepository() {
//        val robots = mutableListOf<Robot>()
//        val robot1 = Robot(UUID.randomUUID(), planetClusterCreator.planet1)
//        val robot2 = Robot(UUID.randomUUID(), planetClusterCreator.planet2)
//        val robot3 = Robot(UUID.randomUUID(), planetClusterCreator.planet3)
//        val robot4 = Robot(UUID.randomUUID(), planetClusterCreator.planet4)
//        val robot5 = Robot(UUID.randomUUID(), planetClusterCreator.planet5)
//        val robot6 = Robot(UUID.randomUUID(), planetClusterCreator.planet6)
//        val robot7 = Robot(UUID.randomUUID(), planetClusterCreator.planet7)
//        val robot8 = Robot(UUID.randomUUID(), planetClusterCreator.planet8)
//        val robot9 = Robot(UUID.randomUUID(), planetClusterCreator.planet9)
//        val robot10 = Robot(UUID.randomUUID(), planetClusterCreator.planet1)
//        val robot11 = Robot(UUID.randomUUID(), planetClusterCreator.planet2)
//        val robot12 = Robot(UUID.randomUUID(), planetClusterCreator.planet3)
//        val robot13 = Robot(UUID.randomUUID(), planetClusterCreator.planet4)
//        val robot14 = Robot(UUID.randomUUID(), planetClusterCreator.planet5)
//        val robot15 = Robot(UUID.randomUUID(), planetClusterCreator.planet6)
//        robot1.strategy = FarmStrategy(robot1, Resource.COAL)
//        robot2.strategy = FarmStrategy(robot2, Resource.COAL)
//        robot3.strategy = FarmStrategy(robot3, Resource.GOLD)
//        robot4.strategy = FarmStrategy(robot4, Resource.COAL)
//        robot5.strategy = FarmStrategy(robot5, Resource.GEM)
//        robot6.strategy = FarmStrategy(robot6, Resource.COAL)
//        robot7.strategy = FarmStrategy(robot7, Resource.GOLD)
//        robot8.strategy = FarmStrategy(robot8, Resource.COAL)
//        robot9.strategy = FarmStrategy(robot9, Resource.IRON)
//        robot10.strategy = FarmStrategy(robot10, Resource.COAL)
//        robot11.strategy = FarmStrategy(robot11, Resource.IRON)
//        robot12.strategy = FarmStrategy(robot12, Resource.COAL)
//        robot13.strategy = FarmStrategy(robot13, Resource.IRON)
//        robot14.strategy = FarmStrategy(robot14, Resource.COAL)
//        robot15.strategy = FarmStrategy(robot15, Resource.PLATIN)
//    }
//
//    fun addRobotsToRepo(listOfRobots: List<Robot>) {
//        for (robot in listOfRobots) {
//            RobotRepository.add(robot)
//        }
//    }
//
//    fun createRobotSpawnedEventBasedOnRobot(robot: Robot): RobotSpawnedEvent {
//        val planetDto = PlanetDto(robot.currentPlanet.id.toString(), UUID.randomUUID().toString(), 1, "COAL")
//        val robotInventoryDto = RobotInventoryDto(0, 0, 20, false, ResourceInventoryDto())
//        val robot = RobotDto(
//            planetDto,
//            robotInventoryDto,
//            robot.id.toString(),
//            true,
//            UUID.randomUUID().toString(),
//            10,
//            20,
//            2,
//            2,
//            2,
//            10,
//            20,
//            0,
//            0,
//            0,
//            0,
//            0,
//            0
//        )
//        return RobotSpawnedEvent(robot)
//    }


}
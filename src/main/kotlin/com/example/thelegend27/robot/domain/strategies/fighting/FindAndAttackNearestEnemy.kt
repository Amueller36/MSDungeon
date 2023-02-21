package com.example.thelegend27.robot.domain.strategies.fighting

import com.example.thelegend27.planet.application.PlanetService
import com.example.thelegend27.planet.domain.DiscoveredPlanet
import com.example.thelegend27.planet.domain.Planet
import com.example.thelegend27.robot.application.RobotService
import com.example.thelegend27.robot.domain.EnemyRobot
import com.example.thelegend27.robot.domain.Robot
import com.example.thelegend27.robot.domain.RobotRepository
import com.example.thelegend27.robot.domain.commands.Command
import com.example.thelegend27.robot.domain.strategies.RobotStrategy
import com.example.thelegend27.robot.domain.strategies.commonstrategies.AttackTarget
import com.example.thelegend27.robot.domain.strategies.commonstrategies.MoveToGivenPlanet
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import org.slf4j.LoggerFactory
import java.util.*

class FindAndAttackNearestEnemy(
    private var robot: Robot,
    val filterForEnemyRobots: List<(enemyRobot: List<EnemyRobot>) -> List<EnemyRobot>>
) : RobotStrategy {
    private val logger = LoggerFactory.getLogger(FindAndAttackNearestEnemy::class.java)

    @OptIn(ExperimentalCoroutinesApi::class)
    private val robotFlow = RobotRepository.asFlow().map { it[robot.id] }

    override suspend fun getCommand(): Command? {
        robot = robotFlow.first() ?: return null
        val currentPlanet = robot.currentPlanet as DiscoveredPlanet
        val closestPlanetWithEnemy = getClosestPlanetWithBeatableEnemy(currentPlanet)
        if (closestPlanetWithEnemy != null) {
            if (closestPlanetWithEnemy.id == currentPlanet.id) {
                val enemyRobotToAttack = chooseTarget()
                logFightInfo(enemyRobotToAttack)
                return AttackTarget(robot, enemyRobotToAttack).getCommand()
            }
            return MoveToGivenPlanet(robot, closestPlanetWithEnemy).getCommand()
        }
        return null
    }


    private fun logFightInfo(enemyRobot: EnemyRobot) {
        logger.info(
            "Our robot ${robot.id}\n" +
                    "HP: ${robot.currentStatus.health}\n" +
                    "Levels: Health Level - ${robot.levels.healthLevel}\n" +
                    "        Damage Level - ${robot.levels.damageLevel}\n" +
                    "        Mining Speed Level - ${robot.levels.miningSpeedLevel}\n" +
                    "        Mining Level - ${robot.levels.miningLevel}\n" +
                    "        Energy Level - ${robot.levels.energyLevel}\n" +
                    "        Energy Regen Level - ${robot.levels.energyRegenLevel}\n" +
                    "        Storage Level - ${robot.levels.storageLevel}\n" +
                    "is trying to attack\n" +
                    "${enemyRobot.robotId}\n" +
                    "HP: ${enemyRobot.health}\n" +
                    "Levels: Health Level - ${enemyRobot.levels.healthLevel}\n" +
                    "        Damage Level - ${enemyRobot.levels.damageLevel}\n" +
                    "        Mining Speed Level - ${enemyRobot.levels.miningSpeedLevel}\n" +
                    "        Mining Level - ${enemyRobot.levels.miningLevel}\n" +
                    "        Energy Level - ${enemyRobot.levels.energyLevel}\n" +
                    "        Energy Regen Level - ${enemyRobot.levels.energyRegenLevel}\n" +
                    "        Storage Level - ${enemyRobot.levels.storageLevel}"
        )
    }

    private suspend fun getClosestPlanetWithBeatableEnemy(
        currentPlanet: DiscoveredPlanet
    ): Planet? {
        var enemyRobotsInCluster = RobotService.getAllEnemyRobotsInClusterByPlanetId(currentPlanet.clusterId)

        if (enemyRobotsInCluster.isEmpty()) return null
        //Applying additional filter criteria for enemy robots.
        for (filter in filterForEnemyRobots) {
            val enemyRobotsBefore = enemyRobotsInCluster
            enemyRobotsInCluster = filter(enemyRobotsInCluster)
            if (enemyRobotsInCluster.isEmpty())
                enemyRobotsInCluster = enemyRobotsBefore
        }
        val closestEnemy = getClosestEnemyFromListOfEnemies(enemyRobotsInCluster)
        return PlanetService.getPlanetById(UUID.fromString(closestEnemy.planetId.toString())).getOrThrow()
    }

    private suspend fun chooseTarget(): EnemyRobot {
        val currentPlanet = robot.currentPlanet as DiscoveredPlanet
        val enemyRobotsOnCurrentPlanet = RobotService.getAllEnemyRobotsInClusterByPlanetId(currentPlanet.id)
            .filter { it.planetId.toString() == currentPlanet.id.toString() && it.health != 0 }
        val possibleEnemiesToAttack = getMostExpensiveLowHpEnemiesFromListOfEnemies(enemyRobotsOnCurrentPlanet)
        return possibleEnemiesToAttack.first()
    }

    private fun getMostExpensiveLowHpEnemiesFromListOfEnemies(enemyRobots: List<EnemyRobot>): List<EnemyRobot> {
        val enemiesWithHighestLevelSum =
            enemyRobots.filter { it.levels.sumOfLevels() == enemyRobots.maxBy { it.levels.sumOfLevels() }.levels.sumOfLevels() }
        return enemiesWithHighestLevelSum.filter { it.health == enemiesWithHighestLevelSum.minBy { it.health }.health }
    }

    suspend fun getClosestEnemyFromListOfEnemies(enemyRobots: List<EnemyRobot>): EnemyRobot {
        val currentPlanet = robot.currentPlanet as DiscoveredPlanet
        val closestEnemy = enemyRobots.minBy {
            PlanetService.distanceFromTo(
                currentPlanet,
                PlanetService.getPlanetById(UUID.fromString(it.planetId.toString())).getOrNull()!!
            ) ?: Int.MAX_VALUE
        }
        return closestEnemy
    }


}
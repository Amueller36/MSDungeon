package com.example.thelegend27.robot.domain.strategies.commonstrategies

import com.example.thelegend27.planet.domain.Planet
import com.example.thelegend27.robot.application.RobotService
import com.example.thelegend27.robot.domain.EnemyRobot
import com.example.thelegend27.robot.domain.Robot
import com.example.thelegend27.robot.domain.RobotRepository
import com.example.thelegend27.robot.domain.commands.Command
import com.example.thelegend27.robot.domain.commands.CommandFactory
import com.example.thelegend27.robot.domain.strategies.RobotStrategy
import com.example.thelegend27.trading.domain.Upgrade
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import org.slf4j.LoggerFactory
import java.util.*

class AttackLowestEnemyOnPlanetIfBeatableOrFlee(private var robot: Robot) : RobotStrategy {
    private val logger = LoggerFactory.getLogger(AttackLowestEnemyOnPlanetIfBeatableOrFlee::class.java)

    @OptIn(ExperimentalCoroutinesApi::class)
    private val robotFlow = RobotRepository.asFlow().map { it[robot.id] }
    override suspend fun getCommand(): Command? {
        robot = robotFlow.first() ?: return CommandFactory.createRegenerateCommand(robot.id.toString())
        if (RobotService.isEnemyOnPlanetWithId(robot.currentPlanet.id)) {
            if (canWeBeatEnemyRobotsOnPlanet(robot.currentPlanet)) {
                return attackLowestEnemyOnPlanet()
            }
            return fleeToNeighbourPlanetWithLeastEnemies()
        }
        return null
    }


    private fun canWeBeatEnemyRobotsOnPlanet(currentPlanet: Planet): Boolean {
        val ourRobots = RobotService.getAllRobotsOnPlanet(currentPlanet.id)
        val ourFightingScore = ourRobots.sumOf { it.calculateFightingScore() }
        
        val enemyRobots = RobotService.getAllEnemiesOnPlanet(currentPlanet.id)
        val enemyFightingScore = enemyRobots.sumOf { it.calculateFightingScore() }

        return ourFightingScore >= enemyFightingScore
    }

    private fun getLowestEnemyRobotOnPlanet(currentPlanet: Planet): EnemyRobot {
        val enemyRobots = RobotService.getAllEnemiesOnPlanet(currentPlanet.id)
        return enemyRobots.minByOrNull { it.health != 0 }!!
    }

    private fun attackLowestEnemyOnPlanet(): Command {
        logger.info("Enemy detected on Planet ${robot.currentPlanet.id}. We are strong enough to beat them!")
        val enemyRobotToAttack = getLowestEnemyRobotOnPlanet(robot.currentPlanet)
        val damageWeDeal = Upgrade.DAMAGE.getValue(robot.levels.damageLevel)
        enemyRobotToAttack.decreaseHealthBy(damageWeDeal)
        return CommandFactory.createAttackCommand(robot.id.toString(), enemyRobotToAttack.robotId.toString())
    }

    private fun fleeToNeighbourPlanetWithLeastEnemies(): Command {
        val planetId = getPlanetIdWithLowestAmountOfEnemyRobots()
        return CommandFactory.createRobotMoveCommand(robot.id.toString(), planetId.toString())
    }

    private fun getPlanetIdWithLowestAmountOfEnemyRobots(): UUID {
        val neighbours = robot.currentPlanet.neighbours
        val planetWithLowestAmountOfEnemyRobots =
            neighbours.minBy { (_, planet) -> RobotService.getAllEnemiesOnPlanet(planet.id).size }
        return planetWithLowestAmountOfEnemyRobots.value.id
    }
}
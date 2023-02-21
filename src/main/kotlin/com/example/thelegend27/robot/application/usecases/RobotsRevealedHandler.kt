package com.example.thelegend27.robot.application.usecases

import com.example.thelegend27.eventinfrastructure.robot.integrationevents.RobotsRevealedIntegrationEvent
import com.example.thelegend27.robot.domain.EnemyRobot
import com.example.thelegend27.robot.domain.EnemyRobotRepository
import com.example.thelegend27.robot.domain.RobotRepository
import java.util.*

class RobotsRevealedHandler {

    suspend fun handle(event: RobotsRevealedIntegrationEvent) {
        val enemyRobots: List<EnemyRobot> =
            event.robots.map { it.toEnemyRobot() }.filter { robot ->
                !RobotRepository.containsKey(UUID.fromString(robot.robotId.toString()))
            }

        enemyRobots.forEach { robot -> EnemyRobotRepository.addOrReplace(robot) }

        val robotsToDelete = EnemyRobotRepository.getAll().filter { robot ->
            !enemyRobots.any { it.robotId == robot.robotId }
        }
        robotsToDelete.forEach {
            EnemyRobotRepository.removeElement(it)
            logKilledRobot(it)
        }

    }

    private fun logKilledRobot(killedRobot: EnemyRobot) {
        println(
            """
            |---------------------------------------------------------|
            |Robot ${killedRobot.robotId} got killed!|
            |---------------------------------------------------------|
        """.trimIndent()
        )
    }
}
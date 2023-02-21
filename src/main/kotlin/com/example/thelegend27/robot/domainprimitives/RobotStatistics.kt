package com.example.thelegend27.robot.domainprimitives

import com.example.thelegend27.robot.domain.EnemyRobotRepository
import com.example.thelegend27.robot.domain.RobotRepository
import com.example.thelegend27.robot.domain.strategies.ExploreStrategy
import com.example.thelegend27.robot.domain.strategies.farming.FarmStrategy
import com.example.thelegend27.robot.domain.strategies.fighting.FightStrategy

data class RobotStatistics(
    val totalNumberOfFriendlyRobots : Int,
    val totalNumberOfEnemyRobots : Int,
    val totalNumberOfFarmers: Int,
    val totalNumberOfExplorers: Int,
    val totalNumberOfFighters: Int
)
{
   fun appendTotalNumberOfFriendlyRobots(amount : Int) : RobotStatistics{
       return RobotStatistics(
           totalNumberOfFriendlyRobots = amount,
           totalNumberOfEnemyRobots,
           totalNumberOfFarmers ,
           totalNumberOfExplorers ,
           totalNumberOfFighters)
   }
    fun appendTotalNumberOfEnemyRobots(amount : Int) : RobotStatistics{
        return RobotStatistics(
            totalNumberOfFriendlyRobots,
            totalNumberOfEnemyRobots=amount,
            totalNumberOfFarmers ,
            totalNumberOfExplorers ,
            totalNumberOfFighters)
    }
    fun appendTotalNumberOfFarmers(amount : Int) : RobotStatistics{
        return RobotStatistics(
            totalNumberOfFriendlyRobots,
            totalNumberOfEnemyRobots,
            totalNumberOfFarmers= amount ,
            totalNumberOfExplorers ,
            totalNumberOfFighters)
    }
    fun appendTotalNumberOfExplorers(amount : Int) : RobotStatistics{
        return RobotStatistics(
            totalNumberOfFriendlyRobots,
            totalNumberOfEnemyRobots,
            totalNumberOfFarmers ,
            totalNumberOfExplorers = amount ,
            totalNumberOfFighters)
    }
    fun appendTotalNumberOfFighters(amount : Int) : RobotStatistics{
        return RobotStatistics(
            totalNumberOfFriendlyRobots,
            totalNumberOfEnemyRobots,
            totalNumberOfFarmers ,
            totalNumberOfExplorers ,
            totalNumberOfFighters = amount)
    }

    companion object {
        fun fromNothing() : RobotStatistics {
            return RobotStatistics(
                totalNumberOfFriendlyRobots = 0,
                totalNumberOfEnemyRobots = 0,
                totalNumberOfFarmers = 0,
                totalNumberOfExplorers = 0,
                totalNumberOfFighters = 0)
        }
    }

    fun getRobotStatistics(): RobotStatistics {
        val robots = RobotRepository.getAll()
        val enemyRobots = EnemyRobotRepository.getAll()
        return fromNothing().appendTotalNumberOfFriendlyRobots(robots.size)
            .appendTotalNumberOfEnemyRobots(enemyRobots.size)
            .appendTotalNumberOfFarmers(robots.count { it.strategy is FarmStrategy })
            .appendTotalNumberOfExplorers(robots.count { it.strategy is ExploreStrategy })
            .appendTotalNumberOfFighters(robots.count { it.strategy is FightStrategy })
    }
}

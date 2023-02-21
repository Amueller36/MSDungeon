package com.example.thelegend27.robot.domain

import com.example.thelegend27.utility.Repository
import com.example.thelegend27.utility.throwables.EntryDoesNotExistException
import java.util.*
import java.util.concurrent.ConcurrentHashMap

object EnemyRobotRepository : Repository<EnemyRobot> {

    private val dataStorage = ConcurrentHashMap<UUID, EnemyRobot>()
    override val elements: Map<UUID, EnemyRobot>
        get() = dataStorage.toMap()

    val amountEnemyRobots: Int
        get() = this.elements.size

    override fun getAll(): List<EnemyRobot> {

        return elements.values.toList()
    }

    override fun get(id: UUID): Result<EnemyRobot> {
        return if (containsKey(id))
            Result.success(dataStorage[id]!!)
        else Result.failure(EntryDoesNotExistException("EnemyRobot With ID : $id does not exist"))
    }

    override fun clear() {
        dataStorage.clear()
    }

    override fun getSize(): Int {
        return dataStorage.size
    }

    override fun containsKey(key: UUID): Boolean {
        return dataStorage.containsKey(key)
    }

    override fun contains(element: EnemyRobot): Boolean {
        return containsKey(element.robotId)
    }

    override fun addOrReplace(element: EnemyRobot): EnemyRobot {
        dataStorage[element.robotId] = element
        return dataStorage[element.robotId]!!
    }

    override fun replace(element: EnemyRobot): Result<EnemyRobot> {
        val robotId = element.robotId
        return if (containsKey(robotId)) {
            dataStorage[robotId] = element
            Result.success(dataStorage[robotId]!!)
        } else Result.failure(EntryDoesNotExistException("EnemyRobot with Id: $robotId does not exist"))
    }

    override fun add(element: EnemyRobot): Result<EnemyRobot> {
        val robotId = element.robotId
        return if (elements.containsKey(robotId)) {
            dataStorage[robotId] = element
            Result.success(element)
        } else Result.failure(EntryDoesNotExistException("EnemyRobot with id $robotId already exists!"))
    }

    override fun removeElement(element: EnemyRobot): Result<EnemyRobot> {
        val data = dataStorage.remove(element.robotId)
        return if (data == null) Result.failure(EntryDoesNotExistException(""))
        else Result.success(data)
    }


}
package com.example.thelegend27.robot.domain

import com.example.thelegend27.utility.Repository
import com.example.thelegend27.utility.throwables.EntryDoesNotExistException
import com.example.thelegend27.utility.throwables.EntryExistsExeption
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import java.util.*
import java.util.concurrent.ConcurrentHashMap

object RobotRepository : Repository<Robot> {

    private val dataStorage = ConcurrentHashMap<UUID, Robot>()

    private val robotFlow = MutableStateFlow<Map<UUID, Robot>>(dataStorage.toMap())


    @ExperimentalCoroutinesApi
    fun asFlow(): Flow<Map<UUID, Robot>> = robotFlow
    override val elements: Map<UUID, Robot>
        get() {
            return dataStorage.toMap()
        }


    override fun getAll(): List<Robot> {
        return elements.values.toList()
    }

    override fun get(id: UUID): Result<Robot> {
        return if (elements.containsKey(id)) {
            Result.success(elements[id]!!)
        } else Result.failure(EntryDoesNotExistException("Robot with id $id not found!"))
    }

    override fun clear() {
        dataStorage.clear()
        robotFlow.value = dataStorage.toMap()
    }

    override fun getSize(): Int {
        return dataStorage.size
    }

    override fun containsKey(key: UUID): Boolean {
        return elements.containsKey(key)
    }

    override fun contains(element: Robot): Boolean {
        return elements.containsValue(element)
    }

    override fun addOrReplace(element: Robot): Robot {
        dataStorage[element.id] = element
        robotFlow.value = dataStorage.toMap()
        return dataStorage[element.id]!!
    }

    override fun replace(element: Robot): Result<Robot> {
        return if (containsKey(element.id)) {
            dataStorage[element.id] = element
            robotFlow.value = dataStorage.toMap()
            Result.success(dataStorage[element.id]!!)
        } else Result.failure(EntryDoesNotExistException("Entry with Id: ${element.id} does not exist"))
    }

    override fun add(element: Robot): Result<Robot> {
        return if (!elements.containsKey(element.id)) {
            dataStorage[element.id] = element
            robotFlow.value = dataStorage.toMap()
            Result.success(dataStorage[element.id]!!)
        } else Result.failure(EntryExistsExeption("Robot with id ${element.id} already exists!"))
    }

    override fun removeElement(element: Robot): Result<Robot> {
        val data = dataStorage.remove(element.id)
        return if (data == null) Result.failure(EntryDoesNotExistException(""))
        else {
            robotFlow.value = dataStorage.toMap()
            Result.success(data)
        }
    }
}
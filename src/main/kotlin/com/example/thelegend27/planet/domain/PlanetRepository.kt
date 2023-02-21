package com.example.thelegend27.planet.domain

import com.example.thelegend27.utility.Repository
import com.example.thelegend27.utility.throwables.EntryDoesNotExistException
import com.example.thelegend27.utility.throwables.EntryExistsExeption
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import java.util.*
import java.util.concurrent.ConcurrentHashMap

object PlanetRepository : Repository<Planet> {
    private val dataStorage = ConcurrentHashMap<UUID, Planet>()
    override val elements: Map<UUID, Planet>
        get() {
            return dataStorage.toMap()
        }

    private val planetFlow = MutableStateFlow<Map<UUID, Planet>>(dataStorage.toMap())

    @ExperimentalCoroutinesApi
    fun asFlow(): Flow<Map<UUID, Planet>> = planetFlow
    override fun getAll(): List<Planet> {
        return elements.values.toList()
    }


    override fun get(id: UUID): Result<Planet> {
        return if (elements.containsKey(id)) {
            Result.success(elements[id]!!)
        } else Result.failure(EntryDoesNotExistException("Planet with id ${id} not found!"))
    }

    override fun clear() {
        dataStorage.clear()
        planetFlow.value = dataStorage.toMap()
    }

    override fun getSize(): Int {
        return dataStorage.size
    }


    override fun containsKey(key: UUID): Boolean {
        return elements.containsKey(key)
    }

    override fun contains(element: Planet): Boolean {
        return elements.containsValue(element)
    }

    override fun removeElement(element: Planet): Result<Planet> {
        val data = dataStorage.remove(element.id)
        return if (data == null) Result.failure(EntryDoesNotExistException("Planet with id ${element.id} not found!"))
        else {
            planetFlow.value = dataStorage.toMap()
            Result.success(data)
        }
    }

    override fun add(element: Planet): Result<Planet> {
        return if (!elements.containsKey(element.id)) {
            dataStorage[element.id] = element
            planetFlow.value = dataStorage.toMap()
            Result.success(element)
        } else Result.failure(EntryExistsExeption("Planet with id ${element.id} already exists!"))
    }

    override fun replace(element: Planet): Result<Planet> {
        return if (containsKey(element.id)) {
            dataStorage[element.id] = element
            planetFlow.value = dataStorage.toMap()
            Result.success(dataStorage[element.id]!!)
        } else Result.failure(EntryDoesNotExistException("Entry with Id: ${element.id} does not exist"))
    }

    override fun addOrReplace(element: Planet): Planet {
        dataStorage[element.id] = element
        planetFlow.value = dataStorage.toMap()
        return dataStorage[element.id]!!
    }

}
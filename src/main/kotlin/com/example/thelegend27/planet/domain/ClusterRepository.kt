package com.example.thelegend27.planet.domain

import com.example.thelegend27.utility.Repository
import com.example.thelegend27.utility.throwables.EntryDoesNotExistException
import com.example.thelegend27.utility.throwables.EntryExistsExeption
import java.util.*

object ClusterRepository : Repository<Cluster> {
    private val dataStorage : MutableMap<UUID, Cluster> = mutableMapOf()
    override val elements: Map<UUID, Cluster>
        get() = dataStorage.toMap()

    override fun getAll(): List<Cluster> {
        return elements.values.toList()
    }

    override fun get(id: UUID): Result<Cluster> {
        return if (containsKey(id))
            Result.success(dataStorage[id]!!)
        else Result.failure(EntryDoesNotExistException("Entry With ID : $id does not exist"))
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

    override fun contains(element: Cluster): Boolean {
        return containsKey(element.id)
    }

    override fun addOrReplace(element: Cluster): Cluster {
        dataStorage[element.id] = element
        return dataStorage[element.id]!!
    }

    override fun replace(element: Cluster): Result<Cluster> {
        return if(containsKey(element.id)) {
            dataStorage[element.id] = element
            Result.success(dataStorage[element.id]!!)
        } else Result.failure(EntryDoesNotExistException("Entry with Id: ${element.id} does not exist"))
    }

    override fun add(element: Cluster): Result<Cluster> {
        return if (!dataStorage.containsKey(element.id)) {

            dataStorage[element.id] = element
            Result.success(element)
        } else Result.failure(EntryExistsExeption("Planet with id ${element.id} already exists!"))
    }

    override fun removeElement(element: Cluster): Result<Cluster> {
        val data = dataStorage.remove(element.id)
        return if (data == null) Result.failure(EntryDoesNotExistException(""))
        else Result.success(data)
    }
}
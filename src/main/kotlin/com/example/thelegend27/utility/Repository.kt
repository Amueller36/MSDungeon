package com.example.thelegend27.utility

import java.util.*

interface Repository<T> {
    val elements: Map<UUID, T>
    fun getAll(): List<T>
    fun get(id: UUID): Result<T>
    fun clear()
    fun getSize(): Int
    fun removeElement(element: T) : Result<T>
    fun add(element: T) : Result<T>
    fun replace(element: T) : Result<T>
    fun addOrReplace(element: T) : T
    fun contains(element: T) : Boolean
    fun containsKey(key: UUID) : Boolean

}
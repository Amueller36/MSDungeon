package com.example.thelegend27.planet.domainprimitives

import com.example.thelegend27.planet.domain.Cluster
import java.util.UUID

data class ClusterStats private constructor(
    val id : UUID,
    val entrySize : Int?,
    val entries : List<UUID>
) {

    fun assignEntries(entries: List<UUID>) : ClusterStats{
        return ClusterStats(id,entries.size,entries)
    }

    companion object {
        fun fromNothing(id :UUID) : ClusterStats {
            return ClusterStats(id,0, emptyList())
        }
        fun empty(id : UUID) : ClusterStats {
            return ClusterStats(id,null, emptyList())
        }
    }
}

package com.example.thelegend27.planet.domainprimitives

import com.example.thelegend27.trading.domain.Resource

class DiscoveredDeposit  (
    override val resourceType : Resource,
    val maxAmount : Int = 0,
    var currentAmount : Int = 0,
) : ResourceDeposit {
}
package com.example.thelegend27.planet.domainprimitives

import com.example.thelegend27.trading.domain.Resource

interface ResourceDeposit : Deposit {

    val resourceType : Resource
}
package com.example.thelegend27.eventinfrastructure

class Event<A>(
    val eventHeader: EventHeaderDto,
    val eventBody: A
)
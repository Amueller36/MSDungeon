package com.example.thelegend27.planet.domain

enum class Direction() {
    NORTH(),
    SOUTH(),
    EAST(),
    WEST(),
    NONE();



    val opposite : () -> Direction = {
        when(this){
            NORTH -> SOUTH
            SOUTH -> NORTH
            EAST -> WEST
            WEST -> EAST
            else -> NONE
        }
    }
    companion object{
    val fromString: (str : String) -> Direction = { str ->
        when(str.lowercase()) {
            "north" -> NORTH
            "south" -> SOUTH
            "east" -> EAST
            "west" -> WEST
            else -> {
                NONE
            }
        }
    }}
}


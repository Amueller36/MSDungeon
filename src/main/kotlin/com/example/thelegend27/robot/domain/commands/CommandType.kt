package com.example.thelegend27.robot.domain.commands

enum class CommandType {
    MOVEMENT,
    BATTLE,
    MINING,
    REGENERATE,
    BUYING,
    SELLING;

    override fun toString(): String {
        return super.toString().lowercase()
    }
}
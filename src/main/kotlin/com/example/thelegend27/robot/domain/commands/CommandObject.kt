package com.example.thelegend27.robot.domain.commands

data class CommandObject private constructor(
    val commandType: String,
    val planetId: String = "",
    val targetId: String = "",
    val itemName: String = "",
    val itemQuantity: Int = 0
) {
    companion object {
        fun createMovementCommandObject(planetId: String): CommandObject {
            return CommandObject(
                commandType = CommandType.MOVEMENT.toString(),
                planetId = planetId
            )
        }

        fun createMiningCommandObject(planetId: String): CommandObject {
            return CommandObject(
                commandType = CommandType.MINING.toString(),
                planetId = planetId
            )
        }

        fun createSellingCommandObject(): CommandObject {
            return CommandObject(
                commandType = CommandType.SELLING.toString()
            )
        }

        fun createBuyingCommandObject(itemName: String, itemQuantity: Int): CommandObject {
            return CommandObject(
                commandType = CommandType.BUYING.toString(),
                itemName = itemName,
                itemQuantity = itemQuantity
            )
        }

        fun createBattleCommandObject(targetId: String): CommandObject {
            return CommandObject(
                commandType = CommandType.BATTLE.toString(),
                targetId = targetId
            )
        }

        fun createRegenerateCommandObject(): CommandObject {
            return CommandObject(
                commandType = CommandType.REGENERATE.toString()
            )
        }
    }
}
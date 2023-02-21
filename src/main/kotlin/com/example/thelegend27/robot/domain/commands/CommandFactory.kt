package com.example.thelegend27.robot.domain.commands

import com.example.thelegend27.trading.domain.Item
import com.example.thelegend27.trading.domain.Restoration
import com.example.thelegend27.trading.domain.Upgrade

object CommandFactory {

    fun createBuyRobotCommand(amount: Int): Command {
        if (amount <= 0) throw IllegalArgumentException("Amount must be greater than 0")
        val itemName = Item.ROBOT.toString()
        val commandObject =
            CommandObject.createBuyingCommandObject(itemName = itemName, itemQuantity = amount)
        return Command(
            commandType = CommandType.BUYING.toString(),
            commandObject = commandObject
        )
    }

    fun createUpgradeCommand(robotId: String, upgrade: Upgrade, level: Int): Command {
        if (level <= 0 || level > 5) throw IllegalArgumentException("Level must be greater than 0 and less than 6")
        val itemName = upgrade.toString() + "_" + level.toString()
        val commandObject = CommandObject.createBuyingCommandObject(itemName = itemName, itemQuantity = 1)
        return Command(
            commandType = CommandType.BUYING.toString(),
            commandObject = commandObject,
            robotId = robotId
        )

    }

    fun createRobotMoveCommand(robotId: String, destinationPlanetId: String): Command {
        val commandType = CommandType.MOVEMENT.toString()
        val commandObject = CommandObject.createMovementCommandObject(planetId = destinationPlanetId)
        return Command(
            robotId = robotId,
            commandType = commandType,
            commandObject = commandObject
        )
    }

    fun createMineCommand(robotId: String, planetId: String): Command {
        val commandObject = CommandObject.createMiningCommandObject(planetId = planetId)
        return Command(
            robotId = robotId,
            commandType = CommandType.MINING.toString(),
            commandObject = commandObject
        )
    }

    fun createSellInventoryCommand(robotId: String): Command {
        val commandObject = CommandObject.createSellingCommandObject()
        return Command(
            robotId = robotId,
            commandType = CommandType.SELLING.toString(),
            commandObject = commandObject
        )
    }

    fun createRegenerateCommand(robotId: String): Command {
        val commandObject = CommandObject.createRegenerateCommandObject()
        return Command(
            robotId = robotId,
            commandType = CommandType.REGENERATE.toString(),
            commandObject = commandObject
        )
    }

    fun createBuyEnergyRestoreCommand(robotId: String): Command {
        val commandObject =
            CommandObject.createBuyingCommandObject(itemName = Restoration.ENERGY_RESTORE.toString(), itemQuantity = 1)
        return Command(
            commandType = CommandType.BUYING.toString(),
            commandObject = commandObject,
            robotId = robotId
        )

    }

    fun createRobotRegenerateEnergyCommand(robotId: String): Command {
        val commandObject = CommandObject.createRegenerateCommandObject()
        return Command(
            robotId = robotId,
            commandType = CommandType.REGENERATE.toString(),
            commandObject = commandObject
        )
    }

    fun createAttackCommand(robotId: String, enemyId: String): Command {
        val commandObject = CommandObject.createBattleCommandObject(targetId = enemyId)
        return Command(
            robotId = robotId,
            commandType = CommandType.BATTLE.toString(),
            commandObject = commandObject
        )
    }

    fun createBuyHealthRestoreCommand(robotId: String): Command {
        val commandObject =
            CommandObject.createBuyingCommandObject(itemName = Restoration.HEALTH_RESTORE.toString(), itemQuantity = 1)
        return Command(
            commandType = CommandType.BUYING.toString(),
            commandObject = commandObject,
            robotId = robotId
        )
    }


}

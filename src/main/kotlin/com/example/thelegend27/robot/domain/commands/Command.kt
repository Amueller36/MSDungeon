package com.example.thelegend27.robot.domain.commands

import com.example.thelegend27.game.application.GameClient
import com.example.thelegend27.player.domain.Player


data class Command(
    val gameId: String = GameClient.getGameIdOfCreatedOrStartedGame(),
    val playerId: String = Player.playerId,
    val robotId: String = "",
    val commandType: String,
    val commandObject: CommandObject
)
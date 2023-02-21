package com.example.thelegend27.game.application


import com.example.thelegend27.eventinfrastructure.game.PlayerInfoDto
import com.example.thelegend27.eventinfrastructure.game.RegisteredPlayerDto
import com.example.thelegend27.game.domain.GameInfoDTO
import com.example.thelegend27.game.throwables.*
import com.example.thelegend27.player.domain.Player
import com.example.thelegend27.player.domain.PlayerQueue
import com.example.thelegend27.robot.domain.commands.Command
import com.example.thelegend27.robot.domain.commands.CommandFactory
import com.google.gson.Gson
import com.google.gson.JsonParser
import com.google.gson.JsonSyntaxException
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.gson.*
import kotlinx.coroutines.*
import org.slf4j.LoggerFactory
import java.util.*


object GameClient {
    private val gameServiceHost: String = System.getenv("GAME_HOST") ?: "http://localhost:8080"
    private val client = createHttpClient()
    private val logger = LoggerFactory.getLogger(GameClient.javaClass.name)

    private fun getPlayerId(name: String = Player.name, email: String = Player.email): String {
        return runBlocking {
            client.request("$gameServiceHost/players?name=$name&mail=$email") {
                method = HttpMethod.Get
                contentType(ContentType.Application.Json)
            }.let { response ->
                if (response.status != HttpStatusCode.OK) throw PlayerNotFoundException("Player with $name and $email not found")
                val playerId = Gson().fromJson(response.bodyAsText(), PlayerInfoDto::class.java).playerId
                playerId
            }
        }
    }

    fun createGame() {
        runBlocking(Dispatchers.IO) {
            client.request("$gameServiceHost/games") {
                method = HttpMethod.Post
                contentType(ContentType.Application.Json)
                setBody("{\"maxRounds\": 500, \"maxPlayers\": 6}")
            }.let { response ->
                if (response.status != HttpStatusCode.Created) throw GameCouldNotBeCreatedException("Error while creating game. Status :${response.status}")
            }
        }
    }

    /**
    Patches the Gametime, duration in ms!
     */
    fun patchGameTime(duration: Long, gameId: String = getGameIdOfCreatedOrStartedGame()) {
        runBlocking(Dispatchers.IO) {
            client.request("$gameServiceHost/games/$gameId/duration") {
                method = HttpMethod.Patch
                contentType(ContentType.Application.Json)
                setBody("{\"duration\": $duration}")
            }.status.let { status ->
                if (status != HttpStatusCode.OK) {
                    throw GameCouldNotBePatchedException("Error while trying to patch game time. Status :$status")
                }
            }
        }
    }

    fun registerPlayer(playerName: String = Player.name, playerMail: String = Player.email) {
        try {
            //If we get a playerId, there is no need for registration
            getPlayerId()
        } catch (e: PlayerNotFoundException) {
            logger.info("Player does not exist, registering player")
            runBlocking(Dispatchers.IO) {

                client.request("$gameServiceHost/players") {
                    method = HttpMethod.Post
                    contentType(ContentType.Application.Json)
                    setBody("{\"name\": \"$playerName\", \"email\": \"$playerMail\"}")

                }.let { response ->
                    if (response.status != HttpStatusCode.Created) {
                        throw Exception("There was an error trying to register a new player! ${response.status.value}")
                    }
                    Player.playerId = getPlayerId()
                    logger.info("Player $playerName registered!: ${response.bodyAsText()}")
                }
            }
        }
        Player.playerId = getPlayerId()
    }

    private fun startGame(gameId: String = getGameIdOfCreatedOrStartedGame()) {
        runBlocking(Dispatchers.IO) {
            client.request("$gameServiceHost/games/$gameId/gameCommands/start") {
                method = HttpMethod.Post
                contentType(ContentType.Application.Json)
            }.status.let { status ->
                if (status != HttpStatusCode.Created) {
                    throw Exception("There was an error trying to start the game!")
                }
            }
        }


    }

    fun joinGame(gameId: String = getGameIdOfCreatedOrStartedGame(), playerId: String = Player.playerId) {
        logger.info("gameId is: $gameId and playerId is: $playerId")

        //Check if the gameId and playerId are valid UUIDs
        UUID.fromString(gameId)
        UUID.fromString(playerId)

        runBlocking(Dispatchers.IO) {
            client.request("$gameServiceHost/games/$gameId/players/$playerId") {
                method = HttpMethod.Put
                contentType(ContentType.Application.Json)
                setBody("{\"playerId\": \"$playerId\"}")
            }.let { response ->
                if (response.status != HttpStatusCode.OK) {
                    throw GameCouldNotBeJoinedException("There was an error trying to join the game! ${response.status}")
                }
                val playerInfos = Gson().fromJson(response.bodyAsText(), RegisteredPlayerDto::class.java)
                Player.playerQueue =
                    PlayerQueue(gameExchange = playerInfos.gameExchange, playerQueue = playerInfos.playerQueue)
            }
        }
    }

    /**
     * @return Returns the response containing a transactionId in success case.
     */
    suspend fun sendCommand(command: Command): HttpResponse {
        return client.request("$gameServiceHost/commands") {
            method = HttpMethod.Post
            contentType(ContentType.Application.Json)
            setBody(Gson().toJson(command))
            expectSuccess = true
        }
    }

    private suspend fun startGameIfNotRunning(gameId: String = getGameIdOfCreatedOrStartedGame()) {
        if (getGameInfo().find { it.gameId == gameId }?.gameStatus?.contains("created", true) == true) {
            startGame(gameId)
            delay(5000)
        }
    }

    fun deleteGame(gameId: String = getGameIdOfCreatedOrStartedGame()) {
        runBlocking(Dispatchers.IO) {
            startGameIfNotRunning(gameId)
            endGame(gameId)
            try {
                getGameInfoForId(gameId)
                logger.info("The game was not ended successfully!")
            } catch (e: GameCouldNotBeFoundException) {
                logger.info("The game was ended successfully!")
            }

        }
    }

    private fun getGameInfoForId(gameId: String): GameInfoDTO {
        val gameData = getGameInfo()
        return gameData.firstOrNull { it.gameId == gameId }
            ?: throw GameCouldNotBeFoundException("Game with id $gameId could not be found!")
    }

    private suspend fun endGame(gameId: String) {
        val response = client.post("$gameServiceHost/games/${gameId}/gameCommands/end") {
            contentType(ContentType.Application.Json)
            setBody("")
        }
        if (response.status != HttpStatusCode.Created) {
            throw GameCouldNotBeEndedException("There was an error trying to end the game! Response code : ${response.status}")
        }
    }


    fun getGameInfo(): List<GameInfoDTO> {
        return runBlocking(Dispatchers.IO) {
            val response = client.get("$gameServiceHost/games") {
                contentType(ContentType.Application.Json)
            }

            if (response.status != HttpStatusCode.OK) {
                throw Exception("There was an error trying to get the game info! HTTP status code: ${response.status}")
            }

            try {
                response.body()
            } catch (e: JsonSyntaxException) {
                throw Exception("Error parsing JSON response while trying to get game info! ${e.message}")
            } catch (e: Exception) {
                throw Exception("Error trying to get game info: ${e.message}")
            }
        }
    }

    fun getGameIdOfCreatedOrStartedGame(): String {
        val gameData: List<GameInfoDTO> = getGameInfo()
        val game = gameData.firstOrNull { gameinfo ->
            gameinfo.gameStatus.contains("created", true) || gameinfo.gameStatus.contains("started", true)
        } ?: throw NoGamesExistException()

        return game.gameId
    }

    fun buyXRobots(amount: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            val command = CommandFactory.createBuyRobotCommand(amount)
            val response = client.request(("${System.getenv()["GAME_HOST"]}/commands")) {
                method = HttpMethod.Post
                contentType(ContentType.Application.Json)
                setBody(command)
            }
            val transactionId = try {
                val jsonObject = JsonParser.parseString(response.body()).asJsonObject
                jsonObject.get("transactionId").asString
            } catch (e: Exception) {
                logger.error("Exception occured while trying to buy $amount of robot/s!")
            }
            logger.info("Sent Robot buying Request to Game Service. Transaction ID: $transactionId")

        }
    }


    fun changeMaxRoundsTo(gameId: String = getGameIdOfCreatedOrStartedGame(), maxRounds: String) {
        runBlocking(Dispatchers.IO) {
            client.request("$gameServiceHost/games/$gameId/maxRounds") {
                method = HttpMethod.Patch
                contentType(ContentType.Application.Json)
                setBody("{\"maxrounds\": $maxRounds}")
            }.status.let { status ->
                if (status != HttpStatusCode.OK) {
                    throw GameCouldNotBePatchedException("There was an error trying to change the max rounds!")
                }
            }
        }
    }

    private fun createHttpClient(): HttpClient {
        return HttpClient(CIO) {
            install(ContentNegotiation) {
                gson {
                    setPrettyPrinting()
                }
            }
            install(HttpTimeout) {
                requestTimeoutMillis = 15000
                socketTimeoutMillis = 20000
            }
            install(HttpRequestRetry) {
                retryOnServerErrors(maxRetries = 5)
            }
        }
    }

}





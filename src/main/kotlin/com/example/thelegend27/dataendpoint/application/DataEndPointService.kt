package com.example.thelegend27.dataendpoint.application


import com.example.thelegend27.planet.application.PlanetService
import com.example.thelegend27.planet.domainprimitives.GeneralStats
import com.example.thelegend27.robot.application.RobotService
import com.example.thelegend27.robot.domainprimitives.RobotStatistics
import io.ktor.http.*
import io.ktor.serialization.gson.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*


object DataEndPointService {
    @OptIn(DelicateCoroutinesApi::class)
    fun start() {
        GlobalScope.launch {
            embeddedServer(Netty, 8090, module = Application::myApplicationModule)
                .start(wait = true)
        }

    }
}

fun Application.dataEndpoint() {
    install(CORS) {
        allowMethod(HttpMethod.Options)
        allowMethod(HttpMethod.Put)
        allowMethod(HttpMethod.Delete)
        allowMethod(HttpMethod.Patch)
        allowHeader(HttpHeaders.Authorization)
        allowHeader(HttpHeaders.AccessControlAllowOrigin)
        anyHost()
        allowHost("client-host")
        allowHost("0.0.0.0:8090")
    }
    install(ContentNegotiation) {
        gson {
            setPrettyPrinting()
        }

    }
    routing {
        get("/") {
            call.respondText("Hello World")
        }
        get("clusters") {
            val data = PlanetService.getAllClusters()
                .mapNotNull { PlanetService.getClusterStats(it.id) }
            call.respond(data)
            //call.respondText("clusters Section")
        }
        get("clusters/{id}") {
            try {
                val id: UUID = UUID.fromString(call.parameters["id"])
                PlanetService.getClusterStats(id)
                    .onFailure {
                        call.respond(HttpStatusCode.NotFound)
                    }
                    .onSuccess {
                        call.respond(it)
                    }

            } catch (e: IllegalArgumentException) {
                call.respond(HttpStatusCode.BadRequest)
            }

        }

        get("planets") {
            //call.respondText { "planets section" }
            val data = PlanetService.getAllPlanets()
                .mapNotNull { PlanetService.getPlanetStats(it.id) }
            call.respond(data)
        }
        get("planets/{id}") {
            try {
                val id: UUID = UUID.fromString(call.parameters["id"])

                PlanetService.getPlanetStats(id)
                    .onFailure {
                        call.respond(HttpStatusCode.NotFound)
                    }
                    .onSuccess {
                        call.respond(it)
                    }

            } catch (e: IllegalArgumentException) {
                call.respond(HttpStatusCode.BadRequest)
            }
        }

        get("robots") {
            val data = RobotService.getAllRobotsAsFriendlyRobotEntryMinimal()
            call.respond(data)
        }
        get("robots/{id}") {
            try {
                val id: UUID = UUID.fromString(call.parameters["id"])

                RobotService.getDetailedFriendlyRobotEntryById(id)
                    .onFailure {
                        call.respond(HttpStatusCode.NotFound)
                    }
                    .onSuccess {
                        call.respond(it)
                    }

            } catch (e: IllegalArgumentException) {
                call.respond(HttpStatusCode.BadRequest)
            }
        }

        get("enemyrobots") {
            val data = RobotService.getAllEnemyRobotsAsEnemyRobotEntryMininmal()
            call.respond(data)
        }

        get("enemyrobots/{id}") {
            try {
                val id: UUID = UUID.fromString(call.parameters["id"])

                RobotService.getEnemyRobotEntryDetailedById(id)
                    .onFailure {
                        call.respond(HttpStatusCode.NotFound)
                    }
                    .onSuccess {
                        call.respond(it)
                    }

            } catch (e: IllegalArgumentException) {
                call.respond(HttpStatusCode.BadRequest)
            }
        }

        get("/stats") {

        }
        get("/stats/map") {
            val data: GeneralStats = PlanetService.generalStats()
            call.respond(data)
        }
        get("/stats/robots") {
            val data: RobotStatistics = RobotStatistics.fromNothing().getRobotStatistics()
            call.respond(data)
        }

    }
}

fun Application.myApplicationModule() {
    dataEndpoint()

}


package com.marketsidekick.cli.instrument.futures

import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureFutureEndpoints() {
    routing {
        staticResources("static", "static")
println("Got to endpoints")
        get("/hello"){
            println("We at hello")
            call.respond(
"Hello World!"
            )
        }
    }
}

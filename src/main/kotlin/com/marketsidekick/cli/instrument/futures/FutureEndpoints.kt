package com.marketsidekick.cli.instrument.futures

import com.marketsidekick.BrowserState
import com.microsoft.playwright.Playwright
import io.ktor.http.*
import io.ktor.serialization.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class LoginCredentials(
    val username: String,
    val password: String
)

fun Application.configureFutureEndpoints() {
    val playwright: Playwright = Playwright.create()
    install(ContentNegotiation) {
        json(Json {
            prettyPrint = true
            isLenient = true
        })
    }
    routing {
        staticResources("static", "static")
        println("Loading endpoints...")

        post("/login") {
            try {
                val credentials = call.receive<LoginCredentials>()
                BrowserState.page?.navigate("https://trader.tradovate.com/welcome")
                BrowserState.page?.locator("input[id=\"name-input\"]")?.fill(credentials.username)
                BrowserState.page?.locator("input[id=\"password-input\"]")?.fill(credentials.password)
                BrowserState.page?.getByText("Login")?.click()
                BrowserState.page?.getByText("Access Simulation")?.click()
            } catch (ex: IllegalStateException) {
                println("Illegal state exception")
                call.respond(HttpStatusCode.BadRequest)
            } catch (ex: JsonConvertException) {
                println("Json convert exception")
                call.respond(HttpStatusCode.BadRequest)
            } catch (ex: Exception) {
                println("Exception occurred")
            }
        }

        post("/order") {
            try {
                val futureOrder = call.receive<FutureOrder>()
                // Ensure the page is open before interacting with it
                if (BrowserState.page != null) {
                    // Here you can execute additional actions on the page
                    if (futureOrder.orderType == "buy") {
                        BrowserState.page?.getByText("Buy Mkt")?.click()
                        println("Buy at time ${futureOrder.time}")
                    }
                    if (futureOrder.orderType == "sell") {
                        println("Selling....")
                    }
                    if (futureOrder.orderType == "close") {
                        BrowserState.page?.locator(".module-dom .header .market-buttons-wrapper")?.getByText("Exit at Mkt & Cxl")?.click()
                        println("Close at time ${futureOrder.time}")
                    }
                } else {
                    call.respond("No active page session exists.")
                }
            } catch (ex: IllegalStateException) {
                call.respond("Illegal state exception: ${ex.message}")
            } catch (ex: JsonConvertException) {
                call.respond("Json convert exception: ${ex.message}")
            } catch (e: Exception) {
                call.respond("Exception occurred: ${e.message}")
            }
        }

        get("/close") {
            // Optionally, provide an endpoint to close the browser and page
            BrowserState.page?.close()
            BrowserState.browser?.close()
            BrowserState.page = null
            BrowserState.browser = null
            call.respond("Browser and page closed.")
        }
    }
}
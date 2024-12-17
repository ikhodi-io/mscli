package com.marketsidekick

import com.github.ajalt.clikt.command.SuspendingCliktCommand
import com.github.ajalt.clikt.command.main
import com.github.ajalt.clikt.parameters.options.help
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.prompt
import com.marketsidekick.cli.instrument.futures.LoginCredentials
import com.marketsidekick.cli.instrument.futures.configureFutureEndpoints
import com.microsoft.playwright.BrowserContext
import com.microsoft.playwright.BrowserType
import com.microsoft.playwright.Page
import com.microsoft.playwright.Playwright
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

object BrowserState {
    var isHeadless: Boolean = System.getenv("MSCLI_MODE") == "production"
    var browser: com.microsoft.playwright.Browser? = null
    var context: BrowserContext? = null
    var page: Page? = null
    val scheduler: ScheduledExecutorService = Executors.newScheduledThreadPool(1)

    init {
        try {
            // Initialize browser, context, and page
            browser = Playwright.create().chromium().launch(
                BrowserType.LaunchOptions().setChannel("chromium").setHeadless(isHeadless)
            )
            context = browser?.newContext()
            page = context?.newPage()
            println("Browser initialized successfully.")

            // Schedule a task to reload the page
            scheduler.scheduleAtFixedRate({
                try {
                    if (page != null) {
                        println("Refreshing the page to keep the session active...")
                        page!!.reload()
                    } else {
                        println("Page is not initialized. Skipping refresh.")
                    }
                } catch (e: Exception) {
                    println("Error while refreshing the page: ${e.message}")
                }
            }, 7, 7, TimeUnit.MINUTES)

            println("Browser is active. Close it manually to terminate.")
        } catch (e: Exception) {
            println("Error during browser setup: ${e.message}")
        }
    }
}

class MsCli : SuspendingCliktCommand() {
    // TODO: initialize BrowserState from command line
    private var username: String? = null
    private var password: String? = null
    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
            })
        }
    }

    override suspend fun run() {
        // Start ktor server in background
        GlobalScope.launch {
            println("Starting Ktor server...")
            embeddedServer(Netty, port = 3000, module = Application::configureFutureEndpoints).start(wait = false)
        }
        var name: String;
        while (true) {
            echo("What is your name >")
            name = readLine().toString()
            if (name == "exit") {
                break
            }
            if (name == "login") {
                echo("Enter username")
                username = readLine().toString()
                echo("Enter password")
                password = readLine().toString()

                // Send POST request to /login endpoint with the entered credentials
                try {
                    val response: HttpResponse = client.post("http://0.0.0.0:3000/login") {
                        contentType(ContentType.Application.Json)
                        setBody(LoginCredentials(username.toString(), password.toString()))
                    }
                    println("Response from login: $response")
                } catch (e: Exception) {
                    println("Error during login: ${e.localizedMessage}")
                }
            }

            if (name == "close"){
                try {
                    val response: HttpResponse = client.get("http://0.0.0.0:3000/close")
                    println("Response from close: $response")
                } catch (e: Exception) {
                    println("Error during close: ${e.localizedMessage}")
                }
            }
        }
    }
}

suspend fun main(args: Array<String>) = MsCli().main(args)

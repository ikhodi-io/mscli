package com.marketsidekick

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.main
import com.github.ajalt.clikt.parameters.options.*
import com.github.ajalt.clikt.parameters.types.int
import com.marketsidekick.cli.instrument.futures.configureFutureEndpoints
import com.microsoft.playwright.BrowserType
import com.microsoft.playwright.Page
import com.microsoft.playwright.Playwright
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class Hello : CliktCommand() {
//    private val count: Int by option().int().default(1).help("Number of greetings")
//    private val name: String by option().prompt("Your name").help("The person to greet")
    override fun run() {
        // Start ktor server in background
        GlobalScope.launch {
            println("Starting Ktor server...")
           embeddedServer(Netty, port = 8080,  module = Application::configureFutureEndpoints).start(wait = false)
        }
        var name: String;
        while(true) {
            echo("What is your name >")
            name = readln()
            if (name == "exit"){
                break
            }
            if (name == "play"){
               echo("running play")
                Playwright.create().use { playwright ->
                    val browser = playwright.chromium().launch(
                        BrowserType.LaunchOptions()
                    )
                   val page: Page = browser.newPage()
                   page.navigate("https://www.tradovate.com/")
                }
            }
            echo("Hello $name!")
        }
    }
}
fun main(args: Array<String>) {
    Hello().main(args)
}
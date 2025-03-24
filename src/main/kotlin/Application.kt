package com.meds

import com.meds.di.appModule
import com.meds.plugin.*
import io.ktor.server.application.*
import org.koin.ktor.plugin.Koin

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    install(Koin){
        modules(appModule)
    }
    configureJwtAuthentication()
    configureMonitoring()
    configureSerialization()
    configureRouting()
    configureValidation()
}

package com.meds.plugin

import com.meds.domain.repository.TaskImageRepository
import com.meds.domain.repository.TaskRepository
import com.meds.domain.repository.UserAuthRepository
import com.meds.routes.authRoutes
import com.meds.routes.taskImageRoutes
import com.meds.routes.taskRoutes
import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import java.io.File

fun Application.configureRouting() {
    val taskRepository by inject<TaskRepository>()
    val taskImgRepository by inject<TaskImageRepository>()
    val authRepository by inject<UserAuthRepository>()
    routing {
        taskRoutes(taskRepository)
        taskImageRoutes(taskImgRepository)
        authRoutes(authRepository)
        //staticFiles("/uploads", File("uploads"))
    }
}

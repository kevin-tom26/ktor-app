package com.meds.plugin

import com.meds.data.models.TaskModel
import io.ktor.server.application.*
import io.ktor.server.plugins.requestvalidation.*

fun Application.configureValidation() {
    install(RequestValidation){

        validate<TaskModel>{ body ->
            if(body.title.isNullOrEmpty()) ValidationResult.Invalid("Invalid task Name.")
            else if(body.body.isNullOrEmpty()) ValidationResult.Invalid("Invalid body.")
            else ValidationResult.Valid
        }
    }
}
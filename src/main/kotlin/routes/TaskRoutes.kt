package com.meds.routes

import com.meds.data.models.*
import com.meds.domain.repository.TaskRepository
import com.meds.services.TaskServices
import com.meds.services.uploadImg
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.serialization.*
import io.ktor.server.auth.*
import io.ktor.server.plugins.requestvalidation.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.cio.*
import io.ktor.utils.io.*
import kotlinx.serialization.json.Json
import java.io.File

fun Route.taskRoutes(taskRepository: TaskRepository){

    authenticate("jwt-auth"){
        get("/") {
            call.respondText("Hello World!")
        }

        get("/getTasks"){
            val enabled = call.queryParameters["enabled"]?.toBooleanStrictOrNull()
            val priorityText = call.queryParameters["priority"]

            val priority = priorityText?.let { priorityTxt ->
                enumValues<Priority>().find { it.name.equals(priorityTxt, ignoreCase = true) }
            }

            if(priorityText != null && priority == null){
                call.respond(HttpStatusCode.BadRequest, "Invalid Priority value.")
                return@get
            }
            val task = if(enabled != null && priority != null){
                taskRepository.getEnabledTaskWithPriority(enabled, priority)
            }else{
                taskRepository.getAllTasks()
            }
            call.respond(RestResponse(
                status = StatusResponse(200, "Success"),
                response = task
            ))
        }

        get("/getTaskByPriority/{priority}"){
            val priorityAsText = call.parameters["priority"]
            if(priorityAsText == null){
                call.respond(HttpStatusCode.BadRequest, "Priority Not passed!!")
                return@get
            }
            try {
                val priority = Priority.valueOf(priorityAsText)
                val taskList = taskRepository.getTaskByPriority(priority)

                if(taskList.isEmpty()){
                    call.respond(HttpStatusCode.NotFound)
                    return@get
                }
                call.respond(taskList)
            }catch (ex: IllegalArgumentException){
                call.respond(HttpStatusCode.BadRequest)
            }
        }

        get("/getTaskByName/{taskName}"){
            val taskName = call.parameters["taskName"]?.replace("%20", " ")
            println("Received taskName: $taskName")
            if(taskName == null){
                call.respond(HttpStatusCode.BadRequest, "taskName Not passed!!")
                return@get
            }
            val task = taskRepository.getTaskByTitle(taskName)

            if(task == null){
                call.respond(HttpStatusCode.NotFound)
                return@get
            }
            call.respond(task)
        }

        post("/task/add"){

            try {
                val task = call.receive<TaskModel>()
                call.respond(task)
                //call.respond(taskRepository.addTask(task))
            }catch (ex : IllegalStateException){
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Check request body"))
            }catch (ex: JsonConvertException){
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Json conversion error"))
            }catch (ex: RequestValidationException){
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to ex.reasons))
            }
        }

        delete("/task/delete/{id}"){
            val id = call.parameters["id"]
            if(id == null){
                call.respond(HttpStatusCode.BadRequest, "ID Not passed!!")
                return@delete
            }

            if(taskRepository.deleteTask(id)){
                call.respond(HttpStatusCode.NoContent)
            }else{
                call.respond(HttpStatusCode.NotFound)
            }
        }

//        post("/uploadImage"){
//            val multiPartFormData = call.receiveMultipart()
//
//            var fileName: String? = null
//            var task : TaskModel? = null
//            var file : File? = null
//            var imageBytes: ByteArray? = null
//
//            multiPartFormData.forEachPart { part->
//                when(part){
//                    is PartData.FormItem -> {
//                        if(part.name == "task"){
//                            try {
//                                task = Json.decodeFromString<TaskModel>(part.value)
//                            }catch (e: Exception){
//                                call.respond(HttpStatusCode.BadRequest, "Invalid task data format")
//                                return@forEachPart
//                            }
//                        }
//                    }
//                    is PartData.FileItem -> {
//                        if(part.name == "image"){
//                            fileName = part.originalFileName ?: return@forEachPart
////                            file = File("uploads/$fileName").apply { parentFile?.mkdirs() }
////                            part.provider().copyAndClose(file!!.writeChannel())
//                            imageBytes = part.provider().toByteArray()
//                        }
//                    }
//                    else -> {}
//                }
//                part.dispose()
//            }
//            if (fileName == null || task == null || imageBytes == null) {
//                call.respond(HttpStatusCode.BadRequest, "Missing image or task data")
//                return@post
//            }
//
//            // Upload to Cloudinary
//            val cloudinaryUrl = uploadImg(imageBytes!!, fileName!!)
//
//            val imageUrl = "http://127.0.0.1:8080/uploads/$fileName"
//
//            val savedTask = TaskImgModel(task = task!!, image = cloudinaryUrl)
//
//            call.respond(HttpStatusCode.OK, savedTask)
//        }
    }
}
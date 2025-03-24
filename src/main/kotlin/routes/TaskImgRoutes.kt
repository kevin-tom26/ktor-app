package com.meds.routes

import com.meds.data.models.*
import com.meds.domain.repository.TaskImageRepository
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

fun Route.taskImageRoutes(taskImageRepository: TaskImageRepository){

    authenticate("jwt-auth"){
        get("/getImageTasks"){
            val task = taskImageRepository.getAllImgTasks()
            call.respond(RestResponse(
                status = StatusResponse(200, "Success"),
                response = task
            ))
        }

        get("/getImgTaskByName/{taskName}"){
            val taskName = call.parameters["taskName"]?.replace("%20", " ")
            println("Received taskName: $taskName")
            if(taskName == null){
                call.respond(HttpStatusCode.BadRequest, "taskName Not passed!!")
                return@get
            }
            val task = taskImageRepository.getImgTaskByTitle(taskName)

            if(task == null){
                call.respond(HttpStatusCode.NotFound)
                return@get
            }
            call.respond(call.respond(RestResponse(
                status = StatusResponse(200, "Success"),
                response = task
            )))
        }

//        post("/task/add"){
//
//            try {
//                val task = call.receive<TaskModel>()
//                call.respond(task)
//                //call.respond(taskRepository.addTask(task))
//            }catch (ex : IllegalStateException){
//                call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Check request body"))
//            }catch (ex: JsonConvertException){
//                call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Json conversion error"))
//            }catch (ex: RequestValidationException){
//                call.respond(HttpStatusCode.BadRequest, mapOf("error" to ex.reasons))
//            }
//        }

        delete("/task/deleteImgTask/{id}"){
            val id = call.parameters["id"]
            if(id == null){
                call.respond(HttpStatusCode.BadRequest, "ID Not passed!!")
                return@delete
            }

            if(taskImageRepository.deleteImgTask(id)){
                call.respond(HttpStatusCode.OK)
            }else{
                call.respond(HttpStatusCode.NotFound)
            }
        }

        post("/uploadImage"){
            val multiPartFormData = call.receiveMultipart()

            var fileName: String? = null
            var task : TaskModel? = null
            var imageBytes: ByteArray? = null

            multiPartFormData.forEachPart { part->
                when(part){
                    is PartData.FormItem -> {
                        if(part.name == "task"){
                            try {
                                task = Json.decodeFromString<TaskModel>(part.value)
                            }catch (e: Exception){
                                call.respond(HttpStatusCode.BadRequest, "Invalid task data format")
                                return@forEachPart
                            }
                        }
                    }
                    is PartData.FileItem -> {
                        if(part.name == "image"){
                            fileName = part.originalFileName ?: return@forEachPart
//                            file = File("uploads/$fileName").apply { parentFile?.mkdirs() }
//                            part.provider().copyAndClose(file!!.writeChannel())
                            imageBytes = part.provider().toByteArray()
                        }
                    }
                    else -> {}
                }
                part.dispose()
            }
            if (fileName == null || task == null || imageBytes == null) {
                call.respond(HttpStatusCode.BadRequest, "Missing image or task data")
                return@post
            }

            // Upload to Cloudinary
            val cloudinaryUrl = uploadImg(imageBytes!!, fileName!!)

            //val imageUrl = "http://127.0.0.1:8080/uploads/$fileName"

            val savedTask = TaskImgModel(task = task!!, image = cloudinaryUrl)

            val imgTask = taskImageRepository.addImgTask(savedTask)

            call.respond(HttpStatusCode.OK, RestResponse(
                status = StatusResponse(200, "Success"),
                response = imgTask
            ))
        }
    }
}
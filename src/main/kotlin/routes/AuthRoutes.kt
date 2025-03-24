package com.meds.routes

import com.meds.data.models.RestResponse
import com.meds.data.models.StatusResponse
import com.meds.data.models.UserAuthModel
import com.meds.domain.repository.UserAuthRepository
import com.meds.plugin.JwtConfig
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.mindrot.jbcrypt.BCrypt

fun Route.authRoutes(authRepository: UserAuthRepository) {

    authenticate("basic-auth") {
        post("/signup") {
            val request = call.receiveParameters()
            val userName = request["userName"]
            val password = request["password"]
            val email = request["email"]

            if ((userName != null && authRepository.getUserByUserName(userName) != null) || (email != null && authRepository.getUserByEmail(
                    email
                ) != null)
            ) {
                call.respond(
                    message = RestResponse<String?>(
                        status = StatusResponse(statusCode = 404, message = "User already exists!"),
                        response = "User already exists!"
                    )
                )
                return@post
            }
            val hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt())
            val newUser =
                UserAuthModel(userName = userName, email = email, hashPassword = hashedPassword, refreshToken = "")
            call.respond(message = authRepository.addUser(newUser))
        }

        post("/login") {
            val request = call.receiveParameters()
            val userName = request["userName"]
            val password = request["password"]
            val email = request["email"]

            val user = when {
                userName != null -> authRepository.getUserByUserName(userName)
                email != null -> authRepository.getUserByEmail(email)
                else -> null
            }

            if (user == null || !BCrypt.checkpw(password, user.hashPassword)) {
                call.respond(
                    message = RestResponse<String?>(
                        status = StatusResponse(statusCode = 404, message = "Invalid credentials!"),
                        response = "Invalid credentials!"
                    )
                )
                return@post
            }
            val accessToken = JwtConfig.generateAccessToken(user)
            val refreshToken = JwtConfig.generateRefreshToken(user)
            authRepository.updateRefreshToken(user.id, refreshToken)

            val userResponse = authRepository.getUserById(user.id)

            if (userResponse.response != null) {
                val updatedUserResponse = userResponse.copy(
                    response = userResponse.response.copy(accessToken = accessToken)
                )
                call.respond(message = updatedUserResponse)
            } else {
                call.respond(message = userResponse)
            }
        }

        post("/refresh"){
            val request = call.receive<Map<String, String>>()
            val refreshToken = request["refreshToken"] ?: return@post call.respond("No refresh token provided")
            val userId = JwtConfig.verifyRefreshToken(refreshToken) ?: return@post call.respond("Invalid refresh token")

            val user = authRepository.getUserByIdForUpdate(userId) ?: return@post call.respond("User not found")

            val newAccessToken = JwtConfig.generateAccessToken(user)
            val newRefreshToken = JwtConfig.generateRefreshToken(user)
            authRepository.updateRefreshToken(user.id, newRefreshToken)

            val userResponse = authRepository.getUserById(user.id)

            if(userResponse.response != null){
                val updatedUserResponse = userResponse.copy(
                    response = userResponse.response.copy(accessToken = newAccessToken)
                )
                call.respond(message = updatedUserResponse)
            }else{
                call.respond(message = userResponse)
            }
        }
    }
}
package com.meds.di

import com.meds.data.repository.TaskImageRepositoryDbImpl
import com.meds.data.repository.TaskRepositoryDbImpl
import com.meds.data.repository.UserAuthRepositoryImpl
import com.meds.domain.repository.TaskImageRepository
import com.meds.domain.repository.TaskRepository
import com.meds.domain.repository.UserAuthRepository
import com.mongodb.ConnectionString
import com.mongodb.MongoClientSettings
import com.mongodb.kotlin.client.coroutine.MongoClient
import org.koin.dsl.module
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo


private val connectionString = System.getenv("MONGO_DB_URI")
    //"mongodb://localhost:27017"

val appModule = module{
    single {
        KMongo.createClient(connectionString)
            .coroutine
            .getDatabase("Task_db")
//        MongoClient.create(connectionString)
//            .getDatabase("Task_db")
//        val settings = MongoClientSettings.builder()
//            .applyConnectionString(ConnectionString(connectionString))
//            .applyToSslSettings{ it.enabled(true) }
//            .build()
//          KMongo.createClient(settings)
//            .coroutine
//            .getDatabase("Task_db")
    }
    single<TaskRepository> {
        //TaskRepositoryImpl()
        TaskRepositoryDbImpl(get())
    }

    single<TaskImageRepository> {
        //TaskRepositoryImpl()
        TaskImageRepositoryDbImpl(get())
    }

    single<UserAuthRepository> {
        UserAuthRepositoryImpl(get())
    }
}
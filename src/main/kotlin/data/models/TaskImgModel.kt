package com.meds.data.models

import kotlinx.serialization.Serializable
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

@Serializable
data class TaskImgModel(
    @BsonId
    val id : String = ObjectId().toString(),
    val task : TaskModel,
    val image : String
)
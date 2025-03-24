package com.meds.domain.repository

import com.meds.data.models.TaskImgModel
import com.meds.data.models.TaskModel

interface TaskImageRepository{

    suspend fun getAllImgTasks() : List<TaskImgModel>

    suspend fun getImgTaskByTitle(name: String) : TaskImgModel?

    suspend fun addImgTask(imgTask: TaskImgModel) : TaskImgModel

    suspend fun deleteImgTask(id: String) : Boolean

}
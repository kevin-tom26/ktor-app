package com.meds.domain.repository

import com.meds.data.models.Priority
import com.meds.data.models.TaskModel

interface TaskRepository{

    suspend fun getAllTasks() : List<TaskModel>

    suspend fun getTaskByPriority(priority: Priority) : List<TaskModel>

    suspend fun getTaskByTitle(name: String) : TaskModel?

    suspend fun addTask(task: TaskModel) : TaskModel

    suspend fun deleteTask(id: String) : Boolean

    suspend fun getEnabledTaskWithPriority(enabled: Boolean, priority: Priority) : List<TaskModel>
}
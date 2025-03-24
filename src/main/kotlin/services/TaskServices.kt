package com.meds.services

import com.meds.data.models.TaskModel

object TaskServices{
    private val tasks = mutableListOf<TaskModel>()

    fun getAllTaskList() : List<TaskModel>{
        return tasks
    }

    fun addToTaskList(task : TaskModel) : TaskModel {
        tasks.add(task)
        return task
    }
}
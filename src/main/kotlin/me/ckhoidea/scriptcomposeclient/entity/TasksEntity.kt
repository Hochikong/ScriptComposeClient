package me.ckhoidea.scriptcomposeclient.entity

data class TasksEntity(
    val tasks: List<TaskDetailEntity>,
    val message: String,
    val code: Int
)
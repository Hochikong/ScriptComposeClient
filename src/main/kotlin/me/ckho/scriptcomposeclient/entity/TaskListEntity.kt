package me.ckho.scriptcomposeclient.entity

data class TaskListEntity(
    val tasks: List<TaskBriefEntity>,
    val message: String,
    val code: Int
)
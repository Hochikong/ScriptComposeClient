package me.ckhoidea.scriptcomposeclient.entity

data class TaskBriefsEntity(
    val brief: List<TaskBriefEntity>,
    val message: String,
    val code: Int
)
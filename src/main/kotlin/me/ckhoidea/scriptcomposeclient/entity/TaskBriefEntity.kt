package me.ckhoidea.scriptcomposeclient.entity

import com.fasterxml.jackson.annotation.JsonProperty

//{
//    "task_hash": "ca270ec27744964e393774339b252c29",
//    "duration": "2022-09-09 09:00:00.516 - 2022-09-09 09:00:19.711",
//    "log_hash": "830d03a66032dadd40db12fbf8a150ad",
//    "status": "Finished"
//}
data class TaskBriefEntity(
    @JsonProperty("task_hash")
    val taskHash: String = "",
    val duration: String = "Empty",
    @JsonProperty("log_hash")
    val logHash: String = "",
    val status: String = ""
)
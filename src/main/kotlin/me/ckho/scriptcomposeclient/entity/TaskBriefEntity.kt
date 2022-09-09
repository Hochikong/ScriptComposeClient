package me.ckho.scriptcomposeclient.entity

import com.fasterxml.jackson.annotation.JsonProperty

//    {
//        "cluster": "Douyin",
//        "group_name": "SN3",
//        "job_type": "cron",
//        "interval": 30,
//        "command": "sh dy_ort.sh",
//        "working_dir": "/home/vagrant/scripts/dy",
//        "start_at": "0 0 9 ? * * *",
//        "task_hash": "ca270ec27744964e393774339b252c29"
//    },
data class TaskBriefEntity(
    val cluster: String,
    @JsonProperty("group_name")
    val groupName: String,
    @JsonProperty("job_type")
    val jobType: String,
    val interval: String,
    val command: String,
    @JsonProperty("working_dir")
    val workingDir: String,
    @JsonProperty("start_at")
    val startAt: String,
    @JsonProperty("task_hash")
    val taskHash: String
) {
    fun toArray(): Array<String> {
        return arrayOf("NONE", groupName, command, jobType, interval, startAt, taskHash)
    }
}
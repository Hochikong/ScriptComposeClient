package me.ckhoidea.scriptcomposeclient.entity

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
data class TaskDetailEntity(
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
    val taskHash: String,
    val runWithTempBashScript: String = "",
    val tmpBashWorkingDir: String = ""
) {
    fun toArrayForJTable(): Array<String> {
        return arrayOf("NONE", groupName, command, jobType, interval, startAt, taskHash)
    }

    fun toArrayForJTable(
        badHash: List<String>,
        undefinedHash: List<String> = listOf(),
        successHash: List<String> = listOf()
    ): Array<String> {
        return if (taskHash in badHash) {
            arrayOf("FAILED", groupName, command, jobType, interval, startAt, taskHash)
        } else if (taskHash in undefinedHash) {
            arrayOf("UNDEFINED", groupName, command, jobType, interval, startAt, taskHash)
        } else if (taskHash in successHash) {
            arrayOf("SUCCEED", groupName, command, jobType, interval, startAt, taskHash)
        } else {
            arrayOf("NONE", groupName, command, jobType, interval, startAt, taskHash)
        }
    }

    override fun toString(): String {
        return "TaskDetailEntity\n" +
                "(\n\n" +
                "cluster='$cluster', \n\n" +
                "groupName='$groupName', \n\n" +
                "jobType='$jobType', \n\n" +
                "interval='$interval',\n\n" +
                "command='$command',\n\n" +
                "workingDir='$workingDir',\n\n" +
                "startAt='$startAt', \n\n" +
                "taskHash='$taskHash'\n\n" +
                "run_with_temp_bash_script='$runWithTempBashScript'\n\n" +
                "tmp_bash_working_dir='$tmpBashWorkingDir'\n\n" +
                ")\n"
    }


}
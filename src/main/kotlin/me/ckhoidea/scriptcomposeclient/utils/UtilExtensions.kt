package me.ckhoidea.scriptcomposeclient.utils

import me.ckhoidea.scriptcomposeclient.MainApp
import me.ckhoidea.scriptcomposeclient.entity.TaskDetailEntity

fun MainApp.splitClustersAndCreateNewTabs(tasks: List<TaskDetailEntity>, badHash: List<String> = listOf()) {
    val distinctClusters = tasks.map { it.cluster }.toSet().toList()
    for (cluster in distinctClusters) {
        val clusterScripts = tasks.filter { it.cluster == cluster }.toList()
        if (badHash.isNotEmpty()) {
            val clusterScripts2DArray: Array<Array<String>> =
                (clusterScripts.map { it.toArrayForJTable(badHash) }.toList()).toTypedArray()
            newTab(cluster, clusterScripts2DArray)
        } else {
            val clusterScripts2DArray: Array<Array<String>> =
                (clusterScripts.map { it.toArrayForJTable() }.toList()).toTypedArray()
            newTab(cluster, clusterScripts2DArray)
        }
    }
}
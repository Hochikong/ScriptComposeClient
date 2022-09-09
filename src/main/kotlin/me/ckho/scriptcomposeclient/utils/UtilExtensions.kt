package me.ckho.scriptcomposeclient.utils

import me.ckho.scriptcomposeclient.MainApp
import me.ckho.scriptcomposeclient.entity.TaskBriefEntity

fun MainApp.splitClustersAndCreateNewTabs(tasks: List<TaskBriefEntity>) {
    val distinctClusters = tasks.map { it.cluster }.toSet().toList()
    for (cluster in distinctClusters) {
        val clusterScripts = tasks.filter { it.cluster == cluster }.toList()
        val clusterScripts2DArray: Array<Array<String>> = (clusterScripts.map { it.toArray() }.toList()).toTypedArray()
        newTab(cluster, clusterScripts2DArray)
    }
}
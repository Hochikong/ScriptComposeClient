package me.ckhoidea.scriptcomposeclient.utils

import me.ckhoidea.scriptcomposeclient.MainApp
import me.ckhoidea.scriptcomposeclient.entity.TaskDetailEntity

fun MainApp.splitClustersAndCreateNewTabs(
    tasks: List<TaskDetailEntity>,
    should_render: Boolean = false,
    badHash: List<String> = listOf(),
    undHash: List<String> = listOf(),
    sucHash: List<String> = listOf()
) {
    val distinctClusters = tasks.map { it.cluster }.toSet().toList()
    for (cluster in distinctClusters) {
        val clusterScripts = tasks.filter { it.cluster == cluster }.toList()
        if (should_render) {
            val clusterScripts2DArray: Array<Array<String>> =
                (clusterScripts.map { it.toArrayForJTable(badHash, undHash, sucHash) }.toList()).toTypedArray()
//            JSONMapper.writeValue(Paths.get("$cluster.json").toFile(), clusterScripts2DArray)
            newTab(cluster, clusterScripts2DArray)
        } else {
            val clusterScripts2DArray: Array<Array<String>> =
                (clusterScripts.map { it.toArrayForJTable() }.toList()).toTypedArray()
//            JSONMapper.writeValue(Paths.get("$cluster.json").toFile(), clusterScripts2DArray)
            newTab(cluster, clusterScripts2DArray)
        }
    }
}
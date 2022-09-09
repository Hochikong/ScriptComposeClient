package me.ckhoidea.scriptcomposeclient.utils

import me.ckho.USwingGUI.entity.SimpleConnectionCfg
import me.ckhoidea.scriptcomposeclient.MainApp
import okhttp3.FormBody
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.Request

fun MainApp.composeLogin(cfg: SimpleConnectionCfg): Boolean {
    val reqPayload = FormBody.Builder()
        .add("username", cfg.username)
        .add("password", cfg.password)
        .build()
    val req = Request.Builder()
        .url(cfg.url + "/login")
        .post(reqPayload)
        .build()
    val response = httpClient.newCall(req).execute()
    val homePage = response.body!!.string()
    return "Welcome to use Scripts Composer" in homePage
}

fun MainApp.listCronTasks(cfg: SimpleConnectionCfg): String {
    val urlParams = (cfg.url + "/tasks/allTasks/byType").toHttpUrl().newBuilder()
        .addQueryParameter("type", "cron")
        .build()

    val req = Request.Builder()
        .url(urlParams)
        .get()
        .build()
    var response = httpClient.newCall(req).execute()
    val data =  response.body!!.string()
    return if ("Please Login" in data){
        composeLogin(cfg)
        response = httpClient.newCall(req).execute()
        return response.body!!.string()
    }else{
        data
    }
}

fun MainApp.fetchLogBriefs(cfg: SimpleConnectionCfg, taskHash: String, st_time: Long, ed_time: Long): String {
    val urlParams = (cfg.url + "/logs/brief").toHttpUrl().newBuilder()
        .addQueryParameter("task_hash", taskHash)
        .addQueryParameter("st", st_time.toString())
        .addQueryParameter("ed", ed_time.toString())
        .build()
    val req = Request.Builder()
        .url(urlParams)
        .get()
        .build()
    var response = httpClient.newCall(req).execute()
    val data =  response.body!!.string()
    return if ("Please Login" in data){
        composeLogin(cfg)
        response = httpClient.newCall(req).execute()
        return response.body!!.string()
    }else{
        data
    }
}

fun MainApp.fetchLog(cfg: SimpleConnectionCfg, log_hash: String): String {
    val urlParams = (cfg.url + "/logs/detail").toHttpUrl().newBuilder()
        .addQueryParameter("log_hash", log_hash)
        .build()
    val req = Request.Builder()
        .url(urlParams)
        .get()
        .build()
    var response = httpClient.newCall(req).execute()
    val data =  response.body!!.string()
    return if ("Please Login" in data){
        composeLogin(cfg)
        response = httpClient.newCall(req).execute()
        return response.body!!.string()
    }else{
        data
    }
}
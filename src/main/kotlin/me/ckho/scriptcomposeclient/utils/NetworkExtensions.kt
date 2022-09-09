package me.ckho.scriptcomposeclient.utils

import me.ckho.USwingGUI.entity.SimpleConnectionCfg
import me.ckho.scriptcomposeclient.MainApp
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
    val response = httpClient.newCall(req).execute()
    val data = response.body!!.string()
    return data
}
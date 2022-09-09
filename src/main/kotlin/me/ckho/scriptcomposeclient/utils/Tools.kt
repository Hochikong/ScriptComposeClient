package me.ckho.scriptcomposeclient.utils

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.KotlinFeature
import com.fasterxml.jackson.module.kotlin.KotlinModule
import java.sql.Timestamp
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

val JSONMapper: ObjectMapper = ObjectMapper().registerModule(
    KotlinModule.Builder()
        .withReflectionCacheSize(512)
        .configure(KotlinFeature.NullToEmptyCollection, false)
        .configure(KotlinFeature.NullToEmptyMap, false)
        .configure(KotlinFeature.NullIsSameAsDefault, false)
        .configure(KotlinFeature.SingletonSupport, false)
        .configure(KotlinFeature.StrictNullChecks, false)
        .build()
)

val YAMLLoader: ObjectMapper = ObjectMapper(YAMLFactory()).let { it.findAndRegisterModules() }

fun date2instant(datetime: String): Instant {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    val temporalAccessor = formatter.parse(datetime)
    val localDateTime = LocalDateTime.from(temporalAccessor)
    val zonedDateTime = ZonedDateTime.of(localDateTime, ZoneId.systemDefault())
    return Instant.from(zonedDateTime)
}

fun date2timestamp(datetime: String): Long {
    val instant = date2instant(datetime)
    val timestamp: Timestamp = Timestamp.from(instant)
    return timestamp.time
}

fun now2str(): String {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    val now = LocalDateTime.now()
    return formatter.format(now)
}

fun beforeNow2timestamp(hoursBack: String): Long{
    val enum = listOf("Not Selected", "Last 1 Hour", "Last 6 Hours", "Last 12 Hours", "Last 1 Day", "Last 3 Days", "Last Week")
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    if (hoursBack !in enum){
        return -1L
    }else{
        val now = LocalDateTime.now()
        // by default is 1 hour back
        val hoursBackTime = when(hoursBack){
            "Not Selected" -> now.minusHours(1)
            "Last 1 Hour" -> now.minusHours(1)
            "Last 6 Hours" -> now.minusHours(6)
            "Last 12 Hours" -> now.minusHours(12)
            "Last 1 Day" -> now.minusHours(24)
            "Last 3 Days" -> now.minusHours(72)
            "Last Week" -> now.minusHours(168)
            else -> now.minusHours(1)
        }
        val hoursBackTimeStr = formatter.format(hoursBackTime)
        return date2timestamp(hoursBackTimeStr)
    }

}
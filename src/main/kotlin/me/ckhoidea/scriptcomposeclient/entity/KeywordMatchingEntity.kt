package me.ckhoidea.scriptcomposeclient.entity

import com.fasterxml.jackson.annotation.JsonProperty

data class KeywordMatchingEntity(
    @JsonProperty("SUCCEED")
    val succeed: List<String>,
    @JsonProperty("FAILED")
    val failed: List<String>,
    @JsonProperty("UNDEFINED")
    val undefined: List<String>
)
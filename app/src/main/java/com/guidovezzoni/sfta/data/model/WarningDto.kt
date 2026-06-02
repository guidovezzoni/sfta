package com.guidovezzoni.sfta.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WarningDto(
    @SerialName("code") val code: String,
    @SerialName("message") val message: String,
    @SerialName("severity") val severity: String,
)

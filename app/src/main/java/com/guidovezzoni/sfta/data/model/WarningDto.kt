package com.guidovezzoni.sfta.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WarningDto(
    @SerialName("code") val code: String? = null,
    @SerialName("message") val message: String? = null,
    @SerialName("severity") val severity: String? = null,
)

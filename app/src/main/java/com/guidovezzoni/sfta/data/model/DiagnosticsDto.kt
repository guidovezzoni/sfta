package com.guidovezzoni.sfta.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DiagnosticsDto(
    @SerialName("fault_codes") val faultCodes: List<String>,
    @SerialName("warnings") val warnings: List<WarningDto>,
)

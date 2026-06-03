package com.guidovezzoni.sfta.domain.model

data class DiagnosticsInfo(
    val faultCodes: List<String>? = null,
    val warnings: List<WarningInfo>? = null,
)

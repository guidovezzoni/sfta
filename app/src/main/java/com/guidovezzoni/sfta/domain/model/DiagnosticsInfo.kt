package com.guidovezzoni.sfta.domain.model

data class DiagnosticsInfo(
    val faultCodes: List<String>,
    val warnings: List<WarningInfo>,
)

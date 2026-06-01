package com.guidovezzoni.sfta.domain.model

data class WarningInfo(
    val code: String,
    val message: String,
    val severity: WarningSeverity,
)

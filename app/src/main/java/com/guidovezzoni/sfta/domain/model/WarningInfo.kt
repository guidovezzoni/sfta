package com.guidovezzoni.sfta.domain.model

data class WarningInfo(
    val code: String? = null,
    val message: String? = null,
    val severity: WarningSeverity? = null,
)

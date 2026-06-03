package com.guidovezzoni.sfta.ui.state

data class DashboardWarningInfo(
    val code: String? = null,
    val message: String? = null,
    val severity: DashboardWarningSeverity? = null,
)

package com.guidovezzoni.sfta.ui.intent

sealed class DashboardUiIntent {
    data object LoadDashboard : DashboardUiIntent()
    data object RetryLoad : DashboardUiIntent()
}

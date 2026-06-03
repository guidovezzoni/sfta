package com.guidovezzoni.sfta.ui.state

data class DashboardUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val bikeName: String? = null,
    val batteryChargePercent: Int? = null,
    val batteryRangeKm: Int? = null,
    val batteryTemperatureCelsius: Double? = null,
    val chargingState: DashboardChargingState? = null,
    val motorPowerHp: Double? = null,
    val motorTemperatureCelsius: Double? = null,
    val powerMap: DashboardPowerMap? = null,
    val maxPowerHp: Int? = null,
    val engineBrakingPercent: Int? = null,
    val regenPercent: Int? = null,
    val sessionDurationSeconds: Long? = null,
    val sessionDistanceKm: Double? = null,
    val sessionMaxSpeedKmh: Double? = null,
    val warnings: List<DashboardWarningInfo>? = null,
)

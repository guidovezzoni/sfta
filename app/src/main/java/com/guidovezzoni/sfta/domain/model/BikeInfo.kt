package com.guidovezzoni.sfta.domain.model

data class BikeInfo(
    val bike: BikeDetails,
    val timestamp: String,
    val battery: BatteryInfo,
    val motor: MotorInfo,
    val rideSettings: RideSettingsInfo,
    val session: SessionInfo,
    val diagnostics: DiagnosticsInfo,
)

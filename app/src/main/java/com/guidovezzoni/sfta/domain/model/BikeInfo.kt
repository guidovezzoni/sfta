package com.guidovezzoni.sfta.domain.model

import kotlinx.datetime.Instant

data class BikeInfo(
    val bike: BikeDetails,
    val timestamp: Instant,
    val battery: BatteryInfo,
    val motor: MotorInfo,
    val rideSettings: RideSettingsInfo,
    val session: SessionInfo,
    val diagnostics: DiagnosticsInfo,
)

package com.guidovezzoni.sfta.domain.model

import kotlinx.datetime.Instant

data class BikeInfo(
    val bike: BikeDetails? = null,
    val timestamp: Instant? = null,
    val battery: BatteryInfo? = null,
    val motor: MotorInfo? = null,
    val rideSettings: RideSettingsInfo? = null,
    val session: SessionInfo? = null,
    val diagnostics: DiagnosticsInfo? = null,
)

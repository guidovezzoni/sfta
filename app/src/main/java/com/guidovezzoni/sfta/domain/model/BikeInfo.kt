package com.guidovezzoni.sfta.domain.model

data class BikeInfo(
    val model: String,
    val variant: String,
    val firmwareVersion: String,
    val imageUrl: String,
    val timestamp: String,
    val battery: BatteryInfo,
    val motor: MotorInfo,
    val rideSettings: RideSettingsInfo,
    val session: SessionInfo,
    val warnings: List<WarningInfo>,
)

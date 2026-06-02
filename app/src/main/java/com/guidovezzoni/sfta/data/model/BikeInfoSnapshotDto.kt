package com.guidovezzoni.sfta.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BikeInfoSnapshotDto(
    @SerialName("bike") val bike: BikeDto,
    @SerialName("timestamp") val timestamp: String,
    @SerialName("battery") val battery: BatteryDto,
    @SerialName("motor") val motor: MotorDto,
    @SerialName("ride_settings") val rideSettings: RideSettingsDto,
    @SerialName("session") val session: SessionDto,
    @SerialName("diagnostics") val diagnostics: DiagnosticsDto,
)

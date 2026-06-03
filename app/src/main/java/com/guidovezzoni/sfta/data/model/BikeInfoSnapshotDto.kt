package com.guidovezzoni.sfta.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BikeInfoSnapshotDto(
    @SerialName("bike") val bike: BikeDto? = null,
    @SerialName("timestamp") val timestamp: String? = null,
    @SerialName("battery") val battery: BatteryDto? = null,
    @SerialName("motor") val motor: MotorDto? = null,
    @SerialName("ride_settings") val rideSettings: RideSettingsDto? = null,
    @SerialName("session") val session: SessionDto? = null,
    @SerialName("diagnostics") val diagnostics: DiagnosticsDto? = null,
)

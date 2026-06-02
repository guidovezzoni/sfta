package com.guidovezzoni.sfta.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MotorDto(
    @SerialName("power_hp") val powerHp: Double,
    @SerialName("temperature_c") val temperatureC: Double,
    @SerialName("current_speed_kmh") val currentSpeedKmh: Double,
)

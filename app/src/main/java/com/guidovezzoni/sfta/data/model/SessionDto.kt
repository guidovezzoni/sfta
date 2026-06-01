package com.guidovezzoni.sfta.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SessionDto(
    @SerialName("duration_s") val durationS: Long,
    @SerialName("distance_km") val distanceKm: Double,
    @SerialName("max_speed_kmh") val maxSpeedKmh: Double,
)

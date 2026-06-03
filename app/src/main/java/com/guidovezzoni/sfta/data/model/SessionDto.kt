package com.guidovezzoni.sfta.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SessionDto(
    @SerialName("duration_s") val durationS: Long? = null,
    @SerialName("distance_km") val distanceKm: Double? = null,
    @SerialName("max_speed_kmh") val maxSpeedKmh: Double? = null,
)

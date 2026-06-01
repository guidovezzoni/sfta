package com.guidovezzoni.sfta.domain.model

data class SessionInfo(
    val durationSeconds: Long,
    val distanceKm: Double,
    val maxSpeedKmh: Double,
)

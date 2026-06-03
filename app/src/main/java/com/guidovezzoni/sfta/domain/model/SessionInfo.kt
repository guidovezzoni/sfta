package com.guidovezzoni.sfta.domain.model

data class SessionInfo(
    val durationSeconds: Long? = null,
    val distanceKm: Double? = null,
    val maxSpeedKmh: Double? = null,
)

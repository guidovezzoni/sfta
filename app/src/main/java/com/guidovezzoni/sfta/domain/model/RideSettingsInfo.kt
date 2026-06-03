package com.guidovezzoni.sfta.domain.model

data class RideSettingsInfo(
    val powerMap: PowerMap? = null,
    val maxPowerHp: Int? = null,
    val engineBrakingPercent: Int? = null,
    val regenPercent: Int? = null,
)

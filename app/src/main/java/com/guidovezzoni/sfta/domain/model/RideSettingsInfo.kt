package com.guidovezzoni.sfta.domain.model

data class RideSettingsInfo(
    val powerMap: PowerMap,
    val maxPowerHp: Int,
    val engineBrakingPercent: Int,
    val regenPercent: Int,
)

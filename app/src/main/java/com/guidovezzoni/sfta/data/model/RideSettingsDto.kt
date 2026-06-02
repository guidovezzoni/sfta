package com.guidovezzoni.sfta.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RideSettingsDto(
    @SerialName("power_map") val powerMap: String,
    @SerialName("max_power_hp") val maxPowerHp: Int,
    @SerialName("engine_braking_pct") val engineBrakingPct: Int,
    @SerialName("regen_pct") val regenPct: Int,
)

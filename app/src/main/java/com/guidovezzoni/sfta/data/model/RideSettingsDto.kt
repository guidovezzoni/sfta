package com.guidovezzoni.sfta.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RideSettingsDto(
    @SerialName("power_map") val powerMap: String? = null,
    @SerialName("max_power_hp") val maxPowerHp: Int? = null,
    @SerialName("engine_braking_pct") val engineBrakingPct: Int? = null,
    @SerialName("regen_pct") val regenPct: Int? = null,
)

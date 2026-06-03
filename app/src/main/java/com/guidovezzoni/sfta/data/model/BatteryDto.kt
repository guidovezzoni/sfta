package com.guidovezzoni.sfta.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BatteryDto(
    @SerialName("state_of_charge_pct") val stateOfChargePct: Int? = null,
    @SerialName("estimated_range_km") val estimatedRangeKm: Int? = null,
    @SerialName("temperature_c") val temperatureC: Double? = null,
    @SerialName("charging_state") val chargingState: String? = null,
)

package com.guidovezzoni.sfta.domain.model

data class BatteryInfo(
    val stateOfChargePercent: Int? = null,
    val estimatedRangeKm: Int? = null,
    val temperatureCelsius: Double? = null,
    val chargingState: ChargingState? = null,
)

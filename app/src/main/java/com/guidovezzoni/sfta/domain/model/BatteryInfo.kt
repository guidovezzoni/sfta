package com.guidovezzoni.sfta.domain.model

data class BatteryInfo(
    val stateOfChargePercent: Int,
    val estimatedRangeKm: Int,
    val temperatureCelsius: Double,
    val chargingState: ChargingState,
)

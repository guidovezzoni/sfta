package com.guidovezzoni.sfta.domain.mapper

import com.guidovezzoni.sfta.data.model.BikeInfoSnapshotDto
import com.guidovezzoni.sfta.domain.model.BatteryInfo
import com.guidovezzoni.sfta.domain.model.BikeDetails
import com.guidovezzoni.sfta.domain.model.BikeInfo
import com.guidovezzoni.sfta.domain.model.ChargingState
import com.guidovezzoni.sfta.domain.model.DiagnosticsInfo
import com.guidovezzoni.sfta.domain.model.MotorInfo
import com.guidovezzoni.sfta.domain.model.PowerMap
import com.guidovezzoni.sfta.domain.model.RideSettingsInfo
import com.guidovezzoni.sfta.domain.model.SessionInfo
import com.guidovezzoni.sfta.domain.model.WarningInfo
import com.guidovezzoni.sfta.domain.model.WarningSeverity

fun BikeInfoSnapshotDto.toDomain(): BikeInfo = BikeInfo(
    bike = BikeDetails(
        model = bike.model,
        variant = bike.variant,
        firmwareVersion = bike.firmwareVersion,
        imageUrl = bike.imageUrl,
    ),
    timestamp = timestamp,
    battery = BatteryInfo(
        stateOfChargePercent = battery.stateOfChargePct,
        estimatedRangeKm = battery.estimatedRangeKm,
        temperatureCelsius = battery.temperatureC,
        chargingState = ChargingState.entries.find {
            it.name.equals(battery.chargingState, ignoreCase = true)
        } ?: ChargingState.UNKNOWN,
    ),
    motor = MotorInfo(
        powerHp = motor.powerHp,
        temperatureCelsius = motor.temperatureC,
        currentSpeedKmh = motor.currentSpeedKmh,
    ),
    rideSettings = RideSettingsInfo(
        powerMap = PowerMap.entries.find {
            it.name.equals(rideSettings.powerMap, ignoreCase = true)
        } ?: PowerMap.UNKNOWN,
        maxPowerHp = rideSettings.maxPowerHp,
        engineBrakingPercent = rideSettings.engineBrakingPct,
        regenPercent = rideSettings.regenPct,
    ),
    session = SessionInfo(
        durationSeconds = session.durationS,
        distanceKm = session.distanceKm,
        maxSpeedKmh = session.maxSpeedKmh,
    ),
    diagnostics = DiagnosticsInfo(
        warnings = diagnostics.warnings.map { warningDto ->
            WarningInfo(
                code = warningDto.code,
                message = warningDto.message,
                severity = WarningSeverity.entries.find {
                    it.name.equals(warningDto.severity, ignoreCase = true)
                } ?: WarningSeverity.UNKNOWN,
            )
        },
    ),
)

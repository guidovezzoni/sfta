package com.guidovezzoni.sfta.data.mapper

import com.guidovezzoni.sfta.data.model.BikeInfoSnapshotDto
import com.guidovezzoni.sfta.data.model.WarningDto
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
import kotlinx.datetime.Instant

fun BikeInfoSnapshotDto.toDomain(): BikeInfo = BikeInfo(
    bike = bike?.let { bikeDto ->
        BikeDetails(
            model = bikeDto.model,
            variant = bikeDto.variant,
            firmwareVersion = bikeDto.firmwareVersion,
            imageUrl = bikeDto.imageUrl,
        )
    },
    timestamp = timestamp?.let { Instant.parse(it) },
    battery = battery?.let { batteryDto ->
        BatteryInfo(
            stateOfChargePercent = batteryDto.stateOfChargePct,
            estimatedRangeKm = batteryDto.estimatedRangeKm,
            temperatureCelsius = batteryDto.temperatureC,
            chargingState = batteryDto.chargingState?.let { str ->
                ChargingState.entries.find { it.name.equals(str, ignoreCase = true) } ?: ChargingState.UNKNOWN
            },
        )
    },
    motor = motor?.let { motorDto ->
        MotorInfo(
            powerHp = motorDto.powerHp,
            temperatureCelsius = motorDto.temperatureC,
        )
    },
    rideSettings = rideSettings?.let { settingsDto ->
        RideSettingsInfo(
            powerMap = settingsDto.powerMap?.let { str ->
                PowerMap.entries.find { it.name.equals(str, ignoreCase = true) } ?: PowerMap.UNKNOWN
            },
            maxPowerHp = settingsDto.maxPowerHp,
            engineBrakingPercent = settingsDto.engineBrakingPct,
            regenPercent = settingsDto.regenPct,
        )
    },
    session = session?.let { sessionDto ->
        SessionInfo(
            durationSeconds = sessionDto.durationS,
            distanceKm = sessionDto.distanceKm,
            maxSpeedKmh = sessionDto.maxSpeedKmh,
        )
    },
    diagnostics = diagnostics?.let { diagnosticsDto ->
        DiagnosticsInfo(
            faultCodes = diagnosticsDto.faultCodes,
            warnings = diagnosticsDto.warnings?.map { warningDto -> warningDto.toDomain() },
        )
    },
)

private fun WarningDto.toDomain(): WarningInfo = WarningInfo(
    code = code,
    message = message,
    severity = severity?.let { str ->
        WarningSeverity.entries.find { it.name.equals(str, ignoreCase = true) } ?: WarningSeverity.UNKNOWN
    },
)

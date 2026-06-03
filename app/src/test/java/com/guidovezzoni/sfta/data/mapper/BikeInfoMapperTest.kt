package com.guidovezzoni.sfta.data.mapper

import com.guidovezzoni.sfta.data.model.BatteryDto
import com.guidovezzoni.sfta.data.model.BikeDto
import com.guidovezzoni.sfta.data.model.BikeInfoSnapshotDto
import com.guidovezzoni.sfta.data.model.DiagnosticsDto
import com.guidovezzoni.sfta.data.model.MotorDto
import com.guidovezzoni.sfta.data.model.RideSettingsDto
import com.guidovezzoni.sfta.data.model.SessionDto
import com.guidovezzoni.sfta.data.model.WarningDto
import com.guidovezzoni.sfta.domain.model.ChargingState
import com.guidovezzoni.sfta.domain.model.PowerMap
import com.guidovezzoni.sfta.domain.model.WarningSeverity
import kotlinx.datetime.Instant
import org.junit.Assert.assertEquals
import org.junit.Test

class BikeInfoMapperTest {

    private val fullyPopulatedDto = BikeInfoSnapshotDto(
        bike = BikeDto(
            model = "Stark VARG MX 1.2",
            variant = "Alpha",
            firmwareVersion = "3.4.1",
            imageUrl = "https://example.com/image.webp",
        ),
        timestamp = "2025-05-19T10:32:45Z",
        battery = BatteryDto(
            stateOfChargePct = 73,
            estimatedRangeKm = 38,
            temperatureC = 34.7,
            chargingState = "discharging",
        ),
        motor = MotorDto(
            powerHp = 52.4,
            temperatureC = 61.2,
            currentSpeedKmh = 47.3,
        ),
        rideSettings = RideSettingsDto(
            powerMap = "enduro",
            maxPowerHp = 80,
            engineBrakingPct = 45,
            regenPct = 60,
        ),
        session = SessionDto(
            durationS = 3742L,
            distanceKm = 24.7,
            maxSpeedKmh = 94.1,
        ),
        diagnostics = DiagnosticsDto(
            faultCodes = listOf("F001"),
            warnings = listOf(
                WarningDto(
                    code = "W_MOT_TEMP_HIGH",
                    message = "Motor temperature elevated",
                    severity = "warning",
                ),
            ),
        ),
    )

    @Test
    fun `GIVEN a fully populated BikeInfoSnapshotDto WHEN toDomain is called THEN all fields are correctly mapped`() {
        val result = fullyPopulatedDto.toDomain()

        assertEquals("Stark VARG MX 1.2", result.bike.model)
        assertEquals("Alpha", result.bike.variant)
        assertEquals("3.4.1", result.bike.firmwareVersion)
        assertEquals("https://example.com/image.webp", result.bike.imageUrl)
        val expectedTimestamp = Instant.parse("2025-05-19T10:32:45Z")
        assertEquals(expectedTimestamp, result.timestamp)
        assertEquals(73, result.battery.stateOfChargePercent)
        assertEquals(38, result.battery.estimatedRangeKm)
        assertEquals(34.7, result.battery.temperatureCelsius, 0.001)
        assertEquals(ChargingState.DISCHARGING, result.battery.chargingState)
        assertEquals(52.4, result.motor.powerHp, 0.001)
        assertEquals(61.2, result.motor.temperatureCelsius, 0.001)
        assertEquals(47.3, result.motor.currentSpeedKmh, 0.001)
        assertEquals(PowerMap.ENDURO, result.rideSettings.powerMap)
        assertEquals(80, result.rideSettings.maxPowerHp)
        assertEquals(45, result.rideSettings.engineBrakingPercent)
        assertEquals(60, result.rideSettings.regenPercent)
        assertEquals(3742L, result.session.durationSeconds)
        assertEquals(24.7, result.session.distanceKm, 0.001)
        assertEquals(94.1, result.session.maxSpeedKmh, 0.001)
        val expectedFaultCodes = listOf("F001")
        assertEquals(expectedFaultCodes, result.diagnostics.faultCodes)
        assertEquals(1, result.diagnostics.warnings.size)
        assertEquals("W_MOT_TEMP_HIGH", result.diagnostics.warnings[0].code)
        assertEquals("Motor temperature elevated", result.diagnostics.warnings[0].message)
        assertEquals(WarningSeverity.WARNING, result.diagnostics.warnings[0].severity)
    }

    @Test
    fun `GIVEN a DTO with unrecognised enum string values WHEN toDomain is called THEN enums fall back to UNKNOWN`() {
        val dtoWithUnknownEnums = fullyPopulatedDto.copy(
            battery = fullyPopulatedDto.battery.copy(chargingState = "supercharging"),
            rideSettings = fullyPopulatedDto.rideSettings.copy(powerMap = "turbo"),
            diagnostics = fullyPopulatedDto.diagnostics.copy(
                warnings = listOf(
                    WarningDto(
                        code = "W_TEST",
                        message = "Test",
                        severity = "extreme",
                    ),
                ),
            ),
        )

        val result = dtoWithUnknownEnums.toDomain()

        assertEquals(ChargingState.UNKNOWN, result.battery.chargingState)
        assertEquals(PowerMap.UNKNOWN, result.rideSettings.powerMap)
        assertEquals(WarningSeverity.UNKNOWN, result.diagnostics.warnings[0].severity)
    }

    @Test
    fun `GIVEN a DTO with an empty warnings list WHEN toDomain is called THEN BikeInfo diagnostics warnings is empty`() {
        val dtoWithEmptyWarnings = fullyPopulatedDto.copy(
            diagnostics = DiagnosticsDto(
                faultCodes = emptyList(),
                warnings = emptyList(),
            ),
        )

        val result = dtoWithEmptyWarnings.toDomain()

        assertEquals(emptyList<String>(), result.diagnostics.faultCodes)
        assertEquals(emptyList<Any>(), result.diagnostics.warnings)
    }

    @Test
    fun `GIVEN a DTO with empty faultCodes WHEN toDomain is called THEN diagnostics faultCodes is empty`() {
        val dtoWithEmptyFaultCodes = fullyPopulatedDto.copy(
            diagnostics = DiagnosticsDto(
                faultCodes = emptyList(),
                warnings = fullyPopulatedDto.diagnostics.warnings,
            ),
        )

        val result = dtoWithEmptyFaultCodes.toDomain()

        assertEquals(emptyList<String>(), result.diagnostics.faultCodes)
    }
}

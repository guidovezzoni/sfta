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
import org.junit.Assert.assertNull
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

        assertEquals("Stark VARG MX 1.2", result.bike?.model)
        assertEquals("Alpha", result.bike?.variant)
        assertEquals("3.4.1", result.bike?.firmwareVersion)
        assertEquals("https://example.com/image.webp", result.bike?.imageUrl)
        val expectedTimestamp = Instant.parse("2025-05-19T10:32:45Z")
        assertEquals(expectedTimestamp, result.timestamp)
        assertEquals(73, result.battery?.stateOfChargePercent)
        assertEquals(38, result.battery?.estimatedRangeKm)
        assertEquals(34.7, requireNotNull(result.battery?.temperatureCelsius), 0.001)
        assertEquals(ChargingState.DISCHARGING, result.battery.chargingState)
        assertEquals(52.4, requireNotNull(result.motor?.powerHp), 0.001)
        assertEquals(61.2, requireNotNull(result.motor.temperatureCelsius), 0.001)
        assertEquals(PowerMap.ENDURO, result.rideSettings?.powerMap)
        assertEquals(80, result.rideSettings?.maxPowerHp)
        assertEquals(45, result.rideSettings?.engineBrakingPercent)
        assertEquals(60, result.rideSettings?.regenPercent)
        assertEquals(3742L, result.session?.durationSeconds)
        assertEquals(24.7, requireNotNull(result.session?.distanceKm), 0.001)
        assertEquals(94.1, requireNotNull(result.session.maxSpeedKmh), 0.001)
        val expectedFaultCodes = listOf("F001")
        assertEquals(expectedFaultCodes, result.diagnostics?.faultCodes)
        assertEquals(1, result.diagnostics?.warnings?.size)
        assertEquals("W_MOT_TEMP_HIGH", result.diagnostics?.warnings?.get(0)?.code)
        assertEquals("Motor temperature elevated", result.diagnostics?.warnings?.get(0)?.message)
        assertEquals(WarningSeverity.WARNING, result.diagnostics?.warnings?.get(0)?.severity)
    }

    @Test
    fun `GIVEN a DTO with unrecognised enum string values WHEN toDomain is called THEN enums fall back to UNKNOWN`() {
        val dtoWithUnknownEnums = fullyPopulatedDto.copy(
            battery = fullyPopulatedDto.battery?.copy(chargingState = "supercharging"),
            rideSettings = fullyPopulatedDto.rideSettings?.copy(powerMap = "turbo"),
            diagnostics = fullyPopulatedDto.diagnostics?.copy(
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

        assertEquals(ChargingState.UNKNOWN, result.battery?.chargingState)
        assertEquals(PowerMap.UNKNOWN, result.rideSettings?.powerMap)
        assertEquals(WarningSeverity.UNKNOWN, result.diagnostics?.warnings?.get(0)?.severity)
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

        assertEquals(emptyList<String>(), result.diagnostics?.faultCodes)
        assertEquals(emptyList<Any>(), result.diagnostics?.warnings)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `GIVEN a DTO with a malformed timestamp WHEN toDomain is called THEN Instant parse throws IllegalArgumentException`() {
        val dtoWithMalformedTimestamp = fullyPopulatedDto.copy(
            timestamp = "not-a-date",
        )

        dtoWithMalformedTimestamp.toDomain()
    }

    @Test
    fun `GIVEN a DTO with empty faultCodes WHEN toDomain is called THEN diagnostics faultCodes is empty`() {
        val dtoWithEmptyFaultCodes = fullyPopulatedDto.copy(
            diagnostics = DiagnosticsDto(
                faultCodes = emptyList(),
                warnings = fullyPopulatedDto.diagnostics?.warnings,
            ),
        )

        val result = dtoWithEmptyFaultCodes.toDomain()

        assertEquals(emptyList<String>(), result.diagnostics?.faultCodes)
    }

    @Test
    fun `GIVEN a DTO with null sub-objects WHEN toDomain is called THEN corresponding domain fields are null`() {
        val dtoWithNullSubObjects = BikeInfoSnapshotDto(
            bike = null,
            timestamp = "2025-05-19T10:32:45Z",
            battery = null,
            motor = null,
            rideSettings = null,
            session = null,
            diagnostics = null,
        )

        val result = dtoWithNullSubObjects.toDomain()

        assertNull(result.bike)
        assertNull(result.battery)
        assertNull(result.motor)
        assertNull(result.rideSettings)
        assertNull(result.session)
        assertNull(result.diagnostics)
    }

    @Test
    fun `GIVEN a DTO with all-null leaf fields WHEN toDomain is called THEN all domain fields are null`() {
        val dtoWithNullLeafFields = BikeInfoSnapshotDto(
            bike = BikeDto(
                model = null,
                variant = null,
                firmwareVersion = null,
                imageUrl = null,
            ),
            timestamp = "2025-05-19T10:32:45Z",
            battery = BatteryDto(
                stateOfChargePct = null,
                estimatedRangeKm = null,
                temperatureC = null,
                chargingState = null,
            ),
            motor = MotorDto(
                powerHp = null,
                temperatureC = null,
            ),
            rideSettings = RideSettingsDto(
                powerMap = null,
                maxPowerHp = null,
                engineBrakingPct = null,
                regenPct = null,
            ),
            session = SessionDto(
                durationS = null,
                distanceKm = null,
                maxSpeedKmh = null,
            ),
            diagnostics = DiagnosticsDto(
                faultCodes = null,
                warnings = null,
            ),
        )

        val result = dtoWithNullLeafFields.toDomain()

        assertNull(result.bike?.model)
        assertNull(result.bike?.variant)
        assertNull(result.bike?.firmwareVersion)
        assertNull(result.bike?.imageUrl)
        assertNull(result.battery?.stateOfChargePercent)
        assertNull(result.battery?.estimatedRangeKm)
        assertNull(result.battery?.temperatureCelsius)
        assertNull(result.battery?.chargingState)
        assertNull(result.motor?.powerHp)
        assertNull(result.motor?.temperatureCelsius)
        assertNull(result.rideSettings?.powerMap)
        assertNull(result.rideSettings?.maxPowerHp)
        assertNull(result.rideSettings?.engineBrakingPercent)
        assertNull(result.rideSettings?.regenPercent)
        assertNull(result.session?.durationSeconds)
        assertNull(result.session?.distanceKm)
        assertNull(result.session?.maxSpeedKmh)
        assertNull(result.diagnostics?.faultCodes)
        assertNull(result.diagnostics?.warnings)
    }

    @Test
    fun `GIVEN a DTO with null enum strings WHEN toDomain is called THEN domain enum fields are null not UNKNOWN`() {
        val dtoWithNullEnums = BikeInfoSnapshotDto(
            bike = BikeDto(),
            timestamp = "2025-05-19T10:32:45Z",
            battery = BatteryDto(chargingState = null),
            motor = MotorDto(),
            rideSettings = RideSettingsDto(powerMap = null),
            session = SessionDto(),
            diagnostics = DiagnosticsDto(
                faultCodes = emptyList(),
                warnings = listOf(WarningDto(severity = null)),
            ),
        )

        val result = dtoWithNullEnums.toDomain()

        assertNull(result.battery?.chargingState)
        assertNull(result.rideSettings?.powerMap)
        assertNull(result.diagnostics?.warnings?.get(0)?.severity)
    }

    @Test
    fun `GIVEN a DTO with null timestamp WHEN toDomain is called THEN domain Instant is null`() {
        val dtoWithNullTimestamp = fullyPopulatedDto.copy(timestamp = null)

        val result = dtoWithNullTimestamp.toDomain()

        assertNull(result.timestamp)
    }

    @Test
    fun `GIVEN a DTO with null lists WHEN toDomain is called THEN domain list fields are null`() {
        val dtoWithNullLists = fullyPopulatedDto.copy(
            diagnostics = DiagnosticsDto(
                faultCodes = null,
                warnings = null,
            ),
        )

        val result = dtoWithNullLists.toDomain()

        assertNull(result.diagnostics?.faultCodes)
        assertNull(result.diagnostics?.warnings)
    }
}

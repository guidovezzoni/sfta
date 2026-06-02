package com.guidovezzoni.sfta.domain.usecase

import com.guidovezzoni.sfta.data.model.BatteryDto
import com.guidovezzoni.sfta.data.model.BikeDto
import com.guidovezzoni.sfta.data.model.BikeInfoSnapshotDto
import com.guidovezzoni.sfta.data.model.DiagnosticsDto
import com.guidovezzoni.sfta.data.model.MotorDto
import com.guidovezzoni.sfta.data.model.RideSettingsDto
import com.guidovezzoni.sfta.data.model.SessionDto
import com.guidovezzoni.sfta.domain.repository.BikeInfoRepository
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import io.mockk.junit4.MockKRule
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.io.IOException

class GetBikeInfoUseCaseTest {

    @get:Rule
    val mockkRule = MockKRule(this)

    @MockK
    lateinit var repository: BikeInfoRepository

    private lateinit var useCase: GetBikeInfoUseCase

    @Before
    fun setup() {
        useCase = GetBikeInfoUseCase(repository)
    }

    private val sampleDto = BikeInfoSnapshotDto(
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
            faultCodes = emptyList(),
            warnings = emptyList(),
        ),
    )

    @Test
    fun `GIVEN the repository returns a successful Result WHEN GetBikeInfoUseCase is invoked THEN it returns Result success with mapped BikeInfo`() =
        runTest {
            coEvery { repository.getBikeInfoSnapshot() } returns Result.success(sampleDto)

            val result = useCase()

            assertTrue(result.isSuccess)
            val bikeInfo = result.getOrThrow()
            assertEquals("Stark VARG MX 1.2", bikeInfo.bike.model)
            assertEquals("Alpha", bikeInfo.bike.variant)
            assertEquals("3.4.1", bikeInfo.bike.firmwareVersion)
            assertEquals(73, bikeInfo.battery.stateOfChargePercent)
            assertEquals(47.3, bikeInfo.motor.currentSpeedKmh, 0.001)
        }

    @Test
    fun `GIVEN the repository returns a failure Result WHEN GetBikeInfoUseCase is invoked THEN it returns the same Result failure`() =
        runTest {
            val expectedException = IOException("Asset not found")
            coEvery { repository.getBikeInfoSnapshot() } returns Result.failure(expectedException)

            val result = useCase()

            assertTrue(result.isFailure)
            assertEquals(expectedException, result.exceptionOrNull())
        }
}

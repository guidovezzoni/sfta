package com.guidovezzoni.sfta.domain.usecase

import com.guidovezzoni.sfta.domain.model.BatteryInfo
import com.guidovezzoni.sfta.domain.model.BikeDetails
import com.guidovezzoni.sfta.domain.model.BikeInfo
import com.guidovezzoni.sfta.domain.model.ChargingState
import com.guidovezzoni.sfta.domain.model.DiagnosticsInfo
import com.guidovezzoni.sfta.domain.model.MotorInfo
import com.guidovezzoni.sfta.domain.model.PowerMap
import com.guidovezzoni.sfta.domain.model.RideSettingsInfo
import com.guidovezzoni.sfta.domain.model.SessionInfo
import com.guidovezzoni.sfta.domain.repository.BikeInfoRepository
import io.mockk.coEvery
import kotlinx.datetime.Instant
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

    private val sampleBikeInfo = BikeInfo(
        bike = BikeDetails(
            model = "Stark VARG MX 1.2",
            variant = "Alpha",
            firmwareVersion = "3.4.1",
            imageUrl = "https://example.com/image.webp",
        ),
        timestamp = Instant.parse("2025-05-19T10:32:45Z"),
        battery = BatteryInfo(
            stateOfChargePercent = 73,
            estimatedRangeKm = 38,
            temperatureCelsius = 34.7,
            chargingState = ChargingState.DISCHARGING,
        ),
        motor = MotorInfo(
            powerHp = 52.4,
            temperatureCelsius = 61.2,
            currentSpeedKmh = 47.3,
        ),
        rideSettings = RideSettingsInfo(
            powerMap = PowerMap.ENDURO,
            maxPowerHp = 80,
            engineBrakingPercent = 45,
            regenPercent = 60,
        ),
        session = SessionInfo(
            durationSeconds = 3742L,
            distanceKm = 24.7,
            maxSpeedKmh = 94.1,
        ),
        diagnostics = DiagnosticsInfo(
            faultCodes = emptyList(),
            warnings = emptyList(),
        ),
    )

    @Test
    fun `GIVEN the repository returns a successful Result WHEN GetBikeInfoUseCase is invoked THEN it returns the same Result unchanged`() =
        runTest {
            coEvery { repository.getBikeInfoSnapshot() } returns Result.success(sampleBikeInfo)

            val result = useCase()

            assertTrue(result.isSuccess)
            val expectedBikeInfo = sampleBikeInfo
            assertEquals(expectedBikeInfo, result.getOrThrow())
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

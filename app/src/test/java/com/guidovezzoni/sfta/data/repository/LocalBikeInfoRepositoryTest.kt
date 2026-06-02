package com.guidovezzoni.sfta.data.repository

import android.content.res.AssetManager
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit4.MockKRule
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.io.ByteArrayInputStream
import java.io.FileNotFoundException

@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
class LocalBikeInfoRepositoryTest {

    @get:Rule
    val mockkRule = MockKRule(this)

    @MockK
    lateinit var assetManager: AssetManager

    private lateinit var repository: LocalBikeInfoRepository

    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {
        repository = LocalBikeInfoRepository(assetManager, testDispatcher)
    }

    @Test
    fun `GIVEN a valid JSON asset WHEN getBikeInfoSnapshot is called THEN it returns Result success with parsed DTO`() =
        runTest {
            val validJson = """
            {
              "bike": {
                "model": "Stark VARG MX 1.2",
                "variant": "Alpha",
                "firmware_version": "3.4.1",
                "image_url": "https://example.com/image.webp"
              },
              "timestamp": "2025-05-19T10:32:45Z",
              "battery": {
                "state_of_charge_pct": 73,
                "estimated_range_km": 38,
                "temperature_c": 34.7,
                "charging_state": "discharging"
              },
              "motor": {
                "power_hp": 52.4,
                "temperature_c": 61.2,
                "current_speed_kmh": 47.3
              },
              "ride_settings": {
                "power_map": "enduro",
                "max_power_hp": 80,
                "engine_braking_pct": 45,
                "regen_pct": 60
              },
              "session": {
                "duration_s": 3742,
                "distance_km": 24.7,
                "max_speed_kmh": 94.1
              },
              "diagnostics": {
                "fault_codes": [],
                "warnings": []
              }
            }
            """.trimIndent()
            every { assetManager.open("bike_info_snapshot.json") } returns ByteArrayInputStream(
                validJson.toByteArray()
            )

            val result = repository.getBikeInfoSnapshot()

            assertTrue(result.isSuccess)
            val dto = result.getOrThrow()
            assertEquals("Stark VARG MX 1.2", dto.bike.model)
            assertEquals("Alpha", dto.bike.variant)
            assertEquals("3.4.1", dto.bike.firmwareVersion)
            assertEquals("2025-05-19T10:32:45Z", dto.timestamp)
            assertEquals(73, dto.battery.stateOfChargePct)
            assertEquals(47.3, dto.motor.currentSpeedKmh, 0.001)
            assertEquals("enduro", dto.rideSettings.powerMap)
            assertEquals(3742L, dto.session.durationS)
        }

    @Test
    fun `GIVEN the asset file does not exist WHEN getBikeInfoSnapshot is called THEN it returns Result failure`() =
        runTest {
            every { assetManager.open("bike_info_snapshot.json") } throws FileNotFoundException("File not found")

            val result = repository.getBikeInfoSnapshot()

            assertTrue(result.isFailure)
            assertTrue(result.exceptionOrNull() is FileNotFoundException)
        }

    @Test
    fun `GIVEN the asset file contains invalid JSON WHEN getBikeInfoSnapshot is called THEN it returns Result failure`() =
        runTest {
            val invalidJson = "{ this is not valid json }"
            every { assetManager.open("bike_info_snapshot.json") } returns ByteArrayInputStream(
                invalidJson.toByteArray()
            )

            val result = repository.getBikeInfoSnapshot()

            assertTrue(result.isFailure)
        }
}

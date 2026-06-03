package com.guidovezzoni.sfta.ui.viewmodel

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
import com.guidovezzoni.sfta.domain.usecase.GetBikeInfoUseCase
import com.guidovezzoni.sfta.ui.intent.DashboardUiIntent
import com.guidovezzoni.sfta.ui.state.DashboardWarningSeverity
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import io.mockk.junit4.MockKRule
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class DashboardViewModelTest {

    @get:Rule
    val mockkRule = MockKRule(this)

    @MockK
    lateinit var getBikeInfoUseCase: GetBikeInfoUseCase

    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var viewModel: DashboardViewModel

    private val sampleBikeInfo = BikeInfo(
        bike = BikeDetails(model = "STARK VARG", variant = null),
        battery = BatteryInfo(
            stateOfChargePercent = 73,
            estimatedRangeKm = 38,
            temperatureCelsius = 34.7,
            chargingState = ChargingState.DISCHARGING,
        ),
        motor = MotorInfo(powerHp = 52.4, temperatureCelsius = 61.2),
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
        diagnostics = DiagnosticsInfo(warnings = emptyList()),
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = DashboardViewModel(getBikeInfoUseCase)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `GIVEN use case returns success WHEN LoadDashboard intent received THEN emits loading then populated state`() = runTest {
        var loadingStateCaptured = false
        coEvery { getBikeInfoUseCase() } coAnswers {
            loadingStateCaptured = viewModel.uiState.value.isLoading
            Result.success(sampleBikeInfo)
        }

        viewModel.onIntent(DashboardUiIntent.LoadDashboard)

        assertTrue(loadingStateCaptured)
        val finalState = viewModel.uiState.value
        assertFalse(finalState.isLoading)
        assertNull(finalState.errorMessage)
        val expectedBatteryPercent = 73
        assertEquals(expectedBatteryPercent, finalState.batteryChargePercent)
    }

    @Test
    fun `GIVEN use case returns failure WHEN LoadDashboard intent received THEN emits error state`() = runTest {
        coEvery { getBikeInfoUseCase() } returns Result.failure(RuntimeException("Network error"))

        viewModel.onIntent(DashboardUiIntent.LoadDashboard)

        val finalState = viewModel.uiState.value
        assertFalse(finalState.isLoading)
        assertNotNull(finalState.errorMessage)
    }

    @Test
    fun `GIVEN ViewModel is in error state WHEN RetryLoad intent received THEN clears error and reloads`() = runTest {
        coEvery { getBikeInfoUseCase() } returns Result.failure(RuntimeException("error"))
        viewModel.onIntent(DashboardUiIntent.LoadDashboard)
        assertNotNull(viewModel.uiState.value.errorMessage)

        coEvery { getBikeInfoUseCase() } returns Result.success(sampleBikeInfo)
        viewModel.onIntent(DashboardUiIntent.RetryLoad)

        val finalState = viewModel.uiState.value
        assertNull(finalState.errorMessage)
        assertFalse(finalState.isLoading)
        val expectedBatteryPercent = 73
        assertEquals(expectedBatteryPercent, finalState.batteryChargePercent)
    }

    @Test
    fun `GIVEN use case returns BikeInfo with warnings WHEN LoadDashboard intent received THEN state contains warnings`() = runTest {
        val warningInfo = WarningInfo(
            code = "W001",
            message = "Battery temperature high",
            severity = WarningSeverity.WARNING,
        )
        val bikeInfoWithWarnings = sampleBikeInfo.copy(
            diagnostics = DiagnosticsInfo(warnings = listOf(warningInfo))
        )
        coEvery { getBikeInfoUseCase() } returns Result.success(bikeInfoWithWarnings)

        viewModel.onIntent(DashboardUiIntent.LoadDashboard)

        val finalState = viewModel.uiState.value
        val expectedWarningsSize = 1
        assertEquals(expectedWarningsSize, finalState.warnings?.size)
        assertEquals("W001", finalState.warnings?.first()?.code)
        assertEquals(DashboardWarningSeverity.WARNING, finalState.warnings?.first()?.severity)
    }

    @Test
    fun `GIVEN use case returns BikeInfo with null battery WHEN LoadDashboard intent received THEN battery fields are null in state`() = runTest {
        val bikeInfoNullBattery = sampleBikeInfo.copy(battery = null)
        coEvery { getBikeInfoUseCase() } returns Result.success(bikeInfoNullBattery)

        viewModel.onIntent(DashboardUiIntent.LoadDashboard)

        val finalState = viewModel.uiState.value
        assertNull(finalState.batteryChargePercent)
        assertNull(finalState.batteryRangeKm)
        assertNull(finalState.batteryTemperatureCelsius)
        assertNull(finalState.chargingState)
        assertFalse(finalState.isLoading)
        assertNull(finalState.errorMessage)
    }
}

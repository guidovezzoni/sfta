package com.guidovezzoni.sfta.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.guidovezzoni.sfta.domain.model.BikeInfo
import com.guidovezzoni.sfta.domain.model.ChargingState
import com.guidovezzoni.sfta.domain.model.PowerMap
import com.guidovezzoni.sfta.domain.model.WarningInfo
import com.guidovezzoni.sfta.domain.model.WarningSeverity
import com.guidovezzoni.sfta.domain.usecase.GetBikeInfoUseCase
import com.guidovezzoni.sfta.ui.effect.DashboardUiEffect
import com.guidovezzoni.sfta.ui.intent.DashboardUiIntent
import com.guidovezzoni.sfta.ui.state.DashboardChargingState
import com.guidovezzoni.sfta.ui.state.DashboardPowerMap
import com.guidovezzoni.sfta.ui.state.DashboardUiState
import com.guidovezzoni.sfta.ui.state.DashboardWarningInfo
import com.guidovezzoni.sfta.ui.state.DashboardWarningSeverity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val ERROR_MESSAGE_GENERIC = "Failed to load bike data"

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val getBikeInfoUseCase: GetBikeInfoUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    private val _uiEffect = MutableSharedFlow<DashboardUiEffect>()
    // This is currently not used, however I left it here because:
    // * in a fully fledged UI it's likely to be used
    // * it's part of the MVI contract
    val uiEffect: SharedFlow<DashboardUiEffect> = _uiEffect.asSharedFlow()

    fun onIntent(intent: DashboardUiIntent) {
        when (intent) {
            is DashboardUiIntent.LoadDashboard -> loadDashboard()
            is DashboardUiIntent.RetryLoad -> retryLoad()
        }
    }

    private fun loadDashboard() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            getBikeInfoUseCase()
                .onSuccess { bikeInfo ->
                    _uiState.update { bikeInfo.toDashboardUiState() }
                }
                .onFailure {
                    _uiState.update { it.copy(isLoading = false, errorMessage = ERROR_MESSAGE_GENERIC) }
                }
        }
    }

    private fun retryLoad() {
        _uiState.update { it.copy(errorMessage = null) }
        loadDashboard()
    }
}

private fun BikeInfo.toDashboardUiState(): DashboardUiState {
    val model = bike?.model
    val variant = bike?.variant
    val name = when {
        model == null -> null
        variant != null -> "$model $variant"
        else -> model
    }
    return DashboardUiState(
        isLoading = false,
        errorMessage = null,
        bikeName = name,
        batteryChargePercent = battery?.stateOfChargePercent,
        batteryRangeKm = battery?.estimatedRangeKm,
        batteryTemperatureCelsius = battery?.temperatureCelsius,
        chargingState = battery?.chargingState?.toDashboardChargingState(),
        motorPowerHp = motor?.powerHp,
        motorTemperatureCelsius = motor?.temperatureCelsius,
        powerMap = rideSettings?.powerMap?.toDashboardPowerMap(),
        maxPowerHp = rideSettings?.maxPowerHp,
        engineBrakingPercent = rideSettings?.engineBrakingPercent,
        regenPercent = rideSettings?.regenPercent,
        sessionDurationSeconds = session?.durationSeconds,
        sessionDistanceKm = session?.distanceKm,
        sessionMaxSpeedKmh = session?.maxSpeedKmh,
        warnings = diagnostics?.warnings?.map { it.toDashboardWarningInfo() },
    )
}

private fun ChargingState.toDashboardChargingState(): DashboardChargingState = when (this) {
    ChargingState.DISCHARGING -> DashboardChargingState.DISCHARGING
    ChargingState.UNKNOWN -> DashboardChargingState.UNKNOWN
}

private fun PowerMap.toDashboardPowerMap(): DashboardPowerMap = when (this) {
    PowerMap.ENDURO -> DashboardPowerMap.ENDURO
    PowerMap.UNKNOWN -> DashboardPowerMap.UNKNOWN
}

private fun WarningInfo.toDashboardWarningInfo(): DashboardWarningInfo = DashboardWarningInfo(
    code = code,
    message = message,
    severity = severity?.toDashboardWarningSeverity(),
)

private fun WarningSeverity.toDashboardWarningSeverity(): DashboardWarningSeverity = when (this) {
    WarningSeverity.WARNING -> DashboardWarningSeverity.WARNING
    WarningSeverity.UNKNOWN -> DashboardWarningSeverity.UNKNOWN
}

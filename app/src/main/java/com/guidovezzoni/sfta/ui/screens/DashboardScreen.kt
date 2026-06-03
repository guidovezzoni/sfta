package com.guidovezzoni.sfta.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.guidovezzoni.sfta.R
import com.guidovezzoni.sfta.ui.intent.DashboardUiIntent
import com.guidovezzoni.sfta.ui.screens.components.BatteryPanel
import com.guidovezzoni.sfta.ui.screens.components.PowerPanel
import com.guidovezzoni.sfta.ui.screens.components.RideSettingsBar
import com.guidovezzoni.sfta.ui.screens.components.SessionPanel
import com.guidovezzoni.sfta.ui.screens.components.WarningBanner
import com.guidovezzoni.sfta.ui.state.DashboardChargingState
import com.guidovezzoni.sfta.ui.state.DashboardPowerMap
import com.guidovezzoni.sfta.ui.state.DashboardUiState
import com.guidovezzoni.sfta.ui.state.DashboardWarningInfo
import com.guidovezzoni.sfta.ui.state.DashboardWarningSeverity
import com.guidovezzoni.sfta.ui.theme.StarkFutureTechnicalAssessmentTheme
import com.guidovezzoni.sfta.ui.theme.TextPrimary

private val PANELS_PADDING = 8.dp
private val PANEL_SPACING = 8.dp
private val SECTION_SPACING = 8.dp

@Composable
fun DashboardScreen(
    modifier: Modifier = Modifier,
    uiState: DashboardUiState,
    onIntent: (DashboardUiIntent) -> Unit = {},
) {
    Scaffold(modifier = modifier) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            when {
                uiState.isLoading -> LoadingContent()
                uiState.errorMessage != null -> ErrorContent(
                    errorMessage = uiState.errorMessage,
                    onRetry = { onIntent(DashboardUiIntent.RetryLoad) },
                )
                else -> DashboardContent(uiState = uiState)
            }
        }
    }
}

@Composable
private fun LoadingContent() {
    val loadingDescription = stringResource(R.string.dashboard_loading_content_description)
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(
            modifier = Modifier.semantics { contentDescription = loadingDescription },
            color = MaterialTheme.colorScheme.primary,
        )
    }
}

@Composable
private fun ErrorContent(
    errorMessage: String,
    onRetry: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = errorMessage,
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimary,
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = onRetry) {
                    Text(text = stringResource(R.string.global_retry))
                }
            }
        }
    }
}

@Composable
private fun DashboardContent(uiState: DashboardUiState) {
    Column(modifier = Modifier.fillMaxSize()) {
        if (!uiState.warnings.isNullOrEmpty()) {
            WarningBanner(warnings = uiState.warnings)
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(PANELS_PADDING)
        ) {
            BatteryPanel(
                modifier = Modifier.weight(1f).fillMaxHeight(),
                batteryChargePercent = uiState.batteryChargePercent,
                batteryRangeKm = uiState.batteryRangeKm,
                batteryTemperatureCelsius = uiState.batteryTemperatureCelsius,
                chargingState = uiState.chargingState,
            )
            Spacer(modifier = Modifier.width(PANEL_SPACING))
            PowerPanel(
                modifier = Modifier.weight(1f).fillMaxHeight(),
                motorPowerHp = uiState.motorPowerHp,
                motorTemperatureCelsius = uiState.motorTemperatureCelsius,
            )
        }

        HorizontalDivider()
        Spacer(modifier = Modifier.height(SECTION_SPACING))

        SessionPanel(
            sessionDurationSeconds = uiState.sessionDurationSeconds,
            sessionDistanceKm = uiState.sessionDistanceKm,
            sessionMaxSpeedKmh = uiState.sessionMaxSpeedKmh,
        )

        HorizontalDivider()
        Spacer(modifier = Modifier.height(SECTION_SPACING))

        RideSettingsBar(
            powerMap = uiState.powerMap,
            maxPowerHp = uiState.maxPowerHp,
            engineBrakingPercent = uiState.engineBrakingPercent,
            regenPercent = uiState.regenPercent,
        )
    }
}

@Preview(
    showBackground = true,
    device = "spec:width=891dp,height=411dp,dpi=420,isRound=false,chinSize=0dp,orientation=landscape",
)
@Composable
private fun PreviewDashboardScreenContent() {
    StarkFutureTechnicalAssessmentTheme {
        DashboardScreen(
            uiState = DashboardUiState(
                isLoading = false,
                bikeName = "STARK VARG",
                batteryChargePercent = 73,
                batteryRangeKm = 38,
                batteryTemperatureCelsius = 34.7,
                chargingState = DashboardChargingState.DISCHARGING,
                motorPowerHp = 52.4,
                motorTemperatureCelsius = 61.2,
                powerMap = DashboardPowerMap.ENDURO,
                maxPowerHp = 80,
                engineBrakingPercent = 45,
                regenPercent = 60,
                sessionDurationSeconds = 3742L,
                sessionDistanceKm = 24.7,
                sessionMaxSpeedKmh = 94.1,
                warnings = emptyList(),
            )
        )
    }
}

@Preview(
    showBackground = true,
    device = "spec:width=891dp,height=411dp,dpi=420,isRound=false,chinSize=0dp,orientation=landscape",
)
@Composable
private fun PreviewDashboardScreenLoading() {
    StarkFutureTechnicalAssessmentTheme {
        DashboardScreen(uiState = DashboardUiState(isLoading = true))
    }
}

@Preview(
    showBackground = true,
    device = "spec:width=891dp,height=411dp,dpi=420,isRound=false,chinSize=0dp,orientation=landscape",
)
@Composable
private fun PreviewDashboardScreenError() {
    StarkFutureTechnicalAssessmentTheme {
        DashboardScreen(uiState = DashboardUiState(errorMessage = "Failed to load bike data"))
    }
}

@Preview(
    showBackground = true,
    device = "spec:width=891dp,height=411dp,dpi=420,isRound=false,chinSize=0dp,orientation=landscape",
)
@Composable
private fun PreviewDashboardScreenWithWarnings() {
    StarkFutureTechnicalAssessmentTheme {
        DashboardScreen(
            uiState = DashboardUiState(
                batteryChargePercent = 73,
                batteryRangeKm = 38,
                batteryTemperatureCelsius = 34.7,
                chargingState = DashboardChargingState.DISCHARGING,
                motorPowerHp = 52.4,
                motorTemperatureCelsius = 61.2,
                powerMap = DashboardPowerMap.ENDURO,
                maxPowerHp = 80,
                engineBrakingPercent = 45,
                regenPercent = 60,
                sessionDurationSeconds = 3742L,
                sessionDistanceKm = 24.7,
                sessionMaxSpeedKmh = 94.1,
                warnings = listOf(
                    DashboardWarningInfo(
                        code = "W001",
                        message = "Battery temperature high",
                        severity = DashboardWarningSeverity.WARNING,
                    )
                ),
            )
        )
    }
}

@Preview(
    showBackground = true,
    device = "spec:width=891dp,height=411dp,dpi=420,isRound=false,chinSize=0dp,orientation=landscape",
)
@Composable
private fun PreviewDashboardScreenLowBattery() {
    StarkFutureTechnicalAssessmentTheme {
        DashboardScreen(
            uiState = DashboardUiState(
                batteryChargePercent = 12,
                batteryRangeKm = 8,
                batteryTemperatureCelsius = 42.0,
                chargingState = DashboardChargingState.DISCHARGING,
                motorPowerHp = 45.0,
                motorTemperatureCelsius = 58.0,
                powerMap = DashboardPowerMap.ENDURO,
                maxPowerHp = 80,
                engineBrakingPercent = 45,
                regenPercent = 60,
                sessionDurationSeconds = 7200L,
                sessionDistanceKm = 48.3,
                sessionMaxSpeedKmh = 110.5,
                warnings = emptyList(),
            )
        )
    }
}

@Preview(
    showBackground = true,
    device = "spec:width=891dp,height=411dp,dpi=420,isRound=false,chinSize=0dp,orientation=landscape",
)
@Composable
private fun PreviewDashboardScreenNullFields() {
    StarkFutureTechnicalAssessmentTheme {
        DashboardScreen(
            uiState = DashboardUiState(
                isLoading = false,
                errorMessage = null,
                bikeName = null,
                batteryChargePercent = null,
                batteryRangeKm = null,
                batteryTemperatureCelsius = null,
                chargingState = null,
                motorPowerHp = null,
                motorTemperatureCelsius = null,
                powerMap = null,
                maxPowerHp = null,
                engineBrakingPercent = null,
                regenPercent = null,
                sessionDurationSeconds = null,
                sessionDistanceKm = null,
                sessionMaxSpeedKmh = null,
                warnings = null,
            )
        )
    }
}

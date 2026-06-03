package com.guidovezzoni.sfta.ui.screens.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.guidovezzoni.sfta.R
import com.guidovezzoni.sfta.ui.state.DashboardChargingState
import com.guidovezzoni.sfta.ui.theme.BatteryAmber
import com.guidovezzoni.sfta.ui.theme.BatteryGreen
import com.guidovezzoni.sfta.ui.theme.BatteryRed
import com.guidovezzoni.sfta.ui.theme.DashboardSurface
import com.guidovezzoni.sfta.ui.theme.StarkFutureTechnicalAssessmentTheme
import com.guidovezzoni.sfta.ui.theme.TextPrimary
import com.guidovezzoni.sfta.ui.theme.TextSecondary

private val PANEL_PADDING = 16.dp
private val LABEL_VALUE_SPACING = 4.dp
private val METRICS_ROW_SPACING = 24.dp

@Composable
fun BatteryPanel(
    modifier: Modifier = Modifier,
    batteryChargePercent: Int?,
    batteryRangeKm: Int?,
    batteryTemperatureCelsius: Double?,
    chargingState: DashboardChargingState?,
) {
    val socColor: Color = when {
        batteryChargePercent == null -> TextSecondary
        batteryChargePercent >= 50 -> BatteryGreen
        batteryChargePercent >= 20 -> BatteryAmber
        else -> BatteryRed
    }

    val socText = batteryChargePercent?.let {
        stringResource(R.string.dashboard_format_battery_charge, it)
    } ?: stringResource(R.string.global_placeholder)

    val rangeText = batteryRangeKm?.let {
        stringResource(R.string.dashboard_format_range, it)
    } ?: stringResource(R.string.global_placeholder)

    val tempText = batteryTemperatureCelsius?.let {
        stringResource(R.string.dashboard_format_temperature, it)
    } ?: stringResource(R.string.global_placeholder)

    val chargingText = when (chargingState) {
        DashboardChargingState.DISCHARGING -> stringResource(R.string.dashboard_charging_state_discharging)
        DashboardChargingState.UNKNOWN -> stringResource(R.string.dashboard_charging_state_unknown)
        null -> stringResource(R.string.global_placeholder)
    }

    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = DashboardSurface),
    ) {
        Column(modifier = Modifier.padding(PANEL_PADDING).fillMaxWidth()) {
            Text(
                text = stringResource(R.string.dashboard_battery_header),
                style = MaterialTheme.typography.titleLarge,
                color = TextPrimary,
            )
            Spacer(modifier = Modifier.height(LABEL_VALUE_SPACING))
            Text(
                text = socText,
                style = MaterialTheme.typography.displayLarge,
                color = socColor,
            )
            Spacer(modifier = Modifier.height(LABEL_VALUE_SPACING))
            Row {
                BatteryMetricItem(
                    label = stringResource(R.string.dashboard_label_range),
                    value = rangeText,
                )
                Spacer(modifier = Modifier.width(METRICS_ROW_SPACING))
                BatteryMetricItem(
                    label = stringResource(R.string.dashboard_label_temperature),
                    value = tempText,
                )
                Spacer(modifier = Modifier.width(METRICS_ROW_SPACING))
                BatteryMetricItem(
                    label = stringResource(R.string.dashboard_label_charging_state),
                    value = chargingText,
                )
            }
        }
    }
}

@Composable
private fun BatteryMetricItem(label: String, value: String) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = TextSecondary,
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            color = TextPrimary,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewBatteryPanelFull() {
    StarkFutureTechnicalAssessmentTheme {
        BatteryPanel(
            batteryChargePercent = 73,
            batteryRangeKm = 38,
            batteryTemperatureCelsius = 34.7,
            chargingState = DashboardChargingState.DISCHARGING,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewBatteryPanelLow() {
    StarkFutureTechnicalAssessmentTheme {
        BatteryPanel(
            batteryChargePercent = 12,
            batteryRangeKm = 8,
            batteryTemperatureCelsius = 40.0,
            chargingState = DashboardChargingState.DISCHARGING,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewBatteryPanelAmber() {
    StarkFutureTechnicalAssessmentTheme {
        BatteryPanel(
            batteryChargePercent = 35,
            batteryRangeKm = 18,
            batteryTemperatureCelsius = 36.0,
            chargingState = DashboardChargingState.UNKNOWN,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewBatteryPanelNull() {
    StarkFutureTechnicalAssessmentTheme {
        BatteryPanel(
            batteryChargePercent = null,
            batteryRangeKm = null,
            batteryTemperatureCelsius = null,
            chargingState = null,
        )
    }
}

package com.guidovezzoni.sfta.ui.screens.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.guidovezzoni.sfta.R
import com.guidovezzoni.sfta.ui.state.DashboardPowerMap
import com.guidovezzoni.sfta.ui.theme.StarkFutureTechnicalAssessmentTheme
import com.guidovezzoni.sfta.ui.theme.TextPrimary
import com.guidovezzoni.sfta.ui.theme.TextSecondary

private val PANEL_PADDING = 16.dp
private val METRICS_ROW_SPACING = 32.dp

@Composable
fun RideSettingsBar(
    modifier: Modifier = Modifier,
    powerMap: DashboardPowerMap?,
    maxPowerHp: Int?,
    engineBrakingPercent: Int?,
    regenPercent: Int?,
) {
    val powerMapText = when (powerMap) {
        DashboardPowerMap.ENDURO -> stringResource(R.string.dashboard_power_map_enduro)
        DashboardPowerMap.UNKNOWN -> stringResource(R.string.dashboard_power_map_unknown)
        null -> stringResource(R.string.global_placeholder)
    }

    val maxPowerText = maxPowerHp?.let {
        stringResource(R.string.dashboard_format_max_power_hp, it)
    } ?: stringResource(R.string.global_placeholder)

    val brakingText = engineBrakingPercent?.let {
        stringResource(R.string.dashboard_format_braking, it)
    } ?: stringResource(R.string.global_placeholder)

    val regenText = regenPercent?.let {
        stringResource(R.string.dashboard_format_regen, it)
    } ?: stringResource(R.string.global_placeholder)

    Row(modifier = modifier.fillMaxWidth().padding(PANEL_PADDING)) {
        Text(
            text = stringResource(R.string.dashboard_settings_header),
            style = MaterialTheme.typography.titleLarge,
            color = TextPrimary,
        )
        Spacer(modifier = Modifier.width(METRICS_ROW_SPACING))
        SettingsMetricItem(
            label = stringResource(R.string.dashboard_label_power_map),
            value = powerMapText,
        )
        Spacer(modifier = Modifier.width(METRICS_ROW_SPACING))
        SettingsMetricItem(
            label = stringResource(R.string.dashboard_label_max_power),
            value = maxPowerText,
        )
        Spacer(modifier = Modifier.width(METRICS_ROW_SPACING))
        SettingsMetricItem(
            label = stringResource(R.string.dashboard_label_braking),
            value = brakingText,
        )
        Spacer(modifier = Modifier.width(METRICS_ROW_SPACING))
        SettingsMetricItem(
            label = stringResource(R.string.dashboard_label_regen),
            value = regenText,
        )
    }
}

@Composable
private fun SettingsMetricItem(label: String, value: String) {
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
private fun PreviewRideSettingsBarFull() {
    StarkFutureTechnicalAssessmentTheme {
        RideSettingsBar(
            powerMap = DashboardPowerMap.ENDURO,
            maxPowerHp = 80,
            engineBrakingPercent = 55,
            regenPercent = 25,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewRideSettingsBarUnknown() {
    StarkFutureTechnicalAssessmentTheme {
        RideSettingsBar(
            powerMap = DashboardPowerMap.UNKNOWN,
            maxPowerHp = 60,
            engineBrakingPercent = 30,
            regenPercent = 10,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewRideSettingsBarNull() {
    StarkFutureTechnicalAssessmentTheme {
        RideSettingsBar(
            powerMap = null,
            maxPowerHp = null,
            engineBrakingPercent = null,
            regenPercent = null,
        )
    }
}

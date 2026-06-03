package com.guidovezzoni.sfta.ui.screens.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.guidovezzoni.sfta.R
import com.guidovezzoni.sfta.ui.theme.DashboardSurface
import com.guidovezzoni.sfta.ui.theme.PowerBlue
import com.guidovezzoni.sfta.ui.theme.StarkFutureTechnicalAssessmentTheme
import com.guidovezzoni.sfta.ui.theme.TextPrimary
import com.guidovezzoni.sfta.ui.theme.TextSecondary

private val PANEL_PADDING = 16.dp
private val LABEL_VALUE_SPACING = 4.dp
private val METRICS_ROW_SPACING = 24.dp

@Composable
fun PowerPanel(
    modifier: Modifier = Modifier,
    motorPowerHp: Double?,
    motorTemperatureCelsius: Double?,
) {
    val powerText = motorPowerHp?.let {
        stringResource(R.string.dashboard_format_motor_power, it)
    } ?: stringResource(R.string.global_placeholder)

    val tempText = motorTemperatureCelsius?.let {
        stringResource(R.string.dashboard_format_temperature, it)
    } ?: stringResource(R.string.global_placeholder)

    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = DashboardSurface),
    ) {
        Column(modifier = Modifier.padding(PANEL_PADDING).fillMaxWidth()) {
            Text(
                text = stringResource(R.string.dashboard_power_header),
                style = MaterialTheme.typography.titleLarge,
                color = TextPrimary,
            )
            Spacer(modifier = Modifier.height(LABEL_VALUE_SPACING))
            Text(
                text = powerText,
                style = MaterialTheme.typography.displayLarge,
                color = PowerBlue,
            )
            Spacer(modifier = Modifier.height(LABEL_VALUE_SPACING))
            Row {
                PowerMetricItem(
                    label = stringResource(R.string.dashboard_label_motor_temp),
                    value = tempText,
                )
                Spacer(modifier = Modifier.width(METRICS_ROW_SPACING))
            }
        }
    }
}

@Composable
private fun PowerMetricItem(label: String, value: String) {
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
private fun PreviewPowerPanelFull() {
    StarkFutureTechnicalAssessmentTheme {
        PowerPanel(
            motorPowerHp = 52.4,
            motorTemperatureCelsius = 61.2,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewPowerPanelNull() {
    StarkFutureTechnicalAssessmentTheme {
        PowerPanel(
            motorPowerHp = null,
            motorTemperatureCelsius = null,
        )
    }
}

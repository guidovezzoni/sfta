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
import com.guidovezzoni.sfta.ui.theme.StarkFutureTechnicalAssessmentTheme
import com.guidovezzoni.sfta.ui.theme.TextPrimary
import com.guidovezzoni.sfta.ui.theme.TextSecondary

private val PANEL_PADDING = 16.dp
private val METRICS_ROW_SPACING = 32.dp

@Composable
fun SessionPanel(
    modifier: Modifier = Modifier,
    sessionDurationSeconds: Long?,
    sessionDistanceKm: Double?,
    sessionMaxSpeedKmh: Double?,
) {
    val durationText = sessionDurationSeconds?.let {
        val hours = (it / 3600L).toInt()
        val minutes = ((it % 3600L) / 60L).toInt()
        stringResource(R.string.dashboard_format_duration, hours, minutes)
    } ?: stringResource(R.string.global_placeholder)

    val distanceText = sessionDistanceKm?.let {
        stringResource(R.string.dashboard_format_distance, it)
    } ?: stringResource(R.string.global_placeholder)

    val maxSpeedText = sessionMaxSpeedKmh?.let {
        stringResource(R.string.dashboard_format_max_speed, it)
    } ?: stringResource(R.string.global_placeholder)

    Row(modifier = modifier.fillMaxWidth().padding(PANEL_PADDING)) {
        Text(
            text = stringResource(R.string.dashboard_session_header),
            style = MaterialTheme.typography.titleLarge,
            color = TextPrimary,
        )
        Spacer(modifier = Modifier.width(METRICS_ROW_SPACING))
        SessionMetricItem(
            label = stringResource(R.string.dashboard_label_duration),
            value = durationText,
        )
        Spacer(modifier = Modifier.width(METRICS_ROW_SPACING))
        SessionMetricItem(
            label = stringResource(R.string.dashboard_label_distance),
            value = distanceText,
        )
        Spacer(modifier = Modifier.width(METRICS_ROW_SPACING))
        SessionMetricItem(
            label = stringResource(R.string.dashboard_label_max_speed),
            value = maxSpeedText,
        )
    }
}

@Composable
private fun SessionMetricItem(label: String, value: String) {
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
private fun PreviewSessionPanelFull() {
    StarkFutureTechnicalAssessmentTheme {
        SessionPanel(
            sessionDurationSeconds = 3742L,
            sessionDistanceKm = 24.7,
            sessionMaxSpeedKmh = 94.1,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewSessionPanelNull() {
    StarkFutureTechnicalAssessmentTheme {
        SessionPanel(
            sessionDurationSeconds = null,
            sessionDistanceKm = null,
            sessionMaxSpeedKmh = null,
        )
    }
}

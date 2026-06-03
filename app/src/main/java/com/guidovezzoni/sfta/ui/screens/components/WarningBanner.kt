package com.guidovezzoni.sfta.ui.screens.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.guidovezzoni.sfta.R
import com.guidovezzoni.sfta.ui.state.DashboardWarningInfo
import com.guidovezzoni.sfta.ui.state.DashboardWarningSeverity
import com.guidovezzoni.sfta.ui.theme.StarkFutureTechnicalAssessmentTheme
import com.guidovezzoni.sfta.ui.theme.TextPrimary
import com.guidovezzoni.sfta.ui.theme.WarningAmber
import com.guidovezzoni.sfta.ui.theme.WarningRed

private val BANNER_PADDING = 12.dp

@Composable
fun WarningBanner(
    modifier: Modifier = Modifier,
    warnings: List<DashboardWarningInfo>,
) {
    val highestSeverity = warnings.mapNotNull { it.severity }.maxByOrNull { it.ordinal }
    val bannerColor: Color = when (highestSeverity) {
        DashboardWarningSeverity.UNKNOWN -> WarningRed
        DashboardWarningSeverity.WARNING -> WarningAmber
        null -> WarningAmber
    }

    val warningContentDescription = stringResource(R.string.dashboard_warning_content_description)

    LazyColumn(
        modifier = modifier
            .fillMaxWidth()
            .background(bannerColor)
            .semantics { contentDescription = warningContentDescription },
    ) {
        items(warnings) { warning ->
            warning.message?.let { message ->
                Text(
                    text = message,
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimary,
                    modifier = Modifier.padding(BANNER_PADDING),
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewWarningBannerWarning() {
    StarkFutureTechnicalAssessmentTheme {
        WarningBanner(
            warnings = listOf(
                DashboardWarningInfo(
                    code = "W001",
                    message = "Battery temperature high",
                    severity = DashboardWarningSeverity.WARNING,
                )
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewWarningBannerUnknown() {
    StarkFutureTechnicalAssessmentTheme {
        WarningBanner(
            warnings = listOf(
                DashboardWarningInfo(
                    code = "E001",
                    message = "Motor fault detected",
                    severity = DashboardWarningSeverity.UNKNOWN,
                )
            )
        )
    }
}

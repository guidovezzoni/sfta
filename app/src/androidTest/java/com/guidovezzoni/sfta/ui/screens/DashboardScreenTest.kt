package com.guidovezzoni.sfta.ui.screens

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.guidovezzoni.sfta.R
import com.guidovezzoni.sfta.ui.intent.DashboardUiIntent
import com.guidovezzoni.sfta.ui.state.DashboardChargingState
import com.guidovezzoni.sfta.ui.state.DashboardPowerMap
import com.guidovezzoni.sfta.ui.state.DashboardUiState
import com.guidovezzoni.sfta.ui.state.DashboardWarningInfo
import com.guidovezzoni.sfta.ui.state.DashboardWarningSeverity
import com.guidovezzoni.sfta.ui.theme.StarkFutureTechnicalAssessmentTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DashboardScreenTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    private val populatedState = DashboardUiState(
        isLoading = false,
        errorMessage = null,
        bikeName = "STARK VARG",
        batteryChargePercent = 73,
        batteryRangeKm = 38,
        batteryTemperatureCelsius = 34.7,
        chargingState = DashboardChargingState.DISCHARGING,
        motorPowerHp = 52.4,
        motorTemperatureCelsius = 61.2,
        powerMap = DashboardPowerMap.ENDURO,
        maxPowerHp = 80,
        engineBrakingPercent = 55,
        regenPercent = 25,
        sessionDurationSeconds = 3742L,
        sessionDistanceKm = 24.7,
        sessionMaxSpeedKmh = 94.1,
        warnings = emptyList(),
    )

    @Test
    fun givenLoadingState_whenScreenRenders_thenSpinnerIsDisplayed() {
        val loadingContentDescription = composeTestRule.activity.getString(
            R.string.dashboard_loading_content_description
        )
        composeTestRule.setContent {
            StarkFutureTechnicalAssessmentTheme {
                DashboardScreen(
                    uiState = DashboardUiState(isLoading = true),
                    onIntent = {},
                )
            }
        }

        composeTestRule.onNodeWithContentDescription(loadingContentDescription).assertIsDisplayed()
    }

    @Test
    fun givenErrorState_whenScreenRenders_thenErrorMessageAndRetryButtonVisible() {
        val retryText = composeTestRule.activity.getString(R.string.global_retry)
        val errorMessage = "Failed to load bike data"
        composeTestRule.setContent {
            StarkFutureTechnicalAssessmentTheme {
                DashboardScreen(
                    uiState = DashboardUiState(errorMessage = errorMessage),
                    onIntent = {},
                )
            }
        }

        composeTestRule.onNodeWithText(errorMessage).assertIsDisplayed()
        composeTestRule.onNodeWithText(retryText).assertIsDisplayed()
    }

    @Test
    fun givenErrorState_whenRetryTapped_thenRetryLoadIntentEmitted() {
        val retryText = composeTestRule.activity.getString(R.string.global_retry)
        val capturedIntents = mutableListOf<DashboardUiIntent>()
        composeTestRule.setContent {
            StarkFutureTechnicalAssessmentTheme {
                DashboardScreen(
                    uiState = DashboardUiState(errorMessage = "Error"),
                    onIntent = { capturedIntents.add(it) },
                )
            }
        }

        composeTestRule.onNodeWithText(retryText).performClick()

        val expectedIntent = DashboardUiIntent.RetryLoad
        assertEquals(expectedIntent, capturedIntents.last())
    }

    @Test
    fun givenPopulatedState_whenScreenRenders_thenBatteryAndPowerPanelsShowValues() {
        composeTestRule.setContent {
            StarkFutureTechnicalAssessmentTheme {
                DashboardScreen(uiState = populatedState, onIntent = {})
            }
        }

        composeTestRule.onNodeWithText("73%").assertIsDisplayed()
        composeTestRule.onNodeWithText("52.4 HP").assertIsDisplayed()
    }

    @Test
    fun givenPopulatedState_whenScreenRenders_thenSessionInfoShowsFormattedValues() {
        composeTestRule.setContent {
            StarkFutureTechnicalAssessmentTheme {
                DashboardScreen(uiState = populatedState, onIntent = {})
            }
        }

        composeTestRule.onNodeWithText("1h 2m").assertIsDisplayed()
        composeTestRule.onNodeWithText("24.7 km").assertIsDisplayed()
        composeTestRule.onNodeWithText("94.1 km/h").assertIsDisplayed()
    }

    @Test
    fun givenPopulatedState_whenScreenRenders_thenSettingsBarShowsAllValues() {
        val enduroText = composeTestRule.activity.getString(R.string.dashboard_power_map_enduro)
        composeTestRule.setContent {
            StarkFutureTechnicalAssessmentTheme {
                DashboardScreen(uiState = populatedState, onIntent = {})
            }
        }

        composeTestRule.onNodeWithText(enduroText).assertIsDisplayed()
        composeTestRule.onNodeWithText("80 HP").assertIsDisplayed()
        composeTestRule.onNodeWithText("55%").assertIsDisplayed()
        composeTestRule.onNodeWithText("25%").assertIsDisplayed()
    }

    @Test
    fun givenStateWithNullFields_whenScreenRenders_thenPlaceholdersShown() {
        val placeholderText = composeTestRule.activity.getString(R.string.global_placeholder)
        composeTestRule.setContent {
            StarkFutureTechnicalAssessmentTheme {
                DashboardScreen(uiState = DashboardUiState(), onIntent = {})
            }
        }

        composeTestRule.onAllNodesWithText(placeholderText).onFirst().assertIsDisplayed()
    }

    @Test
    fun givenStateWithWarnings_whenScreenRenders_thenWarningBannerVisible() {
        val warningState = populatedState.copy(
            warnings = listOf(
                DashboardWarningInfo(
                    code = "W001",
                    message = "Battery temperature high",
                    severity = DashboardWarningSeverity.WARNING,
                )
            )
        )
        composeTestRule.setContent {
            StarkFutureTechnicalAssessmentTheme {
                DashboardScreen(uiState = warningState, onIntent = {})
            }
        }

        composeTestRule.onNodeWithText("Battery temperature high").assertIsDisplayed()
    }

    @Test
    fun givenStateWithNoWarnings_whenScreenRenders_thenNoWarningBanner() {
        val warningContentDescription = composeTestRule.activity.getString(
            R.string.dashboard_warning_content_description
        )
        composeTestRule.setContent {
            StarkFutureTechnicalAssessmentTheme {
                DashboardScreen(
                    uiState = populatedState.copy(warnings = emptyList()),
                    onIntent = {},
                )
            }
        }

        composeTestRule.onNodeWithContentDescription(warningContentDescription).assertDoesNotExist()
    }
}

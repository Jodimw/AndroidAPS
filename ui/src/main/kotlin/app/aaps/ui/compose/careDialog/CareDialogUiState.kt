package app.aaps.ui.compose.careDialog

import androidx.compose.runtime.Immutable
import app.aaps.core.data.model.GlucoseUnit
import app.aaps.core.data.model.TE
import app.aaps.core.interfaces.ui.UiInteraction

@Immutable
data class CareDialogUiState(
    val eventType: UiInteraction.EventType = UiInteraction.EventType.BGCHECK,

    // BG section (visible for BGCHECK, QUESTION, ANNOUNCEMENT)
    val meterType: TE.MeterType = TE.MeterType.FINGER,
    val bgValue: Double = 0.0,

    // Duration section (visible for NOTE, EXERCISE)
    val duration: Double = 0.0,

    // Notes section
    val notes: String = "",

    // Date/Time (always visible)
    val eventTime: Long = System.currentTimeMillis(),
    val eventTimeChanged: Boolean = false,

    // Config values
    val glucoseUnits: GlucoseUnit = GlucoseUnit.MGDL,
    val showNotesFromPreferences: Boolean = false,
    val siteRotationManageCgm: Boolean = false
)

/** BG section visible for BGCHECK, QUESTION, ANNOUNCEMENT */
val CareDialogUiState.showBgSection: Boolean
    get() = eventType in setOf(
        UiInteraction.EventType.BGCHECK,
        UiInteraction.EventType.QUESTION,
        UiInteraction.EventType.ANNOUNCEMENT
    )

/** Duration section visible for NOTE, EXERCISE */
val CareDialogUiState.showDurationSection: Boolean
    get() = eventType in setOf(
        UiInteraction.EventType.NOTE,
        UiInteraction.EventType.EXERCISE
    )

/** Notes always visible for NOTE, QUESTION, ANNOUNCEMENT, EXERCISE (independent of prefs) */
val CareDialogUiState.showNotesSection: Boolean
    get() = eventType in setOf(
        UiInteraction.EventType.NOTE,
        UiInteraction.EventType.QUESTION,
        UiInteraction.EventType.ANNOUNCEMENT,
        UiInteraction.EventType.EXERCISE
    ) || showNotesFromPreferences

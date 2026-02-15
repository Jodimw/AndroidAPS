package app.aaps.ui.search

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import app.aaps.core.interfaces.ui.UiInteraction
import app.aaps.core.ui.compose.icons.Carbs
import app.aaps.core.ui.compose.icons.Prime
import app.aaps.core.ui.compose.icons.TempTarget
import app.aaps.core.ui.compose.icons.Treatment
import app.aaps.core.ui.search.SearchableItem
import app.aaps.core.ui.search.SearchableProvider
import javax.inject.Inject
import javax.inject.Singleton
import app.aaps.core.ui.R as CoreUiR
import app.aaps.core.objects.R as CoreObjectsR

/**
 * Provides searchable items for dialogs and action screens.
 * These are navigable screens/dialogs accessible from search results.
 */
@Singleton
class DialogSearchables @Inject constructor() : SearchableProvider {

    override fun getSearchableItems(): List<SearchableItem> = buildList {
        // Drawer menu screens
        add(
            SearchableItem.Dialog(
                dialogKey = "treatments",
                dialogTitleResId = CoreUiR.string.treatments,
                dialogIconResId = CoreObjectsR.drawable.ic_treatments,
                dialogSummaryResId = CoreUiR.string.treatments_desc
            )
        )
        add(
            SearchableItem.Dialog(
                dialogKey = "stats",
                dialogTitleResId = app.aaps.ui.R.string.statistics,
                dialogIconResId = CoreUiR.drawable.ic_stats,
                dialogSummaryResId = app.aaps.ui.R.string.statistics_desc
            )
        )
        add(
            SearchableItem.Dialog(
                dialogKey = "profile_helper",
                dialogTitleResId = app.aaps.ui.R.string.nav_profile_helper,
                dialogIconResId = CoreUiR.drawable.ic_home_profile,
                dialogSummaryResId = app.aaps.ui.R.string.nav_profile_helper_desc
            )
        )
        add(
            SearchableItem.Dialog(
                dialogKey = "history_browser",
                dialogTitleResId = CoreUiR.string.nav_history_browser,
                dialogIconResId = CoreUiR.drawable.ic_pump_history,
                dialogSummaryResId = CoreUiR.string.nav_history_browser_desc
            )
        )
        add(
            SearchableItem.Dialog(
                dialogKey = "setup_wizard",
                dialogTitleResId = CoreUiR.string.nav_setupwizard,
                dialogIconResId = CoreUiR.drawable.ic_settings,
                dialogSummaryResId = CoreUiR.string.nav_setupwizard_desc
            )
        )
        add(
            SearchableItem.Dialog(
                dialogKey = "about",
                dialogTitleResId = CoreUiR.string.nav_about,
                dialogIcon = Icons.Default.Info,
                dialogSummaryResId = CoreUiR.string.nav_about_desc
            )
        )

        // Action screens (from ManageBottomSheet)
        add(
            SearchableItem.Dialog(
                dialogKey = "running_mode",
                dialogTitleResId = CoreUiR.string.running_mode,
                dialogIconResId = CoreObjectsR.drawable.ic_loop_closed
            )
        )
        add(
            SearchableItem.Dialog(
                dialogKey = "temp_target_management",
                dialogTitleResId = CoreUiR.string.temp_target_management,
                dialogIcon = TempTarget,
                dialogSummaryResId = CoreUiR.string.manage_temp_target_desc
            )
        )
        add(
            SearchableItem.Dialog(
                dialogKey = "quick_wizard_management",
                dialogTitleResId = CoreUiR.string.quickwizard_managemnt,
                dialogIconResId = CoreObjectsR.drawable.ic_quick_wizard,
                dialogSummaryResId = CoreUiR.string.manage_quickwizard_desc
            )
        )

        // Dialogs (from TreatmentBottomSheet)
        add(
            SearchableItem.Dialog(
                dialogKey = "carbs_dialog",
                dialogTitleResId = CoreUiR.string.carbs,
                dialogIcon = Carbs,
                dialogSummaryResId = CoreUiR.string.treatment_carbs_desc
            )
        )
        add(
            SearchableItem.Dialog(
                dialogKey = "insulin_dialog",
                dialogTitleResId = CoreUiR.string.overview_insulin_label,
                dialogIcon = Treatment,
                dialogSummaryResId = CoreUiR.string.treatment_insulin_desc
            )
        )
        add(
            SearchableItem.Dialog(
                dialogKey = "treatment_dialog",
                dialogTitleResId = CoreUiR.string.overview_treatment_label,
                dialogIcon = Treatment,
                dialogSummaryResId = CoreUiR.string.treatment_desc
            )
        )
        add(
            SearchableItem.Dialog(
                dialogKey = "fill_dialog",
                dialogTitleResId = CoreUiR.string.prime_fill,
                dialogIcon = Prime
            )
        )

        // CareDialog events
        addCareDialogEvents()
    }

    private fun MutableList<SearchableItem>.addCareDialogEvents() {
        add(
            SearchableItem.Dialog(
                dialogKey = "care_${UiInteraction.EventType.BGCHECK.name.lowercase()}",
                dialogTitleResId = CoreUiR.string.careportal_bgcheck,
                dialogIconResId = CoreObjectsR.drawable.ic_cp_bgcheck
            )
        )
        add(
            SearchableItem.Dialog(
                dialogKey = "care_${UiInteraction.EventType.SENSOR_INSERT.name.lowercase()}",
                dialogTitleResId = CoreUiR.string.cgm_sensor_insert,
                dialogIconResId = CoreObjectsR.drawable.ic_cp_cgm_insert
            )
        )
        add(
            SearchableItem.Dialog(
                dialogKey = "care_${UiInteraction.EventType.BATTERY_CHANGE.name.lowercase()}",
                dialogTitleResId = CoreUiR.string.pump_battery_change,
                dialogIconResId = CoreObjectsR.drawable.ic_cp_pump_battery
            )
        )
        add(
            SearchableItem.Dialog(
                dialogKey = "care_${UiInteraction.EventType.NOTE.name.lowercase()}",
                dialogTitleResId = CoreUiR.string.careportal_note,
                dialogIconResId = CoreObjectsR.drawable.ic_cp_note
            )
        )
        add(
            SearchableItem.Dialog(
                dialogKey = "care_${UiInteraction.EventType.EXERCISE.name.lowercase()}",
                dialogTitleResId = CoreUiR.string.careportal_exercise,
                dialogIconResId = CoreObjectsR.drawable.ic_cp_exercise
            )
        )
        add(
            SearchableItem.Dialog(
                dialogKey = "care_${UiInteraction.EventType.QUESTION.name.lowercase()}",
                dialogTitleResId = CoreUiR.string.careportal_question,
                dialogIconResId = CoreObjectsR.drawable.ic_cp_question
            )
        )
        add(
            SearchableItem.Dialog(
                dialogKey = "care_${UiInteraction.EventType.ANNOUNCEMENT.name.lowercase()}",
                dialogTitleResId = CoreUiR.string.careportal_announcement,
                dialogIconResId = CoreObjectsR.drawable.ic_cp_announcement
            )
        )
    }
}

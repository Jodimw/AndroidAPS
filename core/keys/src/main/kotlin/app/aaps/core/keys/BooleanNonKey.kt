package app.aaps.core.keys

import app.aaps.core.keys.interfaces.BooleanNonPreferenceKey

@Suppress("SpellCheckingInspection")
enum class BooleanNonKey(
    override val key: String,
    override val defaultValue: Boolean,
    override val exportable: Boolean = true
) : BooleanNonPreferenceKey {

    GeneralSetupWizardProcessed("startupwizard_processed", false),
    SetupWizardIUnderstand("I_understand", false),
    ObjectivesLoopUsed("ObjectivesLoopUsed", false),
    ObjectivesActionsUsed("ObjectivesActionsUsed", false),
    ObjectivesScaleUsed("ObjectivesScaleUsed", false),
    ObjectivesPumpStatusIsAvailableInNS("ObjectivespumpStatusIsAvailableInNS", false),
    ObjectivesBgIsAvailableInNs("ObjectivesbgIsAvailableInNS", false),
    ObjectivesProfileSwitchUsed("ObjectivesProfileSwitchUsed", false),
    ObjectivesDisconnectUsed("ObjectivesDisconnectUsed", false),
    ObjectivesReconnectUsed("ObjectivesReconnectUsed", false),
    ObjectivesTempTargetUsed("ObjectivesTempTargetUsed", false),
    AutosensUsedOnMainPhone("used_autosens_on_main_phone", false),

    // Export destination settings (managed by ExportOptionsDialog, no preferences UI)
    ExportAllCloudEnabled("export_all_cloud_enabled", false),
    ExportLogEmailEnabled("export_log_email_enabled", true),
    ExportLogCloudEnabled("export_log_cloud_enabled", false),
    ExportSettingsLocalEnabled("export_settings_local_enabled", true),
    ExportSettingsCloudEnabled("export_settings_cloud_enabled", false),
    ExportCsvLocalEnabled("export_csv_local_enabled", true),
    ExportCsvCloudEnabled("export_csv_cloud_enabled", false),
}
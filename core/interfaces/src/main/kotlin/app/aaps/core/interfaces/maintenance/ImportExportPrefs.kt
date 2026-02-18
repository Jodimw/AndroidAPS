package app.aaps.core.interfaces.maintenance

import android.content.Context
import androidx.fragment.app.FragmentActivity
import app.aaps.core.interfaces.rx.weardata.CwfData
import org.json.JSONObject

/** Where to send the export. */
enum class ExportDestination { LOCAL, CLOUD, BOTH }

/**
 * Snapshot of the current export-options configuration.
 * Read once, then used to drive UI labels and export routing.
 */
data class ExportConfig(
    val isCloudActive: Boolean,
    val isCloudError: Boolean,
    val settingsLocal: Boolean,
    val settingsCloud: Boolean,
    val logEmail: Boolean,
    val logCloud: Boolean,
    val csvLocal: Boolean,
    val csvCloud: Boolean,
    val cloudDisplayName: String?
)

/**
 * Per-destination result of an export.
 * `null` means the destination was not attempted.
 */
data class ExportResult(
    val localSuccess: Boolean? = null,
    val cloudSuccess: Boolean? = null
)

/**
 * Result of preparing an export: file is ready, and optionally a cached password is available.
 */
data class ExportPreparation(
    val fileName: String,
    val cachedPassword: String?,
    val destination: ExportDestination = ExportDestination.LOCAL,
    val cloudDisplayName: String? = null
)

interface ImportExportPrefs {

    fun doImportSharedPreferences(activity: FragmentActivity)
    fun importSharedPreferences(activity: FragmentActivity)
    fun importCustomWatchface(activity: FragmentActivity)
    fun exportCustomWatchface(customWatchface: CwfData, withDate: Boolean = true)
    fun prefsFileExists(): Boolean
    fun exportSharedPreferences(activity: FragmentActivity)
    fun exportSharedPreferencesNonInteractive(context: Context, password: String): Boolean
    fun exportUserEntriesCsv(context: Context)
    fun exportApsResult(algorithm: String?, input: JSONObject, output: JSONObject?)

    // Compose export support — discrete steps, no UI

    /** Check if master password has been configured */
    fun isMasterPasswordSet(): Boolean

    /** Prepare export file and check for cached password. Returns null if file creation fails. */
    fun prepareExport(): ExportPreparation?

    /** Execute the actual export with the given password. Returns per-destination results. */
    suspend fun executeExport(password: String): ExportResult

    /** Cache the password for future exports. Returns the (possibly transformed) password to use. */
    fun cacheExportPassword(password: String): String

    /** Get a snapshot of the current export configuration. */
    fun getExportConfig(): ExportConfig

    /** Toggle export destination preferences (for inline FilterChips). */
    fun setSettingsLocalEnabled(enabled: Boolean)
    fun setSettingsCloudEnabled(enabled: Boolean)
    fun setLogEmailEnabled(enabled: Boolean)
    fun setLogCloudEnabled(enabled: Boolean)
    fun setCsvLocalEnabled(enabled: Boolean)
    fun setCsvCloudEnabled(enabled: Boolean)

    /**
     * Store for selected file from UI
     */
    var selectedImportFile: PrefsFile?
}
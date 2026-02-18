package app.aaps.core.interfaces.maintenance

import android.content.Context
import androidx.fragment.app.FragmentActivity
import app.aaps.core.interfaces.rx.weardata.CwfData
import org.json.JSONObject

/**
 * Result of preparing an export: file is ready, and optionally a cached password is available.
 */
data class ExportPreparation(
    val fileName: String,
    val cachedPassword: String?
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

    /** Execute the actual export with the given password. Returns true on success. */
    fun executeExport(password: String): Boolean

    /** Cache the password for future exports. Returns the (possibly transformed) password to use. */
    fun cacheExportPassword(password: String): String

    /**
     * Store for selected file from UI
     */
    var selectedImportFile: PrefsFile?
}
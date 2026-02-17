package app.aaps.ui.compose.management

import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.aaps.core.data.ue.Action
import app.aaps.core.data.ue.Sources
import app.aaps.core.interfaces.db.PersistenceLayer
import app.aaps.core.interfaces.iob.IobCobCalculator
import app.aaps.core.interfaces.logging.AAPSLogger
import app.aaps.core.interfaces.logging.L
import app.aaps.core.interfaces.logging.LTag
import app.aaps.core.interfaces.logging.LogElement
import app.aaps.core.interfaces.logging.UserEntryLogger
import app.aaps.core.interfaces.maintenance.Maintenance
import app.aaps.core.interfaces.resources.ResourceHelper
import app.aaps.core.interfaces.utils.fabric.FabricPrivacy
import app.aaps.core.interfaces.overview.OverviewData
import app.aaps.core.interfaces.overview.graph.OverviewDataCache
import app.aaps.core.interfaces.plugin.ActivePlugin
import app.aaps.core.interfaces.plugin.OwnDatabasePlugin
import app.aaps.core.interfaces.pump.PumpSync
import app.aaps.core.interfaces.rx.bus.RxBus
import app.aaps.core.interfaces.rx.events.EventPreferenceChange
import app.aaps.core.interfaces.sync.DataSyncSelectorXdrip
import app.aaps.core.keys.StringKey
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import app.aaps.core.ui.R as CoreUiR

sealed interface MaintenanceEvent {
    data object RecreateActivity : MaintenanceEvent
    data class CleanupResult(val result: String) : MaintenanceEvent
    data class Snackbar(val message: String) : MaintenanceEvent
    data class Error(val message: String) : MaintenanceEvent
}

@Stable
class MaintenanceViewModel @Inject constructor(
    private val aapsLogger: AAPSLogger,
    private val rh: ResourceHelper,
    private val l: L,
    private val maintenance: Maintenance,
    private val activePlugin: ActivePlugin,
    private val persistenceLayer: PersistenceLayer,
    private val fabricPrivacy: FabricPrivacy,
    private val uel: UserEntryLogger,
    private val dataSyncSelectorXdrip: DataSyncSelectorXdrip,
    private val pumpSync: PumpSync,
    private val iobCobCalculator: IobCobCalculator,
    private val overviewData: OverviewData,
    private val overviewDataCache: OverviewDataCache,
    private val rxBus: RxBus
) : ViewModel() {

    private val _events = MutableSharedFlow<MaintenanceEvent>()
    val events: SharedFlow<MaintenanceEvent> = _events

    // Log elements for LogSettingBottomSheet
    val logElements: List<LogElement> get() = l.logElements()

    fun toggleLogElement(element: LogElement, enabled: Boolean) {
        element.enable(enabled)
    }

    fun resetLogDefaults() {
        l.resetToDefaults()
    }

    // Log actions

    fun sendLogs() {
        maintenance.sendLogs()
    }

    fun deleteLogs() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                maintenance.deleteLogs(5)
                uel.log(Action.DELETE_LOGS, Sources.Maintenance)
                _events.emit(MaintenanceEvent.Snackbar(rh.gs(CoreUiR.string.logs_deleted)))
            } catch (e: Exception) {
                aapsLogger.error("Error deleting logs", e)
                fabricPrivacy.logException(e)
            }
        }
    }

    // Database actions

    fun resetApsResults() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                persistenceLayer.clearApsResults()
                aapsLogger.debug("Aps results cleared")
            } catch (e: Exception) {
                aapsLogger.error("Error clearing aps results", e)
            }
        }
        uel.log(Action.RESET_APS_RESULTS, Sources.Maintenance)
    }

    fun cleanupDatabases() {
        viewModelScope.launch {
            try {
                val result = withContext(Dispatchers.IO) {
                    persistenceLayer.cleanupDatabase(93, deleteTrackedChanges = true)
                }
                if (result.isNotEmpty()) {
                    _events.emit(MaintenanceEvent.CleanupResult(result))
                }
                aapsLogger.info(LTag.CORE, "Cleaned up databases with result: $result")
            } catch (e: Exception) {
                aapsLogger.error("Error cleaning up databases", e)
            }
        }
        uel.log(Action.CLEANUP_DATABASES, Sources.Maintenance)
    }

    fun resetDatabases() {
        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    persistenceLayer.clearDatabases()
                    for (plugin in activePlugin.getSpecificPluginsListByInterface(OwnDatabasePlugin::class.java)) {
                        (plugin as OwnDatabasePlugin).clearAllTables()
                    }
                    activePlugin.activeNsClient?.dataSyncSelector?.resetToNextFullSync()
                    dataSyncSelectorXdrip.resetToNextFullSync()
                    pumpSync.connectNewPump()
                    overviewData.reset()
                    overviewDataCache.reset()
                    iobCobCalculator.ads.reset()
                    iobCobCalculator.clearCache()
                }
                rxBus.send(EventPreferenceChange(StringKey.GeneralUnits.key))
                _events.emit(MaintenanceEvent.RecreateActivity)
            } catch (e: Exception) {
                aapsLogger.error("Error clearing databases", e)
            }
        }
        uel.log(Action.RESET_DATABASES, Sources.Maintenance)
    }

    // Export/Import (logged only, execution handled by caller with context)

    fun logSelectDirectory() {
        uel.log(Action.SELECT_DIRECTORY, Sources.Maintenance)
    }

    fun emitError(message: String) {
        viewModelScope.launch {
            _events.emit(MaintenanceEvent.Error(message))
        }
    }

    fun logExportSettings() {
        uel.log(Action.EXPORT_SETTINGS, Sources.Maintenance)
    }

    fun logImportSettings() {
        uel.log(Action.IMPORT_SETTINGS, Sources.Maintenance)
    }

    fun logExportCsv() {
        uel.log(Action.EXPORT_CSV, Sources.Maintenance)
    }
}

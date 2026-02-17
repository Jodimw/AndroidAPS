package app.aaps.ui.compose.management

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.TableChart
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import app.aaps.core.ui.compose.TonalIcon
import app.aaps.core.ui.R as CoreUiR

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MaintenanceBottomSheet(
    onDismiss: () -> Unit,
    onLogSettingsClick: () -> Unit,
    onSendLogsClick: () -> Unit,
    onDeleteLogsClick: () -> Unit,
    onDirectoryClick: () -> Unit,
    onExportSettingsClick: () -> Unit,
    onImportSettingsClick: () -> Unit,
    onExportCsvClick: () -> Unit,
    onResetApsResultsClick: () -> Unit,
    onCleanupDbClick: () -> Unit,
    onResetDbClick: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        MaintenanceBottomSheetContent(
            onDismiss = onDismiss,
            onLogSettingsClick = onLogSettingsClick,
            onSendLogsClick = onSendLogsClick,
            onDeleteLogsClick = onDeleteLogsClick,
            onDirectoryClick = onDirectoryClick,
            onExportSettingsClick = onExportSettingsClick,
            onImportSettingsClick = onImportSettingsClick,
            onExportCsvClick = onExportCsvClick,
            onResetApsResultsClick = onResetApsResultsClick,
            onCleanupDbClick = onCleanupDbClick,
            onResetDbClick = onResetDbClick
        )
    }
}

@Composable
internal fun MaintenanceBottomSheetContent(
    onDismiss: () -> Unit = {},
    onLogSettingsClick: () -> Unit = {},
    onSendLogsClick: () -> Unit = {},
    onDeleteLogsClick: () -> Unit = {},
    onDirectoryClick: () -> Unit = {},
    onExportSettingsClick: () -> Unit = {},
    onImportSettingsClick: () -> Unit = {},
    onExportCsvClick: () -> Unit = {},
    onResetApsResultsClick: () -> Unit = {},
    onCleanupDbClick: () -> Unit = {},
    onResetDbClick: () -> Unit = {}
) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val errorColor = MaterialTheme.colorScheme.error

    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .padding(bottom = 24.dp)
    ) {
        // Section: Log Files
        SectionHeader(stringResource(CoreUiR.string.log_files))

        MaintenanceItem(
            text = stringResource(CoreUiR.string.nav_logsettings),
            icon = Icons.Default.Settings,
            color = primaryColor,
            onDismiss = onDismiss,
            onClick = onLogSettingsClick
        )
        MaintenanceItem(
            text = stringResource(CoreUiR.string.send_all_logs),
            icon = Icons.Default.Send,
            color = primaryColor,
            onDismiss = onDismiss,
            onClick = onSendLogsClick
        )
        MaintenanceItem(
            text = stringResource(CoreUiR.string.delete_logs),
            icon = Icons.Default.Delete,
            color = primaryColor,
            onDismiss = onDismiss,
            onClick = onDeleteLogsClick
        )

        // Section: Settings
        HorizontalDivider(modifier = Modifier.padding(start = 56.dp))
        SectionHeader(stringResource(CoreUiR.string.settings))

        MaintenanceItem(
            text = stringResource(CoreUiR.string.aaps_directory),
            icon = Icons.Default.Folder,
            color = primaryColor,
            onDismiss = onDismiss,
            onClick = onDirectoryClick
        )
        MaintenanceItem(
            text = stringResource(CoreUiR.string.nav_export),
            icon = Icons.Default.FileUpload,
            color = primaryColor,
            onDismiss = onDismiss,
            onClick = onExportSettingsClick
        )
        MaintenanceItem(
            text = stringResource(CoreUiR.string.import_setting),
            icon = Icons.Default.FileDownload,
            color = primaryColor,
            onDismiss = onDismiss,
            onClick = onImportSettingsClick
        )

        // Section: Miscellaneous
        HorizontalDivider(modifier = Modifier.padding(start = 56.dp))
        SectionHeader(stringResource(CoreUiR.string.miscellaneous))

        MaintenanceItem(
            text = stringResource(CoreUiR.string.ue_export_to_csv),
            icon = Icons.Default.TableChart,
            color = primaryColor,
            onDismiss = onDismiss,
            onClick = onExportCsvClick
        )
        MaintenanceItem(
            text = stringResource(CoreUiR.string.database_cleanup),
            icon = Icons.Default.Delete,
            color = primaryColor,
            onDismiss = onDismiss,
            onClick = onCleanupDbClick
        )
        MaintenanceItem(
            text = stringResource(CoreUiR.string.reset_aps_results),
            icon = Icons.Default.DeleteForever,
            color = errorColor,
            onDismiss = onDismiss,
            onClick = onResetApsResultsClick
        )
        MaintenanceItem(
            text = stringResource(CoreUiR.string.nav_resetdb),
            icon = Icons.Default.DeleteForever,
            color = errorColor,
            onDismiss = onDismiss,
            onClick = onResetDbClick
        )
    }
}

@Composable
private fun SectionHeader(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp)
    )
}

@Composable
private fun MaintenanceItem(
    text: String,
    icon: ImageVector,
    color: Color,
    onDismiss: () -> Unit,
    onClick: () -> Unit
) {
    ListItem(
        headlineContent = { Text(text = text, color = color) },
        leadingContent = {
            TonalIcon(painter = rememberVectorPainter(icon), color = color)
        },
        colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.clickable {
            onDismiss()
            onClick()
        }
    )
}

@Preview(showBackground = true)
@Composable
private fun MaintenanceBottomSheetContentPreview() {
    MaterialTheme {
        MaintenanceBottomSheetContent()
    }
}

package app.aaps.ui.compose.overview

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import app.aaps.core.data.model.RM
import app.aaps.ui.compose.actions.StatusItem
import app.aaps.ui.compose.actions.StatusSectionContent
import app.aaps.ui.compose.actions.statusLevelToColor
import app.aaps.ui.compose.actions.viewmodels.ActionsViewModel
import app.aaps.ui.compose.graphs.viewmodels.GraphViewModel
import app.aaps.ui.compose.main.TempTargetChipState

@Composable
fun OverviewScreen(
    profileName: String,
    isProfileModified: Boolean,
    profileProgress: Float,
    tempTargetText: String,
    tempTargetState: TempTargetChipState,
    tempTargetProgress: Float,
    tempTargetReason: String?,
    runningMode: RM.Mode,
    runningModeText: String,
    runningModeProgress: Float,
    graphViewModel: GraphViewModel,
    actionsViewModel: ActionsViewModel,
    onProfileManagementClick: () -> Unit,
    onTempTargetClick: () -> Unit,
    onRunningModeClick: () -> Unit,
    onSensorInsertClick: () -> Unit,
    onFillClick: () -> Unit,
    onInsulinChangeClick: () -> Unit,
    onBatteryChangeClick: () -> Unit,
    paddingValues: PaddingValues,
    modifier: Modifier = Modifier
) {
    // Collect BG info state from ViewModel
    val bgInfoState by graphViewModel.bgInfoState.collectAsState()
    val actionsState by actionsViewModel.uiState.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(paddingValues)
            .verticalScroll(rememberScrollState())
    ) {
        // BG Info and Chips in a row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // BG Info section on the left
            BgInfoSection(
                bgInfo = bgInfoState.bgInfo,
                timeAgoText = bgInfoState.timeAgoText
            )

            // Chips column on the right
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Running mode chip
                if (runningModeText.isNotEmpty()) {
                    RunningModeChip(
                        mode = runningMode,
                        text = runningModeText,
                        progress = runningModeProgress,
                        onClick = onRunningModeClick
                    )
                }
                // Profile chip
                if (profileName.isNotEmpty()) {
                    ProfileChip(
                        profileName = profileName,
                        isModified = isProfileModified,
                        progress = profileProgress,
                        onClick = onProfileManagementClick
                    )
                }
                // TempTarget chip (show when text is available)
                if (tempTargetText.isNotEmpty()) {
                    TempTargetChip(
                        targetText = tempTargetText,
                        state = tempTargetState,
                        progress = tempTargetProgress,
                        reason = tempTargetReason,
                        onClick = onTempTargetClick
                    )
                }
            }
        }

        // Status section with expand/collapse
        OverviewStatusSection(
            sensorStatus = actionsState.sensorStatus,
            insulinStatus = actionsState.insulinStatus,
            cannulaStatus = actionsState.cannulaStatus,
            batteryStatus = actionsState.batteryStatus,
            showFill = actionsState.showFill,
            showPumpBatteryChange = actionsState.showPumpBatteryChange,
            onSensorInsertClick = onSensorInsertClick,
            onFillClick = onFillClick,
            onInsulinChangeClick = onInsulinChangeClick,
            onBatteryChangeClick = onBatteryChangeClick
        )

        // Graph content - New Compose/Vico graphs
        OverviewGraphsSection(graphViewModel = graphViewModel)
    }
}

@Composable
private fun OverviewStatusSection(
    sensorStatus: StatusItem?,
    insulinStatus: StatusItem?,
    cannulaStatus: StatusItem?,
    batteryStatus: StatusItem?,
    showFill: Boolean,
    showPumpBatteryChange: Boolean,
    onSensorInsertClick: () -> Unit,
    onFillClick: () -> Unit,
    onInsulinChangeClick: () -> Unit,
    onBatteryChangeClick: () -> Unit
) {
    val items = listOfNotNull(sensorStatus, insulinStatus, cannulaStatus, batteryStatus)
    if (items.isEmpty()) return

    var expanded by rememberSaveable { mutableStateOf(false) }

    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 1.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header row — clickable to toggle
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = !expanded },
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (expanded) {
                    Text(
                        text = stringResource(app.aaps.core.ui.R.string.status),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.weight(1f))
                } else {
                    Spacer(modifier = Modifier.weight(1f))
                    CompactStatusItems(items = items)
                    Spacer(modifier = Modifier.weight(1f))
                }

                Icon(
                    imageVector = if (expanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Expanded: full status rows with action buttons
            AnimatedVisibility(
                visible = expanded,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    StatusSectionContent(
                        sensorStatus = sensorStatus,
                        insulinStatus = insulinStatus,
                        cannulaStatus = cannulaStatus,
                        batteryStatus = batteryStatus,
                        onSensorInsertClick = onSensorInsertClick,
                        onFillClick = if (showFill) onFillClick else null,
                        onInsulinChangeClick = if (showFill) onInsulinChangeClick else null,
                        onBatteryChangeClick = if (showPumpBatteryChange) onBatteryChangeClick else null
                    )
                }
            }
        }
    }
}

@Composable
private fun CompactStatusItems(items: List<StatusItem>) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        items.forEach { item ->
            CompactStatusItem(item = item)
        }
    }
}

@Composable
private fun CompactStatusItem(item: StatusItem) {
    val ageColor = statusLevelToColor(item.ageStatus)

    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = item.iconRes),
            contentDescription = item.label,
            modifier = Modifier.size(24.dp),
            tint = ageColor
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = buildString {
                append(item.age)
                if (item.level != null) {
                    append(" ")
                    append(item.level)
                }
            },
            style = MaterialTheme.typography.bodyMedium,
            color = ageColor
        )
    }
}

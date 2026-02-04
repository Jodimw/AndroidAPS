package app.aaps.ui.compose.overview

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.aaps.core.data.model.RM
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
    onProfileManagementClick: () -> Unit,
    onTempTargetClick: () -> Unit,
    paddingValues: PaddingValues,
    modifier: Modifier = Modifier
) {
    // Collect BG info state from ViewModel
    val bgInfoState by graphViewModel.bgInfoState.collectAsState()

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
                        progress = runningModeProgress
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

        // Graph content - New Compose/Vico graphs
        OverviewGraphsSection(graphViewModel = graphViewModel)
    }
}

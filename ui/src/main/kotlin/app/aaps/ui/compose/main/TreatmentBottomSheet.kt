package app.aaps.ui.compose.main

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import app.aaps.core.interfaces.configuration.Config
import app.aaps.core.keys.BooleanKey
import app.aaps.core.keys.interfaces.Preferences
import app.aaps.core.ui.compose.AapsTheme
import app.aaps.core.ui.compose.icons.Calculator
import app.aaps.core.ui.compose.icons.Carbs
import app.aaps.core.ui.compose.icons.Treatment
import app.aaps.core.ui.compose.preference.AdaptivePreferenceList
import app.aaps.core.ui.compose.preference.PreferenceSubScreenDef
import app.aaps.core.ui.compose.preference.ProvidePreferenceTheme
import app.aaps.core.ui.R as CoreUiR

private val treatmentButtonSettingsDef = PreferenceSubScreenDef(
    key = "treatment_button_settings",
    titleResId = CoreUiR.string.settings,
    items = listOf(
        BooleanKey.OverviewShowTreatmentButton,
        BooleanKey.OverviewShowInsulinButton,
        BooleanKey.OverviewShowCarbsButton,
        BooleanKey.OverviewShowWizardButton
    )
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TreatmentBottomSheet(
    onDismiss: () -> Unit,
    onCarbsClick: () -> Unit,
    onInsulinClick: (() -> Unit)? = null,
    onCalculatorClick: (() -> Unit)? = null,
    simpleMode: Boolean = false,
    preferences: Preferences? = null,
    config: Config? = null
) {
    val sheetState = rememberModalBottomSheetState()
    var showSettings by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        if (showSettings && preferences != null && config != null) {
            TreatmentSettingsContent(
                preferences = preferences,
                config = config,
                onBack = { showSettings = false }
            )
        } else {
            TreatmentSelectionContent(
                onDismiss = onDismiss,
                onCarbsClick = onCarbsClick,
                onInsulinClick = onInsulinClick,
                onCalculatorClick = onCalculatorClick,
                showSettingsIcon = !simpleMode && preferences != null && config != null,
                onSettingsClick = { showSettings = true }
            )
        }
    }
}

@Composable
private fun TreatmentSelectionContent(
    onDismiss: () -> Unit,
    onCarbsClick: () -> Unit,
    onInsulinClick: (() -> Unit)?,
    onCalculatorClick: (() -> Unit)?,
    showSettingsIcon: Boolean,
    onSettingsClick: () -> Unit
) {
    Column(modifier = Modifier.padding(bottom = 24.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(CoreUiR.string.treatments),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.weight(1f)
            )
            if (showSettingsIcon) {
                IconButton(
                    onClick = onSettingsClick,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = stringResource(CoreUiR.string.settings),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        val disabledAlpha = 0.38f

        // Treatment (not migrated yet)
        val treatmentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = disabledAlpha)
        ListItem(
            headlineContent = {
                Text(
                    text = stringResource(CoreUiR.string.overview_treatment_label),
                    color = treatmentColor
                )
            },
            supportingContent = {
                Text(
                    text = stringResource(CoreUiR.string.treatment_desc),
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = disabledAlpha)
                )
            },
            leadingContent = {
                TonalIcon(
                    imageVector = Icons.Default.Add,
                    color = treatmentColor,
                    enabled = false
                )
            },
            colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.surface)
        )

        // Insulin (disabled for now)
        val insulinEnabled = onInsulinClick != null
        val insulinColor = AapsTheme.elementColors.insulin
        ListItem(
            headlineContent = {
                Text(
                    text = stringResource(CoreUiR.string.overview_insulin_label),
                    color = if (insulinEnabled) insulinColor
                    else MaterialTheme.colorScheme.onSurface.copy(alpha = disabledAlpha)
                )
            },
            supportingContent = {
                Text(
                    text = stringResource(CoreUiR.string.treatment_insulin_desc),
                    color = if (insulinEnabled) MaterialTheme.colorScheme.onSurfaceVariant
                    else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = disabledAlpha)
                )
            },
            leadingContent = {
                TonalIcon(
                    imageVector = Treatment,
                    color = if (insulinEnabled) insulinColor
                    else MaterialTheme.colorScheme.onSurface.copy(alpha = disabledAlpha),
                    enabled = insulinEnabled
                )
            },
            colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = if (insulinEnabled) Modifier.clickable {
                onDismiss()
                onInsulinClick!!()
            } else Modifier
        )

        // Carbs (active)
        val carbsColor = AapsTheme.elementColors.carbs
        ListItem(
            headlineContent = {
                Text(
                    text = stringResource(CoreUiR.string.carbs),
                    color = carbsColor
                )
            },
            supportingContent = {
                Text(
                    text = stringResource(CoreUiR.string.treatment_carbs_desc),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            leadingContent = {
                TonalIcon(
                    imageVector = Carbs,
                    color = carbsColor,
                    enabled = true
                )
            },
            colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.clickable {
                onDismiss()
                onCarbsClick()
            }
        )

        // Calculator (disabled for now)
        val calculatorEnabled = onCalculatorClick != null
        val calculatorColor = AapsTheme.generalColors.calculator
        ListItem(
            headlineContent = {
                Text(
                    text = stringResource(CoreUiR.string.boluswizard),
                    color = if (calculatorEnabled) calculatorColor
                    else MaterialTheme.colorScheme.onSurface.copy(alpha = disabledAlpha)
                )
            },
            supportingContent = {
                Text(
                    text = stringResource(CoreUiR.string.treatment_calculator_desc),
                    color = if (calculatorEnabled) MaterialTheme.colorScheme.onSurfaceVariant
                    else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = disabledAlpha)
                )
            },
            leadingContent = {
                TonalIcon(
                    imageVector = Calculator,
                    color = if (calculatorEnabled) calculatorColor
                    else MaterialTheme.colorScheme.onSurface.copy(alpha = disabledAlpha),
                    enabled = calculatorEnabled
                )
            },
            colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = if (calculatorEnabled) Modifier.clickable {
                onDismiss()
                onCalculatorClick!!()
            } else Modifier
        )
    }
}

@Composable
private fun TreatmentSettingsContent(
    preferences: Preferences,
    config: Config,
    onBack: () -> Unit
) {
    Column(modifier = Modifier.padding(bottom = 24.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 12.dp, end = 24.dp, top = 12.dp, bottom = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(CoreUiR.string.back)
                )
            }
            Text(
                text = stringResource(CoreUiR.string.settings),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        ProvidePreferenceTheme {
            AdaptivePreferenceList(
                items = treatmentButtonSettingsDef.items,
                preferences = preferences,
                config = config
            )
        }
    }
}

@Composable
private fun TonalIcon(
    imageVector: ImageVector,
    color: Color,
    enabled: Boolean,
    modifier: Modifier = Modifier
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(40.dp)
            .background(
                color = if (enabled) color.copy(alpha = 0.12f)
                else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f),
                shape = CircleShape
            )
    ) {
        Icon(
            imageVector = imageVector,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(24.dp)
        )
    }
}

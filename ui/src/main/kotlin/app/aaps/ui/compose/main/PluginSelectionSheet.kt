package app.aaps.ui.compose.main

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import app.aaps.core.data.plugin.PluginType
import app.aaps.core.interfaces.plugin.PluginBase
import app.aaps.core.interfaces.plugin.PluginDescription

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PluginSelectionSheet(
    category: DrawerCategory,
    isSimpleMode: Boolean,
    pluginStateVersion: Int,
    sheetState: SheetState,
    onDismiss: () -> Unit,
    onPluginClick: (PluginBase) -> Unit,
    onPluginEnableToggle: (PluginBase, PluginType, Boolean) -> Unit,
    onPluginPreferencesClick: (PluginBase) -> Unit,
    modifier: Modifier = Modifier
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 32.dp)
        ) {
            // Header
            Text(
                text = stringResource(category.titleRes),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp)
            )

            // Plugin list
            key(pluginStateVersion) {
                category.plugins.forEach { plugin ->
                    val pluginEnabled = plugin.isEnabled(category.type)
                    val hasPreferences = plugin.preferencesId != PluginDescription.PREFERENCE_NONE
                    val showPrefs = hasPreferences && pluginEnabled &&
                        (!isSimpleMode || plugin.pluginDescription.preferencesVisibleInSimpleMode == true)

                    SheetPluginItem(
                        plugin = plugin,
                        isEnabled = pluginEnabled,
                        isAlwaysEnabled = plugin.pluginDescription.alwaysEnabled,
                        showPreferences = showPrefs,
                        onPluginClick = { onPluginClick(plugin) },
                        onEnableToggle = { enabled ->
                            onPluginEnableToggle(plugin, category.type, enabled)
                        },
                        onPreferencesClick = { onPluginPreferencesClick(plugin) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun SheetPluginItem(
    plugin: PluginBase,
    isEnabled: Boolean,
    isAlwaysEnabled: Boolean,
    showPreferences: Boolean,
    onPluginClick: () -> Unit,
    onEnableToggle: (Boolean) -> Unit,
    onPreferencesClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val iconColor = MaterialTheme.colorScheme.primary
    val disabledAlpha = 0.38f

    val iconRes = if (plugin.menuIcon != -1) {
        plugin.menuIcon
    } else {
        app.aaps.core.ui.R.drawable.ic_settings
    }

    val containerColor = if (isEnabled) MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f)
    else MaterialTheme.colorScheme.surface

    ListItem(
        headlineContent = {
            Text(
                text = plugin.name,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        supportingContent = plugin.description?.let { desc ->
            {
                Text(
                    text = desc,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        },
        leadingContent = {
            Box(contentAlignment = Alignment.Center) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(40.dp)
                        .background(
                            color = if (isEnabled) iconColor.copy(alpha = 0.12f)
                            else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f),
                            shape = CircleShape
                        )
                ) {
                    Icon(
                        painter = painterResource(id = iconRes),
                        contentDescription = null,
                        tint = if (isEnabled) iconColor
                        else MaterialTheme.colorScheme.onSurface.copy(alpha = disabledAlpha),
                        modifier = Modifier.size(24.dp)
                    )
                }
                // Checkmark badge for enabled plugins
                if (isEnabled) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .offset(x = 2.dp, y = 2.dp)
                            .size(16.dp)
                            .background(iconColor, CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Check,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(10.dp)
                        )
                    }
                }
            }
        },
        trailingContent = if (isEnabled) {
            {
                Row {
                    if (showPreferences) {
                        IconButton(onClick = onPreferencesClick) {
                            Icon(
                                imageVector = Icons.Filled.Settings,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    IconButton(onClick = onPluginClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        } else null,
        colors = ListItemDefaults.colors(containerColor = containerColor),
        modifier = modifier
            .padding(horizontal = 8.dp, vertical = 2.dp)
            .clip(RoundedCornerShape(12.dp))
            .clickable(enabled = !isAlwaysEnabled) {
                onEnableToggle(!isEnabled)
            }
    )
}

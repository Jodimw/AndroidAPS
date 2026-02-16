package app.aaps.ui.compose.main

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import app.aaps.core.ui.compose.icons.IcClinicalNotes
import app.aaps.core.ui.compose.icons.IcHistory
import app.aaps.core.ui.compose.icons.IcProfile
import app.aaps.core.ui.compose.icons.IcStats

@Composable
fun MainDrawer(
    categories: List<DrawerCategory>,
    versionName: String,
    appIcon: Int,
    onCategoryClick: (DrawerCategory) -> Unit,
    onCategoryExpand: (DrawerCategory) -> Unit,
    onMenuItemClick: (MainMenuItem) -> Unit,
    isTreatmentsEnabled: Boolean,
    modifier: Modifier = Modifier
) {
    ModalDrawerSheet(
        modifier = modifier.width(320.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)
        ) {
            Image(
                painter = painterResource(id = appIcon),
                contentDescription = "AAPS Logo",
                modifier = Modifier.size(40.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "AAPS $versionName",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.primary
            )
        }

        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            DrawerMenuItem(
                //iconRes = app.aaps.core.objects.R.drawable.ic_treatments,
                icon = IcClinicalNotes,
                label = stringResource(app.aaps.core.ui.R.string.treatments),
                description = stringResource(app.aaps.core.ui.R.string.treatments_desc),
                enabled = isTreatmentsEnabled,
                onClick = { onMenuItemClick(MainMenuItem.Treatments) }
            )

            DrawerMenuItem(
                icon = IcHistory,
                label = stringResource(app.aaps.core.ui.R.string.nav_history_browser),
                description = stringResource(app.aaps.core.ui.R.string.nav_history_browser_desc),
                onClick = { onMenuItemClick(MainMenuItem.HistoryBrowser) }
            )

            DrawerMenuItem(
                icon = Icons.Default.Settings,
                label = stringResource(app.aaps.core.ui.R.string.nav_setupwizard),
                description = stringResource(app.aaps.core.ui.R.string.nav_setupwizard_desc),
                onClick = { onMenuItemClick(MainMenuItem.SetupWizard) }
            )

            DrawerMenuItem(
                icon = IcStats,
                label = stringResource(app.aaps.ui.R.string.statistics),
                description = stringResource(app.aaps.ui.R.string.statistics_desc),
                onClick = { onMenuItemClick(MainMenuItem.Stats) }
            )

            DrawerMenuItem(
                icon = IcProfile,
                label = stringResource(app.aaps.ui.R.string.nav_profile_helper),
                description = stringResource(app.aaps.ui.R.string.nav_profile_helper_desc),
                onClick = { onMenuItemClick(MainMenuItem.ProfileHelper) }
            )

            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
            Spacer(modifier = Modifier.height(8.dp))

            // Plugin categories
            categories.forEach { category ->
                val categoryName = stringResource(category.titleRes)

                DrawerCategoryItem(
                    category = category,
                    categoryName = categoryName,
                    onClick = { onCategoryClick(category) },
                    onExpandClick = { onCategoryExpand(category) }
                )
            }
        }

        // Bottom section with About and Exit
        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
        Spacer(modifier = Modifier.height(8.dp))

        DrawerMenuItem(
            icon = Icons.Default.Info,
            label = stringResource(app.aaps.core.ui.R.string.nav_about),
            description = stringResource(app.aaps.core.ui.R.string.nav_about_desc),
            onClick = { onMenuItemClick(MainMenuItem.About) }
        )

        DrawerMenuItem(
            icon = Icons.AutoMirrored.Filled.ExitToApp,
            label = stringResource(app.aaps.core.ui.R.string.nav_exit),
            onClick = { onMenuItemClick(MainMenuItem.Exit) }
        )

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun DrawerMenuItem(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    iconRes: Int? = null,
    description: String? = null,
    enabled: Boolean = true,
) {
    val iconPainter = when {
        icon != null -> rememberVectorPainter(icon)
        iconRes != null -> painterResource(id = iconRes)
        else -> error("DrawerMenuItem requires either icon or iconRes")
    }
    val tint = if (enabled) MaterialTheme.colorScheme.onSurfaceVariant
    else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
    val textColor = if (enabled) MaterialTheme.colorScheme.onSurface
    else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .clickable(enabled = enabled, onClick = onClick)
            .padding(horizontal = 24.dp, vertical = if (description != null) 8.dp else 12.dp)
    ) {
        Icon(
            painter = iconPainter,
            contentDescription = null,
            tint = tint,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge,
                color = textColor
            )
            if (description != null) {
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = tint
                )
            }
        }
    }
}

@Composable
private fun DrawerCategoryItem(
    category: DrawerCategory,
    categoryName: String,
    onClick: () -> Unit,
    onExpandClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // If only 1 plugin is enabled, show its name (behave like exclusive selection)
    val subtitle = if (category.enabledCount == 1) {
        category.enabledPlugins.firstOrNull()?.name ?: "-"
    } else if (category.isMultiSelect) {
        if (category.enabledCount > 0) "${category.enabledCount}" else "-"
    } else {
        category.activePluginName ?: "-"
    }

    // Use plugin compose icon if only one enabled, fall back to resource icon, then settings icon
    val plugin = if (category.enabledCount == 1) category.enabledPlugins.firstOrNull() else null
    val composeIcon = plugin?.pluginDescription?.icon
    val iconPainter = if (composeIcon != null) {
        rememberVectorPainter(composeIcon)
    } else {
        val pluginIcon = plugin?.menuIcon ?: -1
        painterResource(if (pluginIcon != -1) pluginIcon else app.aaps.core.ui.R.drawable.ic_settings)
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(start = 24.dp, top = 6.dp, bottom = 6.dp, end = 8.dp)
    ) {
        Icon(
            painter = iconPainter,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = categoryName,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = "Expand",
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier
                .size(40.dp)
                .clickable(onClick = onExpandClick)
                .padding(8.dp)
        )
    }
}

package app.aaps.ui.compose.overview

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import app.aaps.core.ui.compose.AapsTheme
import app.aaps.ui.R
import app.aaps.ui.compose.main.TempTargetChipState

@Composable
fun TempTargetChip(
    targetText: String,
    state: TempTargetChipState,
    progress: Float,
    reason: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val iconColor = when (state) {
        TempTargetChipState.Active   -> reason.toIconColor()
        TempTargetChipState.Adjusted -> AapsTheme.generalColors.adjusted
        TempTargetChipState.None     -> MaterialTheme.colorScheme.onSurfaceVariant
    }
    val textColor = MaterialTheme.colorScheme.onSurfaceVariant
    val containerColor = when (state) {
        TempTargetChipState.Active   -> iconColor.copy(alpha = 0.2f)
        TempTargetChipState.Adjusted -> iconColor.copy(alpha = 0.2f)
        TempTargetChipState.None     -> Color.Transparent
    }

    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(8.dp),
        color = containerColor,
        modifier = modifier
            .fillMaxWidth()
            .height(35.dp)
    ) {
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Icon(
                    painter = painterResource(reason.toIconRes()),
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    text = targetText,
                    color = textColor,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
            Box(modifier = Modifier
                .fillMaxWidth()
                .height(3.dp)) {
                if (progress > 0f) {
                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(3.dp),
                        color = iconColor,
                        trackColor = iconColor.copy(alpha = 0.3f)
                    )
                }
            }
        }
    }
}

@Composable
private fun String?.toIconColor(): Color = when (this) {
    "Eating Soon" -> AapsTheme.generalColors.ttEatingSoon
    "Activity"    -> AapsTheme.generalColors.ttActivity
    "Hypo"        -> AapsTheme.generalColors.ttHypoglycemia
    else          -> AapsTheme.generalColors.ttCustom // Custom, Automation, Wear
}

private fun String?.toIconRes(): Int = when (this) {
    "Eating Soon" -> R.drawable.ic_target_eatingsoon
    "Activity"    -> R.drawable.ic_target_activity
    "Hypo"        -> R.drawable.ic_target_hypo
    else          -> app.aaps.core.ui.R.drawable.ic_crosstarget // Custom, Automation, Wear, null
}

@Preview(showBackground = true)
@Composable
private fun TempTargetChipActivePreview() {
    MaterialTheme {
        TempTargetChip(
            targetText = "5.5 - 5.5 (30 min)",
            state = TempTargetChipState.Active,
            progress = 0.5f,
            reason = "Eating Soon",
            onClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun TempTargetChipNonePreview() {
    MaterialTheme {
        TempTargetChip(
            targetText = "5.0 - 7.0",
            state = TempTargetChipState.None,
            progress = 0f,
            reason = null,
            onClick = {}
        )
    }
}

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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import app.aaps.core.ui.compose.AapsTheme
import app.aaps.core.ui.compose.icons.IcProfile

@Composable
fun ProfileChip(
    profileName: String,
    isModified: Boolean,
    progress: Float,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val containerColor = if (isModified) AapsTheme.generalColors.inProgress.copy(alpha = 0.2f) else Color.Transparent
    val contentColor = if (isModified) AapsTheme.generalColors.inProgress else MaterialTheme.colorScheme.onSurfaceVariant

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
                    imageVector = IcProfile,
                    contentDescription = null,
                    tint = contentColor,
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    text = profileName,
                    color = contentColor,
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
                        color = contentColor,
                        trackColor = contentColor.copy(alpha = 0.3f)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ProfileChipPreview() {
    MaterialTheme {
        ProfileChip(
            profileName = "Default 5.6",
            isModified = false,
            progress = 0f,
            onClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ProfileChipModifiedPreview() {
    MaterialTheme {
        ProfileChip(
            profileName = "Default 5.6 *",
            isModified = true,
            progress = 0.6f,
            onClick = {}
        )
    }
}

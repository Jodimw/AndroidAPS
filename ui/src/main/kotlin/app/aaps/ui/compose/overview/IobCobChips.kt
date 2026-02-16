package app.aaps.ui.compose.overview

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import app.aaps.ui.compose.overview.graphs.CobUiState
import app.aaps.ui.compose.overview.graphs.IobUiState

@Composable
fun IobCobChipsRow(
    iobUiState: IobUiState,
    cobUiState: CobUiState,
    modifier: Modifier = Modifier
) {
    val spacingDp = 4.dp
    Layout(
        content = {
            IobChip(state = iobUiState)
            CobChip(state = cobUiState)
        },
        modifier = modifier.fillMaxWidth()
    ) { measurables, constraints ->
        val spacingPx = spacingDp.roundToPx()
        val availableWidth = constraints.maxWidth - spacingPx

        // Measure intrinsic widths to learn each chip's preferred size
        val intrinsics = measurables.map { it.minIntrinsicWidth(constraints.maxHeight) }
        val totalIntrinsic = intrinsics.sum()

        // Scale each chip proportionally so they fill 100% of available space
        val placeables = measurables.mapIndexed { i, measurable ->
            val w = if (totalIntrinsic > 0)
                (intrinsics[i].toLong() * availableWidth / totalIntrinsic).toInt()
            else
                availableWidth / measurables.size
            measurable.measure(constraints.copy(minWidth = w, maxWidth = w))
        }

        val height = placeables.maxOf { it.height }
        layout(constraints.maxWidth, height) {
            var x = 0
            placeables.forEachIndexed { i, placeable ->
                placeable.place(x, 0)
                x += placeable.width + if (i < placeables.lastIndex) spacingPx else 0
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun IobCobChipsRowPreview() {
    MaterialTheme {
        IobCobChipsRow(
            iobUiState = IobUiState(text = "1.25 U", iobTotal = 1.25),
            cobUiState = CobUiState(text = "24g", cobValue = 24.0)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun IobCobChipsRowCarbsReqPreview() {
    MaterialTheme {
        IobCobChipsRow(
            iobUiState = IobUiState(text = "1.25 U", iobTotal = 1.25),
            cobUiState = CobUiState(text = "12g\n45 required", carbsReq = 45, cobValue = 12.0)
        )
    }
}

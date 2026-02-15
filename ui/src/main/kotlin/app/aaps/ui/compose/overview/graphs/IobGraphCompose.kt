package app.aaps.ui.compose.overview.graphs

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import app.aaps.core.graph.vico.Square
import app.aaps.core.interfaces.overview.graph.BolusType
import app.aaps.core.ui.compose.AapsTheme
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.VicoScrollState
import com.patrykandpatrick.vico.compose.cartesian.VicoZoomState
import com.patrykandpatrick.vico.compose.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.compose.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.compose.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.compose.cartesian.data.CartesianLayerRangeProvider
import com.patrykandpatrick.vico.compose.cartesian.data.lineSeries
import com.patrykandpatrick.vico.compose.cartesian.layer.LineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.common.Fill
import com.patrykandpatrick.vico.compose.common.component.ShapeComponent
import com.patrykandpatrick.vico.compose.common.component.TextComponent
import com.patrykandpatrick.vico.compose.common.component.rememberTextComponent

/**
 * IOB (Insulin On Board) Graph using Vico.
 *
 * Series (ALWAYS 6, fixed order):
 *   0: IOB data (or invisible dummy)
 *   1: Small SMB triangles (or invisible dummy)
 *   2: Medium SMB triangles (or invisible dummy)
 *   3: Large SMB triangles (or invisible dummy)
 *   4: Normal bolus markers (or invisible dummy)
 *   5: Normalizer (invisible, ensures identical maxPointSize — see GraphUtils.kt)
 *
 * Uses fixed series count with invisible dummies for empty slots to ensure
 * Vico's LineProvider.series() always maps lines to series by index correctly.
 */
@Composable
fun IobGraphCompose(
    viewModel: GraphViewModel,
    scrollState: VicoScrollState,
    zoomState: VicoZoomState,
    modifier: Modifier = Modifier
) {
    // Collect flows independently
    val iobGraphData by viewModel.iobGraphFlow.collectAsState()
    val treatmentGraphData by viewModel.treatmentGraphFlow.collectAsState()
    val derivedTimeRange by viewModel.derivedTimeRange.collectAsState()

    val hasRealTimeRange = derivedTimeRange != null
    val (minTimestamp, maxTimestamp) = derivedTimeRange ?: run {
        val now = System.currentTimeMillis()
        val dayAgo = now - 24 * 60 * 60 * 1000L
        dayAgo to now
    }

    val modelProducer = remember { CartesianChartModelProducer() }

    // Colors from theme
    val iobColor = AapsTheme.generalColors.activeInsulinText
    val smbColor = AapsTheme.elementColors.insulin

    val minX = 0.0
    val maxX = remember(minTimestamp, maxTimestamp) {
        timestampToX(maxTimestamp, minTimestamp)
    }

    val stableTimeRange = remember(minTimestamp / 60000, maxTimestamp / 60000) {
        minTimestamp to maxTimestamp
    }

    // Dummy series for empty slots — 2 invisible points at y=0
    val dummyX = listOf(minX, minX + 1.0)
    val dummyY = listOf(0.0, 0.0)

    // Cache last non-empty treatment data to survive reset() cycles
    val lastTreatmentData = remember { mutableStateOf(treatmentGraphData) }
    if (treatmentGraphData.boluses.isNotEmpty()) {
        lastTreatmentData.value = treatmentGraphData
    }

    LaunchedEffect(iobGraphData, treatmentGraphData, stableTimeRange) {
        // Skip model building when derivedTimeRange is null (fallback 24h range).
        // The fallback range differs from BG graph's range, causing scroll sync misalignment
        // because pixel-based sync maps to different time windows with different data ranges.
        if (!hasRealTimeRange) return@LaunchedEffect

        val iobPoints = iobGraphData.iob
        val activeTreatmentData = lastTreatmentData.value
        val allBoluses = activeTreatmentData.boluses
        val smbs = allBoluses.filter { it.bolusType == BolusType.SMB }
        val normalBoluses = allBoluses.filter { it.bolusType == BolusType.NORMAL }

        // Split SMBs into 3 size categories
        var smallSmbs: List<Pair<Double, Double>> = emptyList()
        var mediumSmbs: List<Pair<Double, Double>> = emptyList()
        var largeSmbs: List<Pair<Double, Double>> = emptyList()

        if (smbs.isNotEmpty()) {
            if (smbs.size <= 1 || smbs.minOf { it.amount } == smbs.maxOf { it.amount }) {
                mediumSmbs = smbs.map { timestampToX(it.timestamp, minTimestamp) to 0.0 }
            } else {
                val minAmount = smbs.minOf { it.amount }
                val range = smbs.maxOf { it.amount } - minAmount
                val smallThreshold = minAmount + range / 3.0
                val largeThreshold = minAmount + 2.0 * range / 3.0

                val small = mutableListOf<Pair<Double, Double>>()
                val medium = mutableListOf<Pair<Double, Double>>()
                val large = mutableListOf<Pair<Double, Double>>()

                for (smb in smbs) {
                    val point = timestampToX(smb.timestamp, minTimestamp) to 0.0
                    when {
                        smb.amount < smallThreshold  -> small.add(point)
                        smb.amount < largeThreshold  -> medium.add(point)
                        else                         -> large.add(point)
                    }
                }
                smallSmbs = small
                mediumSmbs = medium
                largeSmbs = large
            }
        }

        modelProducer.runTransaction {
            lineSeries {
                // Series 0: IOB data (or dummy)
                val iobFiltered = if (iobPoints.isNotEmpty()) {
                    val pts = iobPoints.map { timestampToX(it.timestamp, minTimestamp) to it.value }
                    filterToRange(pts, minX, maxX)
                } else emptyList()
                if (iobFiltered.isNotEmpty()) {
                    series(x = iobFiltered.map { it.first }, y = iobFiltered.map { it.second })
                } else {
                    series(x = dummyX, y = dummyY)
                }

                // Series 1-3: SMBs by size (small, medium, large) — or dummy
                for (smbList in listOf(smallSmbs, mediumSmbs, largeSmbs)) {
                    val filtered = filterToRange(smbList, minX, maxX)
                    if (filtered.isNotEmpty()) {
                        series(x = filtered.map { it.first }, y = filtered.map { it.second })
                    } else {
                        series(x = dummyX, y = dummyY)
                    }
                }

                // Series 4: Normal boluses (or dummy)
                val bolusFiltered = if (normalBoluses.isNotEmpty()) {
                    val pts = normalBoluses.map { timestampToX(it.timestamp, minTimestamp) to it.amount }
                    filterToRange(pts, minX, maxX)
                } else emptyList()
                if (bolusFiltered.isNotEmpty()) {
                    series(x = bolusFiltered.map { it.first }, y = bolusFiltered.map { it.second })
                } else {
                    series(x = dummyX, y = dummyY)
                }

                // Series 5: Normalizer — ensures identical maxPointSize across all charts (see GraphUtils.kt)
                series(x = NORMALIZER_X, y = NORMALIZER_Y)
            }
        }
    }

    // Time formatter and axis configuration
    val timeFormatter = rememberTimeFormatter(minTimestamp)
    val bottomAxisItemPlacer = rememberBottomAxisItemPlacer(minTimestamp)

    // Line styles — FIXED order matching series indices
    val iobLine = remember(iobColor) {
        LineCartesianLayer.Line(
            fill = LineCartesianLayer.LineFill.single(Fill(iobColor)),
            areaFill = LineCartesianLayer.AreaFill.single(
                Fill(Brush.verticalGradient(listOf(iobColor.copy(alpha = 1f), Color.Transparent)))
            ),
            pointConnector = Square
        )
    }

    val smallSmbLine = remember(smbColor) {
        LineCartesianLayer.Line(
            fill = LineCartesianLayer.LineFill.single(Fill(Color.Transparent)),
            areaFill = null,
            pointProvider = LineCartesianLayer.PointProvider.single(
                LineCartesianLayer.Point(
                    component = ShapeComponent(fill = Fill(smbColor), shape = TriangleShape),
                    size = 10.dp
                )
            )
        )
    }
    val mediumSmbLine = remember(smbColor) {
        LineCartesianLayer.Line(
            fill = LineCartesianLayer.LineFill.single(Fill(Color.Transparent)),
            areaFill = null,
            pointProvider = LineCartesianLayer.PointProvider.single(
                LineCartesianLayer.Point(
                    component = ShapeComponent(fill = Fill(smbColor), shape = TriangleShape),
                    size = 16.dp
                )
            )
        )
    }
    val largeSmbLine = remember(smbColor) {
        LineCartesianLayer.Line(
            fill = LineCartesianLayer.LineFill.single(Fill(Color.Transparent)),
            areaFill = null,
            pointProvider = LineCartesianLayer.PointProvider.single(
                LineCartesianLayer.Point(
                    component = ShapeComponent(fill = Fill(smbColor), shape = TriangleShape),
                    size = 22.dp
                )
            )
        )
    }

    val bolusLine = remember(smbColor) {
        LineCartesianLayer.Line(
            fill = LineCartesianLayer.LineFill.single(Fill(Color.Transparent)),
            areaFill = null,
            pointProvider = LineCartesianLayer.PointProvider.single(
                LineCartesianLayer.Point(
                    component = ShapeComponent(fill = Fill(smbColor), shape = InvertedTriangleShape),
                    size = 22.dp
                )
            )
        )
    }

    // Normalizer line — invisible 22dp-point line that equalizes maxPointSize across all charts.
    // Without this, charts with different point sizes get different xSpacing and unscalableStartPadding,
    // breaking pixel-based scroll/zoom sync. See GraphUtils.kt for details.
    val normalizerLine = remember { createNormalizerLine() }

    // FIXED 6 lines — always matches 6 series
    val lines = remember(iobLine, smallSmbLine, mediumSmbLine, largeSmbLine, bolusLine, normalizerLine) {
        listOf(iobLine, smallSmbLine, mediumSmbLine, largeSmbLine, bolusLine, normalizerLine)
    }

    CartesianChartHost(
        chart = rememberCartesianChart(
            rememberLineCartesianLayer(
                lineProvider = LineCartesianLayer.LineProvider.series(lines),
                rangeProvider = remember(maxX) { CartesianLayerRangeProvider.fixed(minX = 0.0, maxX = maxX) }
            ),
            startAxis = VerticalAxis.rememberStart(
                label = rememberTextComponent(
                    style = TextStyle(color = MaterialTheme.colorScheme.onSurface),
                    minWidth = TextComponent.MinWidth.fixed(30.dp)
                )
            ),
            bottomAxis = HorizontalAxis.rememberBottom(
                valueFormatter = timeFormatter,
                itemPlacer = bottomAxisItemPlacer,
                label = rememberTextComponent(
                    style = TextStyle(color = MaterialTheme.colorScheme.onSurface)
                )
            ),
            getXStep = { 1.0 }
        ),
        modelProducer = modelProducer,
        modifier = modifier
            .fillMaxWidth()
            .height(75.dp),
        scrollState = scrollState,
        zoomState = zoomState
    )
}

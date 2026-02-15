# Graph Treatments Rendering Plan

## Completed
1. **Bolus labels on IOB graph** — inverted triangle markers with formatted amount labels (1, 1.2, .8)
2. **Carbs labels on COB graph** — same inverted triangle + label approach, sourced from `treatmentGraphFlow.carbs`
3. **Sharper triangle shapes** — narrowed base to 60% width for both `TriangleShape` and `InvertedTriangleShape`
4. **Theme colors** — added `cob` color to `ElementColors` for distinct carbs markers on COB graph
5. **Extended boluses on IOB graph** — horizontal purple lines (one 2-point series per EB), dynamic series architecture replacing fixed-6
6. **IOB graph refactored** from fixed 6 series to dynamic series (like COB already was)

## Architecture Decisions Made
- **Vico `dataLabel`** on `LineCartesianLayer.Line` for point labels (formatter + TextComponent + Position.Vertical)
- **Vico `inBounds`** flips label Top→Bottom when near chart edge — we let Vico decide
- **Custom shapes** can't represent duration (fixed bounding box, no chart coordinate access)
- **One series per extended bolus** needed to avoid connecting separate EBs with a line
- **`formatBolusLabel`**: `%.2f` trimmed, leading zero dropped for <1 values
- **`formatCarbsLabel`**: `%.1f` trimmed

## Planned: Treatment Belt Graph
A new thin graph **above BG graph** for running mode + therapy events:

```
┌─────────────────────────────┐
│ TreatmentBeltGraph (40-50dp)│  ← NEW: colored belt + event icons + markers
├─────────────────────────────┤
│ BgGraphCompose              │
├─────────────────────────────┤
│ IobGraphCompose             │
├─────────────────────────────┤
│ CobGraphCompose             │
└─────────────────────────────┘
```

### Running Mode Background
- Vico `Decoration` drawing colored rectangles in `drawUnderLayers()`
- 10 modes: CLOSED_LOOP (transparent), OPEN_LOOP (blue), LGS (purple), DISABLED (red), SUSPENDED (yellow), etc.
- Data from `PrepareRunningModeDataWorker` (5-min interval sampling)
- Thin colored bar spanning time ranges, pinned to chart area

### Therapy Events as Icon Series
7 types to render:

| # | Type | Visual | Has Duration |
|---|---|---|---|
| 1 | MBG (manual BG) | Circle stroke | No |
| 2 | Finger Stick BG | Filled circle | No |
| 3 | Announcement | Circle + label | No |
| 4 | Settings Export | Circle | No |
| 5 | Exercise | Label + duration bar | Yes |
| 6 | General (no duration) | Circle + label | No |
| 7 | General with duration | Label + bar | Yes |

### Icons via Custom PainterComponent
- Vico's `DrawingContext.mutableDrawScope` exposes Compose `DrawScope`
- `rememberVectorPainter(Icons.Default.*)` → use as `Component` in `LineCartesianLayer.Point`
- Layered with `ShapeComponent` background via `LayeredComponent`

```kotlin
class PainterComponent(
    private val painter: Painter,
    private val tint: Color
) : Component {
    override fun draw(context: DrawingContext, left: Float, top: Float, right: Float, bottom: Float) {
        with(context.mutableDrawScope) {
            translate(left, top) {
                with(painter) {
                    draw(
                        size = Size(right - left, bottom - top),
                        colorFilter = ColorFilter.tint(tint)
                    )
                }
            }
        }
    }
}
```

### Interaction — Markers for Tap Details
- `CartesianMarkerController.rememberToggleOnTap()` — tap to show/hide tooltip
- `DefaultCartesianMarker` with custom `ValueFormatter` showing event type + label
- `ExtraStore` maps x-positions → therapy event data for marker lookup

### Duration Events (Exercise, General with Duration)
- Same approach as extended boluses: one 2-point series per duration event
- Horizontal line spanning start→end at fixed Y
- Label via `dataLabel`

### Shared Scroll/Zoom
- Same `scrollState`/`zoomState` as all other graphs
- Same X coordinate system (`timestampToX`, `rangeProvider`, `getXStep = { 1.0 }`)
- No axis labels needed (clean belt look)

## Key Files
- `IobGraphCompose.kt` — IOB + bolus + SMB + extended bolus
- `CobGraphCompose.kt` — COB + carbs
- `GraphUtils.kt` — shapes, formatters, normalizer
- `ElementColors.kt` — theme colors (insulin, cob, extendedBolus)
- `CalculationResults.kt` — data models (TreatmentGraphData, TherapyEventGraphPoint, etc.)
- `PrepareRunningModeDataWorker.kt` — running mode data preparation
- `RunningModeDataPoint.kt` — running mode colors per state

## Running Mode Color Map
| Mode | Color |
|---|---|
| `CLOSED_LOOP` | Transparent (hidden) |
| `OPEN_LOOP` | Blue `#4983D7` |
| `CLOSED_LOOP_LGS` | Purple `#800080` |
| `DISABLED_LOOP` | Red `#FF1313` |
| `SUPER_BOLUS` | Orange |
| `DISCONNECTED_PUMP` | Gray `#939393` |
| `SUSPENDED_BY_PUMP` | Yellow `#F6CE22` |
| `SUSPENDED_BY_USER` | Yellow `#F6CE22` |
| `SUSPENDED_BY_DST` | Yellow `#F6CE22` |
| `RESUME` | No color |

package app.aaps.core.ui.compose.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

/**
 * Icon for insulin treatment/bolus.
 * Represents a syringe.
 *
 * Bounding box: x: 1.6-22.4, y: 3.5-20.5 (viewport: 24x24)
 */
val Treatment: ImageVector by lazy {
    ImageVector.Builder(
        name = "Treatment",
        defaultWidth = 48.dp,
        defaultHeight = 48.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(18.501f, 12.002f)
            lineToRelative(-0.428f, 0.564f)
            lineTo(8.179f, 5.064f)
            curveToRelative(-0.092f, -0.07f, -0.212f, -0.09f, -0.323f, -0.055f)
            lineTo(5.115f, 5.89f)
            lineTo(1.571f, 3.501f)
            lineTo(4.755f, 6.36f)
            lineTo(4.647f, 9.243f)
            curveToRelative(-0.004f, 0.116f, 0.047f, 0.226f, 0.14f, 0.296f)
            lineToRelative(9.895f, 7.502f)
            lineToRelative(-0.428f, 0.564f)
            curveToRelative(-0.26f, 0.343f, -0.193f, 0.832f, 0.15f, 1.092f)
            reflectiveCurveToRelative(0.832f, 0.193f, 1.092f, -0.15f)
            lineToRelative(1.653f, -2.18f)
            lineToRelative(2.219f, 1.683f)
            lineToRelative(-0.719f, 0.948f)
            curveToRelative(-0.26f, 0.343f, -0.193f, 0.832f, 0.15f, 1.092f)
            curveToRelative(0.343f, 0.26f, 0.832f, 0.193f, 1.092f, -0.15f)
            lineToRelative(2.379f, -3.138f)
            curveToRelative(0.26f, -0.343f, 0.193f, -0.832f, -0.15f, -1.092f)
            curveToRelative(-0.343f, -0.26f, -0.832f, -0.193f, -1.092f, 0.15f)
            lineToRelative(-0.719f, 0.948f)
            lineToRelative(-2.219f, -1.683f)
            lineToRelative(1.653f, -2.18f)
            curveToRelative(0.26f, -0.343f, 0.193f, -0.832f, -0.15f, -1.092f)
            reflectiveCurveTo(18.762f, 11.659f, 18.501f, 12.002f)
            close()
            moveTo(5.458f, 6.524f)
            lineToRelative(2.44f, -0.783f)
            lineToRelative(9.748f, 7.39f)
            lineTo(17.26f, 13.64f)
            lineToRelative(-5.754f, -4.362f)
            curveToRelative(-0.091f, -0.069f, -0.221f, -0.053f, -0.293f, 0.035f)
            lineTo(9.087f, 11.91f)
            lineToRelative(-0.67f, -0.508f)
            lineToRelative(1.526f, -1.833f)
            curveToRelative(0.002f, -0.002f, 0.004f, -0.005f, 0.006f, -0.008f)
            curveToRelative(0.068f, -0.09f, 0.054f, -0.219f, -0.033f, -0.291f)
            curveToRelative(-0.09f, -0.075f, -0.224f, -0.063f, -0.299f, 0.027f)
            lineToRelative(-1.538f, 1.848f)
            lineToRelative(-0.769f, -0.583f)
            lineToRelative(1.526f, -1.833f)
            curveToRelative(0.002f, -0.002f, 0.004f, -0.005f, 0.006f, -0.008f)
            curveToRelative(0.068f, -0.09f, 0.054f, -0.219f, -0.033f, -0.291f)
            curveToRelative(-0.09f, -0.075f, -0.224f, -0.063f, -0.299f, 0.027f)
            lineTo(6.97f, 10.305f)
            lineTo(6.201f, 9.721f)
            lineToRelative(1.526f, -1.833f)
            curveTo(7.728f, 7.886f, 7.731f, 7.882f, 7.732f, 7.88f)
            curveToRelative(0.068f, -0.09f, 0.054f, -0.219f, -0.033f, -0.291f)
            curveTo(7.609f, 7.513f, 7.475f, 7.526f, 7.4f, 7.616f)
            lineTo(5.861f, 9.464f)
            lineTo(5.362f, 9.085f)
            lineTo(5.458f, 6.524f)
            close()
        }
    }.build()
}

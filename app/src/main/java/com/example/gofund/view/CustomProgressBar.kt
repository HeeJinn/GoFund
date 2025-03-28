package com.example.gofund.view

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height // This might be unused in this specific snippet
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.* // Keeping wildcard as requested ("don't change anything")
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import com.example.gofund.ui.theme.IntroFamily
import com.example.gofund.ui.theme.PoppinsFamily

@Composable
fun CustomComponent(
    canvasSize: Dp = 300.dp,
    indicatorValue: Int = 0,
    maxIndicatorValue: Int = 100000,
    backgroundColorIndicator: Color = Color.LightGray,
    backgroundIndicatorStrokeWidth: Float = 100f,
    foregroundColorIndicator: Color = MaterialTheme.colorScheme.primary,
    foregroundIndicatorStrokeWidth: Float = 100f,
    bigTextFontSize: TextUnit = MaterialTheme.typography.headlineLarge.fontSize,
    bigTextColor: Color = MaterialTheme.colorScheme.onSurface,
    bigTextSuffix: String = "₱",
    smallText: String = "Remaining",
    smallTextFontSize: TextUnit = MaterialTheme.typography.titleLarge.fontSize,
    smallTextColor: Color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
) {
    var allowedIndicatorValue by remember { mutableStateOf(maxIndicatorValue) }
    allowedIndicatorValue = if (indicatorValue <= maxIndicatorValue) indicatorValue else maxIndicatorValue

    var animatedIndicatorValue by remember { mutableFloatStateOf(0f) } // Changed to mutableFloatStateOf for consistency
    LaunchedEffect(key1 = allowedIndicatorValue) {
        animatedIndicatorValue = allowedIndicatorValue.toFloat()
    }

    // Ensure maxIndicatorValue is not zero to avoid division by zero
    val safeMaxIndicatorValue = if (maxIndicatorValue == 0) 1 else maxIndicatorValue
    val percentage = (animatedIndicatorValue / safeMaxIndicatorValue) * 100

    val sweepAngle by animateFloatAsState(
        targetValue = (2.4 * percentage).toFloat(),
        animationSpec = tween(1000),
        label = "SweepAngleAnimation" // Added label
    )

    val receivedValue by animateIntAsState(
        targetValue = allowedIndicatorValue,
        animationSpec = tween(1000),
        label = "ReceivedValueAnimation" // Added label
    )

    val animatedBigTextColor by animateColorAsState(
        targetValue = if (allowedIndicatorValue == 0) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f) else bigTextColor,
        animationSpec = tween(1000),
        label = "TextColorAnimation" // Added label
    )

    Column(
        modifier = Modifier
            .size(canvasSize)
            .drawBehind {
                val componentSize = size / 1.25f // Calculation kept as is
                backgroundIndicator(
                    componentSize = componentSize,
                    indicatorColor = backgroundColorIndicator,
                    indicatorStrokeWidth = backgroundIndicatorStrokeWidth
                )
                foregroundIndicator(
                    sweepAngle = sweepAngle,
                    componentSize = componentSize,
                    indicatorColor = foregroundColorIndicator,
                    indicatorStrokeWidth = foregroundIndicatorStrokeWidth
                )
            },
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        EmbeddedElements(
            bigText = receivedValue,
            bigTextFontSize = bigTextFontSize,
            bigTextColor = animatedBigTextColor,
            bigTextSuffix = bigTextSuffix,
            smallText = smallText,
            smallTextColor = smallTextColor,
            smallTextFontSize = smallTextFontSize,
        )
    }
}


fun DrawScope.backgroundIndicator(
    componentSize: Size,
    indicatorColor: Color,
    indicatorStrokeWidth: Float
) {
    drawArc(
        color = indicatorColor,
        startAngle = 150f,
        sweepAngle = 240f,
        useCenter = false,
        style = Stroke(
            width = indicatorStrokeWidth,
            cap = StrokeCap.Round
        ),
        size = componentSize,
        topLeft = Offset(
            x = (size.width - componentSize.width) / 2f,
            y = (size.height - componentSize.height) / 2f // Corrected from size.width
        )
    )
}

fun DrawScope.foregroundIndicator(
    sweepAngle: Float,
    componentSize: Size,
    indicatorColor: Color,
    indicatorStrokeWidth: Float
) {
    drawArc(
        color = indicatorColor,
        startAngle = 150f,
        sweepAngle = sweepAngle,
        useCenter = false,
        style = Stroke(
            width = indicatorStrokeWidth,
            cap = StrokeCap.Round
        ),
        size = componentSize,
        topLeft = Offset(
            x = (size.width - componentSize.width) / 2f,
            y = (size.height - componentSize.height) / 2f // Corrected from size.width
        )
    )
}

@Composable
fun EmbeddedElements(
    bigText: Int,
    bigTextFontSize: TextUnit,
    bigTextColor: Color,
    bigTextSuffix: String,
    smallText: String,
    smallTextColor: Color,
    smallTextFontSize: TextUnit
) {
    Text(
        text = smallText,
        color = smallTextColor,
        fontSize = smallTextFontSize,
        textAlign = TextAlign.Center,
    )
    Text(
        // Original formatting kept: "${bigTextSuffix.take(2)} ${bigText}"
        // Consider if only one char of suffix needed: "${bigTextSuffix.firstOrNull() ?: ""} $bigText"
        text = "${bigTextSuffix.take(2)} ${bigText}",
        color = bigTextColor,
        maxLines = 1,
        fontSize = bigTextFontSize,
        textAlign = TextAlign.Center,
        fontFamily = IntroFamily,
        fontWeight = FontWeight.Bold
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewCustomCircle() {
    CustomComponent()
}
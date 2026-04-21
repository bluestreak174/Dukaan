package com.addendtek.dukaan.ui.utils

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.addendtek.dukaan.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.sqrt

@Composable
fun IntroShowCase(targetCords: LayoutCoordinates) {
    val targetRect = targetCords.boundsInRoot()
    val targetRadius = targetRect.maxDimension / 2f + 40f
    // 40f extra traget spacing

    Box (
        modifier = Modifier.fillMaxSize()
    ){
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer(alpha = 0.99f)
        ) {
            drawCircle(
                color = Color.Black,
                center = targetRect.center
            )
            drawCircle(
                color = Color.White,
                radius = targetRadius,
                center = targetRect.center,
                blendMode = BlendMode.Clear
            )
        }
    }
}



@Composable
fun HelpShowCase(
    target: ShowcaseProperty,
    dismissOnClickOutside: Boolean,
    onShowCaseCompleted: () -> Unit,

) {
        if(target.coordinates.isAttached){
            val targetCords = target.coordinates
            val targetRect = target.coordinates.boundsInRoot()
            val targetRadius = targetRect.maxDimension / 2f + 40f
            var dismissShowcaseRequest by remember(target) { mutableStateOf(false) }
            val maxDimension =
                max(targetCords.size.width.absoluteValue, targetCords.size.height.absoluteValue)

            val animationSpec = infiniteRepeatable<Float>(
                animation = tween(2000, easing = FastOutLinearInEasing),
                repeatMode = RepeatMode.Restart,
            )
            var outerOffset by remember(target) {
                mutableStateOf(Offset(0f, 0f))
            }

            var outerRadius by remember(target) {
                mutableFloatStateOf(0f)
            }
            val outerAnimatable = remember { Animatable(0.6f) }
            val outerAlphaAnimatable = remember(target) { Animatable(0f) }
            val animatables = listOf(
                remember { Animatable(0f) },
                remember { Animatable(0f) }
            )

            animatables.forEachIndexed { index, animatable ->
                LaunchedEffect(animatable) {
                    delay(index * 1000L)
                    animatable.animateTo(
                        targetValue = 1f, animationSpec = animationSpec
                    )
                }
            }

            LaunchedEffect(target) {
                outerAnimatable.snapTo(0.6f)

                outerAnimatable.animateTo(
                    targetValue = 1f,
                    animationSpec = tween(
                        durationMillis = 500,
                        easing = FastOutSlowInEasing,
                    ),
                )
            }

            LaunchedEffect(target) {
                outerAlphaAnimatable.animateTo(
                    targetValue = target.style.backgroundAlpha,
                    animationSpec = tween(
                        durationMillis = 500,
                        easing = FastOutSlowInEasing,
                    ),
                )
            }

            LaunchedEffect(dismissShowcaseRequest) {
                if (dismissShowcaseRequest) {
                    launch {
                        outerAlphaAnimatable.animateTo(
                            0f,
                            animationSpec = tween(
                                durationMillis = 200
                            )
                        )
                    }
                    launch {
                        outerAnimatable.animateTo(
                            targetValue = 0.6f,
                            animationSpec = tween(
                                durationMillis = 350,
                                easing = FastOutSlowInEasing,
                            )
                        )
                    }
                    delay(350)
                    onShowCaseCompleted()

                }
            }

            val dys = animatables.map { it.value }

            if (!dismissShowcaseRequest) {
                Box(
                    modifier = Modifier
                        .alpha(outerAlphaAnimatable.value)
                ) {
                    Canvas(
                        modifier = Modifier
                            .fillMaxSize()
                            .pointerInput(target) {
                                detectTapGestures { tapOffeset ->
                                    if (targetRect.contains(tapOffeset)) {
                                        dismissShowcaseRequest = true
                                    }
                                }
                            }
                            .let {
                                if (dismissOnClickOutside) {
                                    it.clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = null
                                    ) { dismissShowcaseRequest = true }
                                } else it
                            }
                            .graphicsLayer(alpha = 0.99f)
                    ) {
                        drawCircle(
                            color = target.style.backgroundColor,
                            center = outerOffset,
                            radius = outerRadius * outerAnimatable.value,
                            alpha = target.style.backgroundAlpha
                        )

                        dys.forEach { dy ->
                            drawCircle(
                                color = target.style.targetCircleColor,
                                radius = maxDimension * dy * 2f,
                                center = targetRect.center,
                                alpha = 1 - dy
                            )
                        }

                        drawCircle(
                            color = target.style.targetCircleColor,
                            radius = targetRadius,
                            center = targetRect.center,
                            blendMode = BlendMode.Xor
                        )
                    }

                    ShowCaseText(
                        target,
                        targetRect,
                        targetRadius,
                    ) { textCoords ->
                        val contentRect = textCoords.boundsInWindow()
                        val outerRect = getOuterRect(contentRect, targetRect)
                        outerOffset = outerRect.center
                        outerRadius = getOuterRadius(outerRect) + targetRadius
                    }

                }
            }
        }



}

private fun getOuterRect(contentRect: Rect, targetRect: Rect): Rect {

    val topLeftX = min(contentRect.topLeft.x, targetRect.topLeft.x)
    val topLeftY = min(contentRect.topLeft.y, targetRect.topLeft.y)
    val bottomRightX = max(contentRect.bottomRight.x, targetRect.bottomRight.x)
    val bottomRightY = max(contentRect.bottomRight.y, targetRect.bottomRight.y)

    return Rect(topLeftX, topLeftY, bottomRightX, bottomRightY)
}

private fun getOuterRadius(outerRect: Rect): Float {
    val d = sqrt(
        outerRect.height.toDouble().pow(2.0)
                + outerRect.width.toDouble().pow(2.0)
    ).toFloat()

    return (d / 2f)
}

@Composable
private fun ShowCaseText(
    currentTarget: ShowcaseProperty,
    boundsInParent: Rect,
    targetRadius: Float,
    updateContentCoordinates: (LayoutCoordinates) -> Unit,

) {

    var contentOffsetY by remember(currentTarget) { mutableFloatStateOf(0f) }

    Box(
        modifier = Modifier
            .offset(y = with(LocalDensity.current) {
                contentOffsetY.toDp()
            })
            .onGloballyPositioned {
                updateContentCoordinates(it)
                val contentHeight = it.size.height

                val possibleTop =
                    boundsInParent.center.y - targetRadius - contentHeight

                contentOffsetY = if (possibleTop > 0) {
                    possibleTop
                } else {
                    boundsInParent.center.y + targetRadius
                }
            }
            .padding(16.dp)
    ){

        Column {
            Text(
                text = currentTarget.title,
                fontSize = 24.sp,
                color = currentTarget.subTitleColor,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = currentTarget.subTitle,
                fontSize = 16.sp,
                color = currentTarget.subTitleColor
            )
            HorizontalDivider()
            Text(
                text = stringResource(R.string.help_info_enable_and_disable),
                fontSize = 16.sp,
                color = currentTarget.subTitleColor
            )

        }

    }

}

data class ShowcaseProperty(
    val index: Int,
    val coordinates: LayoutCoordinates,
    val style: ShowcaseStyle = ShowcaseStyle.Default,
    val title: String, val subTitle: String,
    val titleColor: Color = Color.White,
    val subTitleColor: Color = Color.White,
)

class ShowcaseStyle(
    val backgroundColor: Color = Color.Blue,
    /*@FloatRange(from = 0.0, to = 1.0)*/
    val backgroundAlpha: Float = DEFAULT_BACKGROUND_RADIUS,
    val targetCircleColor: Color = Color.White
) {

    fun copy(
        backgroundColor: Color = this.backgroundColor,
        /*@FloatRange(from = 0.0, to = 1.0)*/
        backgroundAlpha: Float = this.backgroundAlpha,
        targetCircleColor: Color = this.targetCircleColor
    ): ShowcaseStyle {

        return ShowcaseStyle(
            backgroundColor = backgroundColor,
            backgroundAlpha = backgroundAlpha,
            targetCircleColor = targetCircleColor
        )
    }

    companion object {
        private const val DEFAULT_BACKGROUND_RADIUS = 0.9f

        /**
         * Constant for default text style.
         */
        @Stable
        val Default = ShowcaseStyle()
    }
}
package com.SharaSpot.ui.extensions

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.graphicsLayer
import com.SharaSpot.ui.theme.Animation

/**
 * Premium Animation Utilities for SharaCharge
 *
 * Provides smooth, professional animations and micro-interactions
 * that enhance the user experience and give a premium feel.
 */

// ========== Scale Animations ==========

/**
 * Adds a subtle scale animation when pressed
 * Perfect for buttons and interactive elements
 */
fun Modifier.pressableScale(
    pressedScale: Float = 0.95f,
    animationSpec: AnimationSpec<Float> = spring(
        dampingRatio = Spring.DampingRatioMediumBouncy,
        stiffness = Spring.StiffnessLow
    )
): Modifier = composed {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale = remember { Animatable(1f) }

    LaunchedEffect(isPressed) {
        scale.animateTo(
            targetValue = if (isPressed) pressedScale else 1f,
            animationSpec = animationSpec
        )
    }

    this
        .scale(scale.value)
        .clickable(
            interactionSource = interactionSource,
            indication = null,
            onClick = {}
        )
}

/**
 * Adds a bounce effect when appearing
 */
fun Modifier.bounceIn(
    initialScale: Float = 0.8f
): Modifier = composed {
    val scale = remember { Animatable(initialScale) }

    LaunchedEffect(Unit) {
        scale.animateTo(
            targetValue = 1f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioLowBouncy,
                stiffness = Spring.StiffnessMedium
            )
        )
    }

    this.scale(scale.value)
}

// ========== Shimmer Effect ==========

/**
 * Creates a shimmer loading effect
 * Perfect for skeleton screens and loading states
 */
fun Modifier.shimmerEffect(): Modifier = composed {
    val infiniteTransition = rememberInfiniteTransition(label = "shimmer")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "shimmerAlpha"
    )
    this.alpha(alpha)
}

/**
 * Creates a sliding shimmer effect (left to right)
 */
fun Modifier.shimmerSlide(): Modifier = composed {
    val infiniteTransition = rememberInfiniteTransition(label = "shimmerSlide")
    val translateX by infiniteTransition.animateFloat(
        initialValue = -1000f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmerTranslate"
    )
    this.graphicsLayer {
        translationX = translateX
    }
}

// ========== Fade Animations ==========

/**
 * Smooth fade in animation
 */
@Composable
fun FadeInAnimation(
    visible: Boolean,
    content: @Composable AnimatedVisibilityScope.() -> Unit
) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(animationSpec = tween(Animation.Normal.toInt())),
        exit = fadeOut(animationSpec = tween(Animation.Normal.toInt())),
        content = content
    )
}

/**
 * Slide and fade in from bottom
 * Perfect for cards and dialog content
 */
@Composable
fun SlideUpFadeIn(
    visible: Boolean,
    content: @Composable AnimatedVisibilityScope.() -> Unit
) {
    AnimatedVisibility(
        visible = visible,
        enter = slideInVertically(
            initialOffsetY = { it / 2 },
            animationSpec = tween(Animation.Normal.toInt(), easing = FastOutSlowInEasing)
        ) + fadeIn(animationSpec = tween(Animation.Normal.toInt())),
        exit = slideOutVertically(
            targetOffsetY = { it / 2 },
            animationSpec = tween(Animation.Fast.toInt())
        ) + fadeOut(animationSpec = tween(Animation.Fast.toInt())),
        content = content
    )
}

/**
 * Expand and fade in
 * Perfect for lists and expandable sections
 */
@Composable
fun ExpandFadeIn(
    visible: Boolean,
    content: @Composable AnimatedVisibilityScope.() -> Unit
) {
    AnimatedVisibility(
        visible = visible,
        enter = expandVertically(
            animationSpec = tween(Animation.Normal.toInt(), easing = FastOutSlowInEasing)
        ) + fadeIn(animationSpec = tween(Animation.Normal.toInt())),
        exit = shrinkVertically(
            animationSpec = tween(Animation.Fast.toInt())
        ) + fadeOut(animationSpec = tween(Animation.Fast.toInt())),
        content = content
    )
}

// ========== Pulse Animation ==========

/**
 * Adds a subtle pulse animation
 * Great for drawing attention to important elements
 */
fun Modifier.pulse(
    minScale: Float = 0.95f,
    maxScale: Float = 1.05f,
    durationMillis: Int = 1000
): Modifier = composed {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = minScale,
        targetValue = maxScale,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )
    this.scale(scale)
}

// ========== Rotation Animations ==========

/**
 * Continuous rotation animation
 * Perfect for loading indicators
 */
fun Modifier.rotate(): Modifier = composed {
    val infiniteTransition = rememberInfiniteTransition(label = "rotation")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotationAngle"
    )
    this.graphicsLayer {
        rotationZ = rotation
    }
}

// ========== Hover/Press Effects ==========

/**
 * Adds a scale and alpha effect when clicked
 * Creates a premium tactile feedback
 */
fun Modifier.premiumClickable(
    onClick: () -> Unit,
    pressedScale: Float = 0.97f,
    pressedAlpha: Float = 0.9f
): Modifier = composed {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale = remember { Animatable(1f) }
    val alpha = remember { Animatable(1f) }

    LaunchedEffect(isPressed) {
        if (isPressed) {
            scale.animateTo(pressedScale, tween(Animation.ExtraFast.toInt()))
            alpha.animateTo(pressedAlpha, tween(Animation.ExtraFast.toInt()))
        } else {
            scale.animateTo(1f, spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessMedium
            ))
            alpha.animateTo(1f, tween(Animation.Fast.toInt()))
        }
    }

    this
        .scale(scale.value)
        .alpha(alpha.value)
        .clickable(
            interactionSource = interactionSource,
            indication = null,
            onClick = onClick
        )
}

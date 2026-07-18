package com.robotopia.androidstudiolite.designsystem.animation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith

private const val NavFadeMs = 220
/** Exit starts slightly after enter so the new screen begins fading in first. */
private const val NavExitDelayMs = NavFadeMs

/**
 * Cross-fade for custom route switches ([androidx.compose.animation.AnimatedContent]).
 * Enter fades immediately; exit fades with a short delay.
 */
fun AnimatedContentTransitionScope<*>.navFade(): ContentTransform =
    fadeIn(animationSpec = tween(NavFadeMs)) togetherWith
        fadeOut(
            animationSpec = tween(
                durationMillis = NavFadeMs,
                delayMillis = NavExitDelayMs,
            ),
        )

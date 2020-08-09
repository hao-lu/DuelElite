package com.lucidity.haolu.searchcards.transition

import androidx.transition.ChangeBounds
import androidx.transition.ChangeImageTransform
import androidx.transition.ChangeTransform
import androidx.transition.TransitionSet

class RotateCrossfadeTransition(
    isCounterClockwise: Boolean = false,
    startRotation: Float = 0f,
    endRotation: Float = 90f
) : TransitionSet() {

    init {
        ordering = ORDERING_TOGETHER
        addTransition(ChangeBounds())
        addTransition(ChangeTransform())
        addTransition(ChangeImageTransform())
        addTransition(
            RotateImage(
                isCounterClockwise,
                startRotation,
                endRotation
            )
        )
//            .addTransition(Crossfade())
    }
}
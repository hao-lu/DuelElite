package com.lucidity.haolu.searchcards.transition

import android.animation.Animator
import android.animation.ValueAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import android.widget.ImageView
import androidx.transition.Transition
import androidx.transition.TransitionValues

/**
 * This Transition rotates an ImageView by x degrees.
 */
class RotateImage : Transition {

    private var isCounterClockwise = false
    private var startRotation = 0f
    private var endRotation = 0f

    constructor() {}

    constructor(
        isCounterClockwise: Boolean = false,
        startRotation: Float = 0f,
        endRotation: Float = 90f
    ) {
        this.isCounterClockwise = isCounterClockwise
        this.startRotation = startRotation
        this.endRotation = endRotation
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {}

    override fun captureEndValues(transitionValues: TransitionValues) {
        captureValues(transitionValues)
    }

    override fun captureStartValues(transitionValues: TransitionValues) {
        captureValues(transitionValues)
    }

    private fun captureValues(transitionValues: TransitionValues) {
        if (transitionValues.view is ImageView) {
            val drawable = (transitionValues.view as ImageView).drawable
            transitionValues.values[PROPNAME_TRANSITION_DRAWABLE] = drawable
        }
    }

    override fun getTransitionProperties(): Array<String>? {
        return TRANSITION_PROPERTIES
    }

    override fun createAnimator(
        sceneRoot: ViewGroup,
        startValues: TransitionValues?,
        endValues: TransitionValues?
    ): Animator? {
        if (startValues == null || endValues == null) {
            return null
        }

        val imageView = endValues.view as ImageView
        val rotationRange = createRotationRange()
        val animator = ValueAnimator.ofFloat(rotationRange.first, rotationRange.second)
        animator.addUpdateListener {
            imageView.rotation = it.animatedValue as Float
        }
        return animator
    }

    private fun createRotationRange(): Pair<Float, Float> {
        return if (isCounterClockwise)
            Pair(endRotation, startRotation)
        else
            Pair(startRotation, endRotation)
    }

    companion object {
        private const val PROPNAME_TRANSITION_DRAWABLE = "lucidity:rotate:transitionRotation"

        private val TRANSITION_PROPERTIES = arrayOf(
            PROPNAME_TRANSITION_DRAWABLE
        )
    }
}
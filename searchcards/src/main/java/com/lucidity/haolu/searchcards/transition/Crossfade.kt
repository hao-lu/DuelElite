package com.lucidity.haolu.searchcards.transition

import android.animation.*
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.BitmapDrawable
import android.view.TextureView
import android.view.View
import android.view.ViewGroup
import androidx.transition.Transition
import androidx.transition.TransitionValues

/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * This transition captures bitmap representations of target views before and
 * after the scene change and fades between them.
 *
 *
 * Note: This transition is not compatible with [TextureView]
 * or [SurfaceView].
 *
 * @hide
 */
class Crossfade : Transition() {
    /**
     * Returns the fading behavior of the animation.
     *
     * @return This crossfade object.
     * @see .setFadeBehavior
     */
    var fadeBehavior =
        FADE_BEHAVIOR_OUT_IN
        private set

    /**
     * Returns the resizing behavior of the animation.
     *
     * @return This crossfade object.
     * @see .setResizeBehavior
     */
    var resizeBehavior =
        RESIZE_BEHAVIOR_SCALE
        private set
    // TODO: Add fade/resize behaviors to xml resources
    /**
     * Sets the type of fading animation that will be run, one of
     * [.FADE_BEHAVIOR_CROSSFADE] and [.FADE_BEHAVIOR_REVEAL].
     *
     * @param fadeBehavior The type of fading animation to use when this
     * transition is run.
     */
    fun setFadeBehavior(fadeBehavior: Int): Crossfade {
        if (fadeBehavior >= FADE_BEHAVIOR_CROSSFADE && fadeBehavior <= FADE_BEHAVIOR_OUT_IN) {
            this.fadeBehavior = fadeBehavior
        }
        return this
    }

    /**
     * Sets the type of resizing behavior that will be used during the
     * transition animation, one of [.RESIZE_BEHAVIOR_NONE] and
     * [.RESIZE_BEHAVIOR_SCALE].
     *
     * @param resizeBehavior The type of resizing behavior to use when this
     * transition is run.
     */
    fun setResizeBehavior(resizeBehavior: Int): Crossfade {
        if (resizeBehavior >= RESIZE_BEHAVIOR_NONE && resizeBehavior <= RESIZE_BEHAVIOR_SCALE) {
            this.resizeBehavior = resizeBehavior
        }
        return this
    }

    override fun createAnimator(
        sceneRoot: ViewGroup,
        startValues: TransitionValues?,
        endValues: TransitionValues?
    ): Animator? {
        if (startValues == null || endValues == null) {
            return null
        }
        val useParentOverlay =
            fadeBehavior != FADE_BEHAVIOR_REVEAL
        val view: View = endValues.view
        val startVals: Map<String, Any> = startValues.values
        val endVals: Map<String, Any> = endValues.values
        val startBounds =
            startVals[PROPNAME_BOUNDS] as Rect?
        val endBounds =
            endVals[PROPNAME_BOUNDS] as Rect?
        val startBitmap =
            startVals[PROPNAME_BITMAP] as Bitmap?
        val endBitmap =
            endVals[PROPNAME_BITMAP] as Bitmap?
        val startDrawable =
            startVals[PROPNAME_DRAWABLE] as BitmapDrawable?
        val endDrawable =
            endVals[PROPNAME_DRAWABLE] as BitmapDrawable?
//        if (Transition.DBG) {
//            Log.d(
//                LOG_TAG,
//                "StartBitmap.sameAs(endBitmap) = " + startBitmap!!.sameAs(endBitmap) +
//                        " for start, end: " + startBitmap + ", " + endBitmap
//            )
//        }
        return if (startDrawable != null && endDrawable != null && !startBitmap!!.sameAs(endBitmap)) {
            val overlay =
                if (useParentOverlay) (view.parent as ViewGroup).overlay else view.overlay
            if (fadeBehavior == FADE_BEHAVIOR_REVEAL) {
                overlay.add(endDrawable)
            }
            overlay.add(startDrawable)
            // The transition works by placing the end drawable under the start drawable and
            // gradually fading out the start drawable. So it's not really a cross-fade, but rather
            // a reveal of the end scene over time. Also, animate the bounds of both drawables
            // to mimic the change in the size of the view itself between scenes.
            val anim: ObjectAnimator
            anim = if (fadeBehavior == FADE_BEHAVIOR_OUT_IN) {
                // Fade out completely halfway through the transition
                ObjectAnimator.ofInt(startDrawable, "alpha", 255, 0, 0)
            } else {
                ObjectAnimator.ofInt(startDrawable, "alpha", 0)
            }
            anim.addUpdateListener { // TODO: some way to auto-invalidate views based on drawable changes? callbacks?
                view.invalidate(startDrawable.bounds)
            }
            var anim1: ObjectAnimator? = null
            if (fadeBehavior == FADE_BEHAVIOR_OUT_IN) {
                // start fading in halfway through the transition
                anim1 = ObjectAnimator.ofFloat(
                    view,
                    View.ALPHA,
                    0f,
                    0f,
                    1f
                )
            } else if (fadeBehavior == FADE_BEHAVIOR_CROSSFADE) {
                anim1 =
                    ObjectAnimator.ofFloat(view, View.ALPHA, 0f, 1f)
            }
            anim.addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    val overlay =
                        if (useParentOverlay) (view.parent as ViewGroup).overlay else view.overlay
                    overlay.remove(startDrawable)
                    if (fadeBehavior == FADE_BEHAVIOR_REVEAL) {
                        overlay.remove(endDrawable)
                    }
                }
            })
            val set = AnimatorSet()
            set.playTogether(anim)
            if (anim1 != null) {
                set.playTogether(anim1)
            }
            if (resizeBehavior == RESIZE_BEHAVIOR_SCALE && startBounds != endBounds) {

                val anim2: Animator = ObjectAnimator.ofObject(
                    startDrawable, "bounds",
                    sRectEvaluator, startBounds, endBounds
                )
                set.playTogether(anim2)
                if (resizeBehavior == RESIZE_BEHAVIOR_SCALE) {
                    // TODO: How to handle resizing with a CROSSFADE (vs. REVEAL) effect
                    // when we are animating the view directly?
                    val anim3: Animator = ObjectAnimator.ofObject(
                        endDrawable, "bounds",
                        sRectEvaluator, startBounds, endBounds
                    )
                    set.playTogether(anim3)
                }
            }
            set
        } else {
            null
        }
    }

    private fun captureValues(transitionValues: TransitionValues) {
        val view: View = transitionValues.view
        val bounds =
            Rect(0, 0, view.width, view.height)
        if (fadeBehavior != FADE_BEHAVIOR_REVEAL) {
            bounds.offset(view.left, view.top)
        }
        transitionValues.values.put(PROPNAME_BOUNDS, bounds)

        var bitmap = Bitmap.createBitmap(
            view.width, view.height,
            Bitmap.Config.ARGB_8888
        )
        if (view is TextureView) {
            bitmap = view.bitmap
        } else {
            val c = Canvas(bitmap)
            view.draw(c)
        }
        transitionValues.values.put(PROPNAME_BITMAP, bitmap)
        // TODO: I don't have resources, can't call the non-deprecated method?
        val drawable = BitmapDrawable(bitmap)
        // TODO: lrtb will be wrong if the view has transXY set
        drawable.bounds = bounds
        transitionValues.values.put(PROPNAME_DRAWABLE, drawable)
    }

    override fun captureStartValues(transitionValues: TransitionValues) {
        captureValues(transitionValues)
    }

    override fun captureEndValues(transitionValues: TransitionValues) {
        captureValues(transitionValues)
    }

    companion object {
        // TODO: Add a hook that lets a Transition call user code to query whether it should run on
        // a given target view. This would save bitmap comparisons in this transition, for example.
        private const val LOG_TAG = "Crossfade"
        private const val PROPNAME_BITMAP = "android:crossfade:bitmap"
        private const val PROPNAME_DRAWABLE = "android:crossfade:drawable"
        private const val PROPNAME_BOUNDS = "android:crossfade:bounds"
        private val sRectEvaluator =
            RectEvaluator()

        /**
         * Flag specifying that the fading animation should cross-fade
         * between the old and new representation of all affected target
         * views. This means that the old representation will fade out
         * while the new one fades in. This effect may work well on views
         * without solid backgrounds, such as TextViews.
         *
         * @see .setFadeBehavior
         */
        const val FADE_BEHAVIOR_CROSSFADE = 0

        /**
         * Flag specifying that the fading animation should reveal the
         * new representation of all affected target views. This means
         * that the old representation will fade out, gradually
         * revealing the new representation, which remains opaque
         * the whole time. This effect may work well on views
         * with solid backgrounds, such as ImageViews.
         *
         * @see .setFadeBehavior
         */
        const val FADE_BEHAVIOR_REVEAL = 1

        /**
         * Flag specifying that the fading animation should first fade
         * out the original representation completely and then fade in the
         * new one. This effect may be more suitable than the other
         * fade behaviors for views with.
         *
         * @see .setFadeBehavior
         */
        const val FADE_BEHAVIOR_OUT_IN = 2

        /**
         * Flag specifying that the transition should not animate any
         * changes in size between the old and new target views.
         * This means that no scaling will take place as a result of
         * this transition
         *
         * @see .setResizeBehavior
         */
        const val RESIZE_BEHAVIOR_NONE = 0

        /**
         * Flag specifying that the transition should animate any
         * changes in size between the old and new target views.
         * This means that the animation will scale the start/end
         * representations of affected views from the starting size
         * to the ending size over the course of the animation.
         * This effect may work well on images, but is not recommended
         * for text.
         *
         * @see .setResizeBehavior
         */
        const val RESIZE_BEHAVIOR_SCALE = 1
    }
}

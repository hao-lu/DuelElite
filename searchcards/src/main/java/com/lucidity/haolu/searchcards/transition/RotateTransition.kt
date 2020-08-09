package com.lucidity.haolu.searchcards.transition

import android.animation.Animator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Matrix
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ImageView.ScaleType
import androidx.transition.Transition
import androidx.transition.TransitionValues

class RotateTransition : Transition {
    constructor() {}

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {}

    private fun captureValues(transitionValues: TransitionValues) {
        val view = transitionValues.view
        if (view !is ImageView || view.getVisibility() != View.VISIBLE) {
            return
        }
        val imageView = view
        val drawable = imageView.drawable ?: return
        val values =
            transitionValues.values
        val left = view.getLeft()
        val top = view.getTop()
        val right = view.getRight()
        val bottom = view.getBottom()
        val bounds = Rect(left, top, right, bottom)
        values[PROPNAME_BOUNDS] = bounds
        values[PROPNAME_MATRIX] =
            copyImageMatrix(
                imageView
            )
    }

    override fun captureStartValues(transitionValues: TransitionValues) {
        captureValues(transitionValues)
    }

    override fun captureEndValues(transitionValues: TransitionValues) {
        captureValues(transitionValues)
    }

    override fun getTransitionProperties(): Array<String>? {
        return sTransitionProperties
    }

    /**
     * Creates an Animator for ImageViews moving, changing dimensions, and/or changing
     * [android.widget.ImageView.ScaleType].
     *
     * @param sceneRoot   The root of the transition hierarchy.
     * @param startValues The values for a specific target in the start scene.
     * @param endValues   The values for the target in the end scene.
     * @return An Animator to move an ImageView or null if the View is not an ImageView,
     * the Drawable changed, the View is not VISIBLE, or there was no change.
     */
    override fun createAnimator(
        sceneRoot: ViewGroup,
        startValues: TransitionValues?,
        endValues: TransitionValues?
    ): Animator? {
        if (startValues == null || endValues == null) {
            return null
        }
        val startBounds =
            startValues.values[PROPNAME_BOUNDS] as Rect?
        val endBounds =
            endValues.values[PROPNAME_BOUNDS] as Rect?
        if (startBounds == null || endBounds == null) {
            return null
        }
        val startMatrix =
            startValues.values[PROPNAME_MATRIX] as Matrix?
        val endMatrix =
            endValues.values[PROPNAME_MATRIX] as Matrix?
        val matricesEqual = (startMatrix == null && endMatrix == null
                || startMatrix != null && startMatrix == endMatrix)
        if (startBounds == endBounds && matricesEqual) {
            return null
        }
        val imageView = endValues.view as ImageView
        val drawable = imageView.drawable
        val drawableWidth = drawable.intrinsicWidth
        val drawableHeight = drawable.intrinsicHeight

//        ObjectAnimator animator;
//        if (drawableWidth <= 0 || drawableHeight <= 0) {
//            animator = createNullAnimator(imageView);
//        } else {
//            if (startMatrix == null) {
//                startMatrix = MatrixUtils.IDENTITY_MATRIX;
//            }
//            if (endMatrix == null) {
//                endMatrix = MatrixUtils.IDENTITY_MATRIX;
//            }
//            ANIMATED_TRANSFORM_PROPERTY.set(imageView, startMatrix);
//            animator = createMatrixAnimator(imageView, startMatrix, endMatrix);
//        }
        return ValueAnimator.ofFloat(0f, 1.0f)
    }

    companion object {
        private const val PROPNAME_MATRIX = "android:changeImageTransform:matrix"
        private const val PROPNAME_BOUNDS = "android:changeImageTransform:bounds"
        private val sTransitionProperties = arrayOf(
            PROPNAME_MATRIX,
            PROPNAME_BOUNDS
        )
//        private val NULL_MATRIX_EVALUATOR: TypeEvaluator<Matrix> =
//            TypeEvaluator<Matrix?> { fraction, startValue, endValue -> null }
//        private val ANIMATED_TRANSFORM_PROPERTY: Property<ImageView, Matrix> =
//            object : Property<ImageView?, Matrix?>(
//                Matrix::class.java, "animatedTransform"
//            ) {
//                override fun set(
//                    view: ImageView?,
//                    matrix: Matrix?
//                ) {
////                    ImageViewUtils.animateTransform(view, matrix);
//                }
//
//                override fun get(`object`: ImageView?): Matrix? {
//                    return null
//                }
//            }

        //    @NonNull
        //    private ObjectAnimator createNullAnimator(@NonNull ImageView imageView) {
        //        return ObjectAnimator.ofObject(imageView, ANIMATED_TRANSFORM_PROPERTY,
        //                NULL_MATRIX_EVALUATOR, MatrixUtils.IDENTITY_MATRIX, MatrixUtils.IDENTITY_MATRIX);
        //    }
        //
        //    private ObjectAnimator createMatrixAnimator(final ImageView imageView, Matrix startMatrix,
        //                                                final Matrix endMatrix) {
        //        return ObjectAnimator.ofObject(imageView, ANIMATED_TRANSFORM_PROPERTY,
        //                new TransitionUtils.MatrixEvaluator(), startMatrix, endMatrix);
        //    }
        private fun copyImageMatrix(view: ImageView): Matrix {
            val image = view.drawable
            if (image.intrinsicWidth > 0 && image.intrinsicHeight > 0) {
                when (view.scaleType) {
                    ScaleType.FIT_XY -> return fitXYMatrix(
                        view
                    )
                    ScaleType.CENTER_CROP -> return centerCropMatrix(
                        view
                    )
                }
            }
            return Matrix(view.imageMatrix)
        }

        /**
         * Calculates the image transformation matrix for an ImageView with ScaleType FIT_XY. This
         * needs to be manually calculated as the platform does not give us the value for this case.
         */
        private fun fitXYMatrix(view: ImageView): Matrix {
            val image = view.drawable
            val matrix = Matrix()
            matrix.postScale(
                view.width.toFloat() / image.intrinsicWidth,
                view.height.toFloat() / image.intrinsicHeight
            )
            return matrix
        }

        /**
         * Calculates the image transformation matrix for an ImageView with ScaleType CENTER_CROP. This
         * needs to be manually calculated for consistent behavior across all the API levels.
         */
        private fun centerCropMatrix(view: ImageView): Matrix {
            val image = view.drawable
            val imageWidth = image.intrinsicWidth
            val imageViewWidth = view.width
            val scaleX = imageViewWidth.toFloat() / imageWidth
            val imageHeight = image.intrinsicHeight
            val imageViewHeight = view.height
            val scaleY = imageViewHeight.toFloat() / imageHeight
            val maxScale = Math.max(scaleX, scaleY)
            val width = imageWidth * maxScale
            val height = imageHeight * maxScale
            val tx = Math.round((imageViewWidth - width) / 2f)
            val ty = Math.round((imageViewHeight - height) / 2f)
            val matrix = Matrix()
            matrix.postScale(maxScale, maxScale)
            matrix.postTranslate(tx.toFloat(), ty.toFloat())
            return matrix
        }
    }
}
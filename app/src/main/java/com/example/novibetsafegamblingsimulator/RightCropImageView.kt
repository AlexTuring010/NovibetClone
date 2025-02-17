package com.example.novibetsafegamblingsimulator

import android.content.Context
import android.graphics.Matrix
import android.util.AttributeSet
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.shape.ShapeAppearanceModel

class RightCropImageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ShapeableImageView(context, attrs, defStyleAttr) {

    init {
        scaleType = ScaleType.MATRIX // Ensures we always use MATRIX scaling

        // Apply rounded corners
        shapeAppearanceModel = ShapeAppearanceModel.builder(
            context,
            attrs,
            defStyleAttr,
            0
        ).setAllCornerSizes(resources.getDimension(R.dimen.corner_radius)).build()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        adjustImageMatrix()
    }

    private fun adjustImageMatrix() {
        val drawable = drawable ?: return

        val imageWidth = drawable.intrinsicWidth
        val imageHeight = drawable.intrinsicHeight

        val viewWidth = width.toFloat()
        val viewHeight = height.toFloat()

        if (imageWidth <= 0 || imageHeight <= 0 || viewWidth <= 0 || viewHeight <= 0) {
            return
        }

        val scale = viewHeight / imageHeight // Scale to fit height
        val scaledWidth = imageWidth * scale

        var dx = viewWidth - scaledWidth
        val dy = 0f // Align to top

        // Ensure dx is negative or zero (crop left side)
        if (dx > 0) {
            dx = 0f
        }

        val matrix = Matrix()
        matrix.setScale(scale, scale)
        matrix.postTranslate(dx, dy)

        imageMatrix = matrix
    }
}
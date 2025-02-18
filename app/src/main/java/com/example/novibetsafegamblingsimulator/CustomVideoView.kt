package com.example.novibetsafegamblingsimulator

import android.content.Context
import android.util.AttributeSet
import android.widget.VideoView

class CustomVideoView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : VideoView(context, attrs, defStyleAttr) {

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val defaultWidth = getDefaultSize(0, widthMeasureSpec)
        val defaultHeight = getDefaultSize(0, heightMeasureSpec)
        val aspectRatio = 16f / 9f // Adjust this to match your video's aspect ratio

        val width: Int
        val height: Int

        if (defaultWidth < defaultHeight * aspectRatio) {
            width = (defaultHeight * aspectRatio).toInt()
            height = defaultHeight
        } else {
            width = defaultWidth
            height = (defaultWidth / aspectRatio).toInt()
        }

        setMeasuredDimension(width, height)
    }
}
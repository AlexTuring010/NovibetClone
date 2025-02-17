package com.example.novibetsafegamblingsimulator

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat

class CustomTabLinesView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var tabCount: Int = 0
    private var currentTab: Int = 0
    private var progress: Float = 0f
    private val spacing: Float = 16f // Space between tab lines in pixels

    private val paintGrey = Paint().apply {
        color = ContextCompat.getColor(context, R.color.unselected_tab_color)
        style = Paint.Style.FILL
    }

    private val paintBlue = Paint().apply {
        color = ContextCompat.getColor(context, R.color.selected_tab_color)
        style = Paint.Style.FILL
    }

    fun setTabCount(count: Int) {
        tabCount = count
        invalidate()
    }

    fun setCurrentTab(tab: Int) {
        currentTab = tab
        progress = 0f
        invalidate()
    }

    fun setProgress(progress: Float) {
        this.progress = progress
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (tabCount == 0) return

        val totalSpacing = spacing * (tabCount - 1)
        val tabWidth = (width - totalSpacing) / tabCount.toFloat()
        for (i in 0 until tabCount) {
            val left = i * (tabWidth + spacing)
            val right = left + tabWidth
            canvas.drawRect(left, 0f, right, height.toFloat(), paintGrey)
            if (i == currentTab) {
                canvas.drawRect(left, 0f, left + tabWidth * progress, height.toFloat(), paintBlue)
            }
        }
    }
}
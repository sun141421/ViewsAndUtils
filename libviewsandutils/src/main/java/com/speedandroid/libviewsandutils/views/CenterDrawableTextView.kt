package com.speedandroid.libviewsandutils.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.Gravity
import androidx.annotation.IntDef
import androidx.appcompat.widget.AppCompatTextView
import com.speedandroid.libviewsandutils.R

/**
 * 解决drawable不与文字居中的问题
 * */
class CenterDrawableTextView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    AppCompatTextView(context, attrs, defStyleAttr) {


    private var drawables = arrayOfNulls<Drawable?>(4)
    private var widths: IntArray
    private var heights: IntArray

    companion object {
        const val LEFT = 0
        const val TOP = 1
        const val RIGHT = 2
        const val BOTTOM = 3
    }

    @IntDef(value = [LEFT, TOP, RIGHT, BOTTOM])
    @Retention(AnnotationRetention.SOURCE)
    annotation class DrawGravity


    init {
        widths = IntArray(4)
        heights = IntArray(4)
        gravity = Gravity.CENTER
        val array = context.obtainStyledAttributes(attrs, R.styleable.CenterDrawableTextView)
        drawables[LEFT] = array.getDrawable(R.styleable.CenterDrawableTextView_leftDrawable)
        drawables[TOP] = array.getDrawable(R.styleable.CenterDrawableTextView_topDrawable)
        drawables[RIGHT] = array.getDrawable(R.styleable.CenterDrawableTextView_rightDrawable)
        drawables[BOTTOM] = array.getDrawable(R.styleable.CenterDrawableTextView_bottomDrawable)
        val defaultLeftWidth = drawables[LEFT]?.intrinsicWidth ?: 0
        widths[LEFT] = array.getDimensionPixelSize(R.styleable.CenterDrawableTextView_leftDrawableWidth, defaultLeftWidth)
        val defaultTopWidth = drawables[TOP]?.intrinsicWidth ?: 0
        widths[TOP] = array.getDimensionPixelSize(R.styleable.CenterDrawableTextView_topDrawableWidth, defaultTopWidth)
        val defaultRightWidth = drawables[RIGHT]?.intrinsicWidth ?: 0
        widths[RIGHT] = array.getDimensionPixelSize(R.styleable.CenterDrawableTextView_rightDrawableWidth, defaultRightWidth)
        val defaultBottomWidth = drawables[BOTTOM]?.intrinsicWidth ?: 0
        widths[BOTTOM] = array.getDimensionPixelSize(R.styleable.CenterDrawableTextView_bottomDrawableWidth, defaultBottomWidth)
        val defaultLeftHeight = drawables[LEFT]?.intrinsicHeight ?: 0
        heights[LEFT] = array.getDimensionPixelSize(R.styleable.CenterDrawableTextView_leftDrawableHeight, defaultLeftHeight)
        val defaultTopHeight = drawables[TOP]?.intrinsicHeight ?: 0
        heights[TOP] = array.getDimensionPixelSize(R.styleable.CenterDrawableTextView_topDrawableHeight, defaultTopHeight)
        val defaultRightHeight = drawables[RIGHT]?.intrinsicHeight ?: 0
        heights[RIGHT] = array.getDimensionPixelSize(R.styleable.CenterDrawableTextView_rightDrawableHeight, defaultRightHeight)
        val defaultBottomHeight = drawables[BOTTOM]?.intrinsicHeight ?: 0
        heights[BOTTOM] = array.getDimensionPixelSize(R.styleable.CenterDrawableTextView_bottomDrawableHeight, defaultBottomHeight)
        array.recycle()
    }


    fun setDrawable(@DrawGravity gravity: Int, drawable: Drawable?, width: Int, height: Int) {
        drawables[gravity] = drawable
        widths[gravity] = width
        heights[gravity] = height
        postInvalidate()
    }

    fun setDrawables(drawables: Array<Drawable?>?, widths: IntArray?, heights: IntArray?) {
        if (drawables != null && drawables.size >= 4 && widths != null && widths.size >= 4 && heights != null && heights.size >= 4) {
            this.drawables = drawables
            this.widths = widths
            this.heights = heights
            postInvalidate()
        }
    }

    override fun onDraw(canvas: Canvas) {
        val drawablePadding = compoundDrawablePadding
        translateText(canvas, drawablePadding)
        super.onDraw(canvas)
        val centerX = ((width + paddingLeft - paddingRight) / 2).toFloat()
        val centerY = ((height + paddingTop - paddingBottom) / 2).toFloat()
        val halfTextWidth = paint.measureText((text?.toString() ?: "").ifEmpty { hint?.toString() ?: "" }) / 2
        val fontMetrics = paint.fontMetrics
        val halfTextHeight = (fontMetrics.descent - fontMetrics.ascent) / 2
        if (drawables[LEFT] != null) {
            val left = (centerX - drawablePadding - halfTextWidth - widths[LEFT]).toInt()
            val top = (centerY - heights[LEFT] / 2).toInt()
            drawables[LEFT]!!.setBounds(
                left,
                top,
                left + widths[LEFT],
                top + heights[LEFT]
            )
            canvas.save()
            drawables[LEFT]!!.draw(canvas)
            canvas.restore()
        }
        if (drawables[RIGHT] != null) {
            val left = (centerX + halfTextWidth + drawablePadding).toInt()
            val top = (centerY - heights[RIGHT] / 2).toInt()
            drawables[RIGHT]!!.setBounds(
                left,
                top,
                left + widths[RIGHT],
                top + heights[RIGHT]
            )
            canvas.save()
            drawables[RIGHT]!!.draw(canvas)
            canvas.restore()
        }
        if (drawables[TOP] != null) {
            val left = (centerX - widths[TOP] / 2).toInt()
            val bottom = (centerY - halfTextHeight - drawablePadding).toInt()
            drawables[TOP]!!.setBounds(
                left,
                bottom - heights[TOP],
                left + widths[TOP],
                bottom
            )
            canvas.save()
            drawables[TOP]!!.draw(canvas)
            canvas.restore()
        }
        if (drawables[BOTTOM] != null) {
            val left = (centerX - widths[3] / 2).toInt()
            val top = (centerY + halfTextHeight + drawablePadding).toInt()
            drawables[BOTTOM]!!.setBounds(
                left,
                top,
                left + widths[BOTTOM],
                top + heights[BOTTOM]
            )
            canvas.save()
            drawables[BOTTOM]!!.draw(canvas)
            canvas.restore()
        }
    }

    private fun translateText(canvas: Canvas, drawablePadding: Int) {
        var translateWidth = 0
        if (drawables[LEFT] != null && drawables[RIGHT] != null) {
            translateWidth = (widths[LEFT] - widths[RIGHT]) / 2
        } else if (drawables[LEFT] != null) {
            translateWidth = (widths[LEFT] + drawablePadding) / 2
        } else if (drawables[RIGHT] != null) {
            translateWidth = -(widths[RIGHT] + drawablePadding) / 2
        }
        var translateHeight = 0
        if (drawables[TOP] != null && drawables[BOTTOM] != null) {
            translateHeight = (heights[TOP] - heights[BOTTOM]) / 2
        } else if (drawables[TOP] != null) {
            translateHeight = (heights[TOP] + drawablePadding) / 2
        } else if (drawables[BOTTOM] != null) {
            translateHeight = -(heights[BOTTOM] - drawablePadding) / 2
        }
        canvas.translate(translateWidth.toFloat(), translateHeight.toFloat())
    }

    override fun drawableStateChanged() {
        super.drawableStateChanged()
        for (drawable in drawables) {
            drawable?.setState(drawableState)
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        var requireWidth = measuredWidth
        if (drawables[LEFT] != null) {
            requireWidth += widths[LEFT] + compoundDrawablePadding
        }
        if (drawables[RIGHT] != null) {
            requireWidth += widths[RIGHT] + compoundDrawablePadding
        }
        val newWidthMeasureSpec = resolveSizeAndState(requireWidth, widthMeasureSpec, 1)

        var requireHeight = measuredHeight
        if (drawables[TOP] != null) {
            requireHeight += heights[TOP] + compoundDrawablePadding
        }
        if (drawables[BOTTOM] != null) {
            requireHeight += heights[BOTTOM] + compoundDrawablePadding
        }

        val newHeightMeasureSpec = resolveSizeAndState(requireHeight, heightMeasureSpec, 1)
        setMeasuredDimension(newWidthMeasureSpec, newHeightMeasureSpec)
    }
}
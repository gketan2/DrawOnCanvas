package com.k10.draw

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.view.drawToBitmap

class DrawingCanvas constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {

    private var latestPath: Path
    private var latestPaint: Paint

    private var drawingPath: ArrayList<Path> = ArrayList()
    private var drawingPaint: ArrayList<Paint> = ArrayList()

    private var numberOfPaths: Int = 0

    init {
        latestPath = Path()
        latestPaint = Paint()
        latestPaint.strokeWidth = 15f
        latestPaint.isAntiAlias = true
        latestPaint.isDither = true
        latestPaint.style = Paint.Style.STROKE
        latestPaint.strokeJoin = Paint.Join.MITER
        latestPaint.strokeCap = Paint.Cap.ROUND
        latestPaint.color = 0xFF000000.toInt()
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        event?.let {
            when (it.action) {
                MotionEvent.ACTION_DOWN -> {
                    latestPath = Path()
                    //adding the new path to the list
                    drawingPath.add(latestPath)
                    drawingPaint.add(latestPaint)
                    numberOfPaths++
                    //changing the last path
                    drawingPath[numberOfPaths - 1].moveTo(it.x, it.y)
                }
                MotionEvent.ACTION_MOVE -> {
                    //drawingPath[numberOfPathInList - 1].lineTo(it.x, it.y)
                    latestPath.lineTo(it.x, it.y)
                }
                MotionEvent.ACTION_UP -> {
                    //line is complete
                }
                else -> {
                }
            }
        }
        invalidate()
        return true
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        //canvas?.drawColor(0xFFFF0000.toInt()) // setBackgroundColor of canvas
        for (x in 0 until numberOfPaths) {
            canvas?.drawPath(drawingPath[x], drawingPaint[x])
        }
    }

    fun reset() {
        drawingPath.clear()
        drawingPaint.clear()
        numberOfPaths = 0
        invalidate()
    }

    fun undo(): Int{
        if(numberOfPaths > 0){
            numberOfPaths--
            drawingPath.removeAt(numberOfPaths)
            drawingPaint.removeAt(numberOfPaths)
            invalidate()
        }
        return numberOfPaths
    }

    fun isEmpty(): Boolean {
        return numberOfPaths <= 0
    }
    fun changePaint(width: Float) {
        val p = Paint()
        p.isAntiAlias = true
        p.isDither = true
        p.style = Paint.Style.STROKE
        p.strokeWidth = width
        p.strokeJoin = Paint.Join.MITER
        p.strokeCap = Paint.Cap.ROUND
        p.color = 0xFF000000.toInt()
        latestPaint = p
    }

    fun getAllPath(): ArrayList<Path> {
        return drawingPath
    }

    fun getBitmap(): Bitmap? {
        if(numberOfPaths <= 0)
            return null

        return drawToBitmap()
    }
}
package com.k10.draw

import android.graphics.Paint
import android.graphics.Path
import androidx.lifecycle.ViewModel

class DrawingActivityViewModel: ViewModel() {
    var drawingPath: ArrayList<Path> = ArrayList()
    var drawingPaint: ArrayList<Paint> = ArrayList()

    var latestPath: Path = Path()
    var latestPaint: Paint = Paint().apply {
        strokeWidth = 15f
        isAntiAlias = true
        isDither = true
        style = Paint.Style.STROKE
        strokeJoin = Paint.Join.MITER
        strokeCap = Paint.Cap.ROUND
        color = 0xFF000000.toInt()
    }
    var canvasBackgroundColor: Int = 0xFFFFFFFF.toInt()
}
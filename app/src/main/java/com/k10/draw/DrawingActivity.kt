package com.k10.draw

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.ImageButton
import android.widget.PopupWindow
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import com.github.dhaval2404.colorpicker.ColorPickerDialog
import com.github.dhaval2404.colorpicker.model.ColorShape
import kotlinx.android.synthetic.main.activity_drawing.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DrawingActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var viewModel: DrawingActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_drawing)

//        viewModel = ViewModelProvider(this)[DrawingActivityViewModel::class.java]

//        savedInstanceState?.let{
//            if(it.getBoolean("DRAW", false)){
//                canvas.setAllPaint(viewModel.drawingPaint)
//                canvas.setAllPath(viewModel.drawingPath)
//                canvas.setCanvasBackgroundColor(viewModel.canvasBackgroundColor)
//                canvas.setLatestPaint(viewModel.latestPaint)
//                canvas.setLatestPath(viewModel.latestPath)
//                canvas.invalidate()
//            }
//        }

        resetButton.setOnClickListener(this)
        widthButton.setOnClickListener(this)
        undoButton.setOnClickListener(this)
        saveButton.setOnClickListener(this)
        penColorButton.setOnClickListener(this)
        backgroundColorButton.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.resetButton -> {
                canvas.reset()
                undoButton.setImageResource(R.drawable.ic_undo_disabled)
                DrawableCompat.setTint(backgroundColorButton.drawable, 0xFFFFFF)
            }
            R.id.widthButton -> {
                selectWidthPopUp()
            }
            R.id.undoButton -> {
                if (canvas.undo() == 0) {
                    undoButton.setImageResource(R.drawable.ic_undo_disabled)
                } else {
                    undoButton.setImageResource(R.drawable.ic_undo)
                }
            }
            R.id.saveButton -> {
                saveCanvasToStorage()
            }
            R.id.penColorButton -> {
                colorPicker("PEN")
            }
            R.id.backgroundColorButton -> {
                colorPicker("BACKGROUND")
            }
        }
    }

//    override fun onSaveInstanceState(outState: Bundle) {
//        viewModel.drawingPaint = canvas.getAllPaint()
//        viewModel.drawingPath = canvas.getAllPath()
//        viewModel.canvasBackgroundColor = canvas.getCanvasBackgroundColor()
//        viewModel.latestPaint = canvas.getLatestPaint()
//        viewModel.latestPath = canvas.getLatestPath()
//        outState.putBoolean("DRAW", true)
//        super.onSaveInstanceState(outState)
//    }

    private fun selectWidthPopUp() {

        val inflater: LayoutInflater = getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val v: View = inflater.inflate(R.layout.width_select_popup, null)
        val popupWindow = PopupWindow(v, WRAP_CONTENT, WRAP_CONTENT, true)

        popupWindow.showAtLocation(widthButton, Gravity.NO_GRAVITY, widthButton.x.toInt(), 3*widthButton.height/2)

        v.findViewById<ImageButton>(R.id.widthSmall).setOnClickListener {
            canvas.changePaint(10f)
            popupWindow.dismiss()
        }
        v.findViewById<ImageButton>(R.id.widthMedium).setOnClickListener {
            canvas.changePaint(15f)
            popupWindow.dismiss()
        }
        v.findViewById<ImageButton>(R.id.widthLarge).setOnClickListener {
            canvas.changePaint(20f)
            popupWindow.dismiss()
        }
    }

    private fun colorPicker(type: String) {
        ColorPickerDialog.Builder(this)
            .setColorShape(ColorShape.CIRCLE)
            .setDefaultColor(0xFFFFFF)
            .setColorListener { color, _ ->
                if (type == "PEN"){
                    canvas.changePaint(color = color)
                    DrawableCompat.setTint(penColorButton.drawable, color)
                }else if (type == "BACKGROUND"){
                    canvas.changeBackgroundColor(color)
                    DrawableCompat.setTint(backgroundColorButton.drawable, color)
                }
            }
            .show()
    }

    private fun saveCanvasToStorage() {
        if (isReadWritePermissionGranted()) {
            canvas.getBitmap()?.let {
                CoroutineScope(Dispatchers.IO).launch {
                    val result = FileIO.saveFile(this@DrawingActivity, it)
                    withContext(Dispatchers.Main) {
                        if (result) {
                            Toast.makeText(
                                this@DrawingActivity,
                                "File Saved....",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            Toast.makeText(
                                this@DrawingActivity,
                                "Something Went Wrong",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
                return
            }
            Toast.makeText(this, "Nothing to save", Toast.LENGTH_SHORT).show()
        } else {
            askReadWritePermission()
        }
    }

    private fun isReadWritePermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun askReadWritePermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ),
            100
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == 100) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED
                && grantResults[1] == PackageManager.PERMISSION_GRANTED
            ) {
                saveCanvasToStorage()
            }
        }
    }
}
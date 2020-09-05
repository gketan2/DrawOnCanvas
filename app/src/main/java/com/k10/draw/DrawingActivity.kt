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
import kotlinx.android.synthetic.main.activity_drawing.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DrawingActivity : AppCompatActivity(), View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_drawing)

        resetButton.setOnClickListener(this)
        widthButton.setOnClickListener(this)
        undoButton.setOnClickListener(this)
        saveButton.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.resetButton -> {
                canvas.reset()
                undoButton.setImageResource(R.drawable.ic_undo_disabled)
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
        }
    }

    private fun selectWidthPopUp() {

        val inflater: LayoutInflater = getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val v: View = inflater.inflate(R.layout.width_select_popup, null)
        val popupWindow = PopupWindow(v, WRAP_CONTENT, WRAP_CONTENT, true)

        popupWindow.showAtLocation(widthButton, Gravity.NO_GRAVITY, 0, widthButton.height)

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

    private fun saveCanvasToStorage() {
        if (isReadWritePermissionGranted()) {
            canvas.getBitmap()?.let {
                CoroutineScope(Dispatchers.IO).launch {
                    val result = FileIO.saveFile(this@DrawingActivity, it)
                    withContext(Dispatchers.Main){
                        if(result){
                            Toast.makeText(this@DrawingActivity, "File Saved....", Toast.LENGTH_SHORT).show()
                        }else{
                            Toast.makeText(this@DrawingActivity, "Something Went Wrong", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
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
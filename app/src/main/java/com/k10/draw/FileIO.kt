package com.k10.draw

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.*

class FileIO {

    companion object {

        suspend fun saveFile(context: Context, bitmap: Bitmap): Boolean {

            val time = System.currentTimeMillis()

            val formatter = SimpleDateFormat("yyyyMMdd_hhmmss", Locale.ENGLISH)
            val fileName = "Draw_${formatter.format(Date())}"

            val resolver = context.contentResolver
            if (Build.VERSION.SDK_INT >= 29) {
                val values = ContentValues().apply {
                    put(MediaStore.Images.Media.MIME_TYPE, "image/png")
                    put(MediaStore.Images.Media.DATE_ADDED, time / 1000)
                    put(MediaStore.Images.Media.DATE_TAKEN, time)
                    put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/Draw")
                    put(MediaStore.Images.Media.IS_PENDING, true)
                }
                val uri: Uri? = resolver.insert(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    values
                )
                uri?.let {
                    saveBitmapToStream(bitmap, resolver.openOutputStream(it))
                    values.put(MediaStore.Images.Media.IS_PENDING, false)
                    resolver.update(uri, values, null, null)
                }
            } else {
                val directory = File(Environment.getExternalStorageDirectory().toString() + "/Pictures/Draw")

                if (!directory.exists()) {
                    directory.mkdirs()
                }

                val file = File(directory, "${fileName}.png")

                println("----------------------"+file.path)

                saveBitmapToStream(bitmap, FileOutputStream(file))

                val values = ContentValues().apply{
                    put(MediaStore.Images.Media.DATA, file.absolutePath)
                }

                resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
            }

            return true
        }

        private fun saveBitmapToStream(bitmap: Bitmap, outputStream: OutputStream?) {
            if (outputStream != null) {
                try {
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                    outputStream.close()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }
}